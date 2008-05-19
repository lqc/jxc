package org.lqc.jxc.transform;

import java.util.Stack;

import org.lqc.jxc.CompilerException;
import org.lqc.jxc.SyntaxErrorException;
import org.lqc.jxc.TypeCheckException;
import org.lqc.jxc.tokens.ArgumentDecl;
import org.lqc.jxc.tokens.AssignmentInstr;
import org.lqc.jxc.tokens.CompileUnit;
import org.lqc.jxc.tokens.ComplexInstr;
import org.lqc.jxc.tokens.CondInstr;
import org.lqc.jxc.tokens.ConstantExpr;
import org.lqc.jxc.tokens.EmptyInstruction;
import org.lqc.jxc.tokens.Expression;
import org.lqc.jxc.tokens.FunctionCall;
import org.lqc.jxc.tokens.FunctionDecl;
import org.lqc.jxc.tokens.IncrementInstr;
import org.lqc.jxc.tokens.Instruction;
import org.lqc.jxc.tokens.LoopInstr;
import org.lqc.jxc.tokens.NullExpression;
import org.lqc.jxc.tokens.ReturnInstr;
import org.lqc.jxc.tokens.TreeVisitor;
import org.lqc.jxc.tokens.VarDecl;
import org.lqc.jxc.tokens.VarExpr;
import org.lqc.jxc.types.FunctionType;
import org.lqc.jxc.types.PrimitiveType;
import org.lqc.jxc.types.Type;
import org.lqc.util.ElementNotFoundException;
import org.lqc.util.NonUniqueElementException;
import org.lqc.util.Relation;

/**
 * 
 * Class is responsible for static context analysis. This includes:
 * 
 * <ul>
 * <li>Checking scope for variable/method visibility</li>
 * <li>Type checking</li>
 * </ul>
 * 
 */
public class ScopeAnalyzer implements TreeVisitor {

	private Stack<Context> envStack;
	private Context current;

	private void push(Context ctx) {
		envStack.push(ctx);
		current = ctx;
	}

	private void pop() {
		current = envStack.pop();
	}

	public ScopeAnalyzer(Context topContext) {
		envStack = new Stack<Context>();
		push(topContext);
	}

	public void visit(CompileUnit prog) 
	{	
		prog.bindStaticContext(current);
		Context ctx = new Context(current, "<global>");		

		for (FunctionDecl f : prog.getFunctions()) {
			try {
				ctx.put(f);
			} catch (NonUniqueElementException e) {
				throw new SyntaxErrorException(f,
						"Function redeclaration\n");
			}
		}

		push(ctx);

		for (FunctionDecl f : prog.getFunctions()) {
			f.bindStaticContext(ctx);
			f.visitNode(this);
		}

	}

	public void visit(ArgumentDecl decl) {
		try {
			current.put(decl);
		} catch (NonUniqueElementException e) {
			throw new SyntaxErrorException(decl, String.format(
					"Function argument '%s' redeclaration\n", decl
							.getID()));
		}
	}
	
	public void visit(FunctionDecl decl) {
		decl.initInnerContext(decl.getStaticContext());
		push(decl.innerContext());		
		
		int localVars = 0;

		try {
			current
					.put(new VarDecl(decl.getType().getReturnType(), 
							Context.RETURN_ID) );
		} catch (NonUniqueElementException e) {
			throw new CompilerException(
					"[INTERNAL] Return value reference already in scope. Impossible ?",
					e);
		}

		/* process arguments */
		for (ArgumentDecl d : decl.getArgs()) {
			localVars++;
			d.bindStaticContext(current);
			d.visitNode(this);
		}

		/*
		 * process instructions - don't create context for body
		 */
		decl.getBody().bindStaticContext(current);
		
		for (Instruction i : decl.getBody()) {
			i.bindStaticContext(current);
			i.visitNode(this);
			
			if (i instanceof VarDecl)
				localVars++;

			if (i instanceof Expression) {
				Expression e = (Expression) i;
				/* check if expression returns void */
				if (!e.getType().compareTo(Type.VOID).equals(Relation.EQUAL)) {
					throw new TypeCheckException(e,
							"Instruction must be 'void' type");
				}
			}		
		}						

		pop();
	}

	public void visit(VarDecl decl) {
		/* declarations are not recursive */
		Expression e = decl.getInitialValue();

		e.visitNode(this);
		switch (decl.getType().compareTo(e.getType())) {
		case EQUAL:
		case GREATER:
			/* ok */
			break;
		default:
			throw new TypeCheckException(
					decl,
					String.format(
						"Can't initialize variable '%s' of type '%s' with '%s' type expression",
						decl.getID(), decl.getType(), e.getType()));
		}

		try {
			current.put(decl);
		} catch (NonUniqueElementException ex) {
			throw new SyntaxErrorException(decl, "Multiply declaration of"
					+ "variable: " + decl.getID());
		}
	}

	public void visit(FunctionCall call) {
		Type[] types = new Type[call.getArgs().size()];
		int i = 0;

		for (Expression e : call.getArgs()) {
			e.bindStaticContext(current);
			e.visitNode(this);
			types[i++] = e.getType();
		}

		FunctionType alpha = new FunctionType(Type.ANY, types);

		try {
			FunctionDecl d = current.getFunction(call.getFid(), alpha);
			call.bindRef(d);
		} catch (ElementNotFoundException e) {
			throw new SyntaxErrorException(call, String.format(
					"No match for '%s' with type '%s'\n", call.getFid(), alpha
							.toString()));
		}
	}

	public void visit(ConstantExpr c) {
		// nothing to do
	}

	public void visit(VarExpr e) {
		try {
			VarDecl d = current.getVariable(e.getId());
			e.bindRef(d);
		} catch (ElementNotFoundException exc) {
			throw new SyntaxErrorException(e, 
				String.format("No match for variable '%s' in current scope.\n", e
							.getId()));
		}
	}

	public void visit(ComplexInstr instr) {
		push(new Context(current, "block"));

		for (Instruction i : instr) {
			i.bindStaticContext(current);
			i.visitNode(this);
		}

		pop();
	}

	public void visit(AssignmentInstr instr) {
		try {
			VarDecl d = current.getVariable(instr.getId());
			instr.setRef(d);

			Expression e = instr.getValue();
			e.visitNode(this);
			switch (e.getType().compareTo(d.getType())) {
			case EQUAL:
			case LESSER:
				/* Type ok */
				break;
			default:
				throw new TypeCheckException(instr, String.format(
						"Cannot assign expression of type '%s' to"
								+ " variable '%s' of type '%s'.", e.getType()
								.toString(), instr.getId(), d.getType()
								.toString()));
			}
		} catch (ElementNotFoundException e) {
			throw new SyntaxErrorException(instr, String.format(
					"No match for variable '%s' in current scope.\n", instr
							.getId()));
		}
	}
	
	public void visit(IncrementInstr instr) {
		VarDecl d;
		
		try {
			d = current.getVariable(instr.getId());
			instr.setRef(d);
		} catch (ElementNotFoundException e) {
			throw new SyntaxErrorException(instr, String.format(
				"No match for variable '%s' in current scope.\n", instr
						.getId()));			
		}
			
		/* match increment */
		FunctionType ft = new FunctionType(Type.VOID, d.getType(), PrimitiveType.INT);
		FunctionDecl fd;
			
		try {
			fd = current.getFunction("_INC", ft);
			
		} catch (ElementNotFoundException e) {
			/* the type has no defined _INC function */								
			ConstantExpr ce = new ConstantExpr(
					instr.getLine(), instr.getColumn(),
					new Integer(instr.getChange()) );
			ce.visitNode(this);
				
			VarExpr ve = new VarExpr(
					instr.getLine(), instr.getColumn(), d.getID());
			ve.visitNode(this);
				
			FunctionCall fc = new FunctionCall(
					instr.getLine(), instr.getColumn(), "_ADD", ce, ve);
			fc.visitNode(this);
			fd = fc.getRef();
		}	
		
		instr.setAction(fd);		
	}

	public void visit(LoopInstr loop) {
		loop.getInitInstr().visitNode(this);
		Expression e = loop.getCondition();

		/* calculate type */
		e.bindStaticContext(current);
		e.visitNode(this);
		switch (PrimitiveType.BOOLEAN.compareTo(e.getType())) {
		case EQUAL:
		case LESSER:
			/* ok */
			break;
		default:
			throw new TypeCheckException(loop, "Condition must have "
					+ "boolean type - got: " + e.getType().toString());
		}

		loop.getPostInstr().bindStaticContext(current);
		loop.getPostInstr().visitNode(this);
		
		loop.getBody().bindStaticContext(current);
		loop.getBody().visitNode(this);
	}

	public void visit(ReturnInstr ret) {
		try {
			VarDecl d = current.getVariable(Context.RETURN_ID);
			ret.getValue().visitNode(this);			
			switch (d.getType().compareTo(ret.getValue().getType())) {
			case GREATER:
			case EQUAL:
								
				break;
			default:
				throw new SyntaxErrorException(ret, "Non-compatible return expression.");
			}
		} catch (ElementNotFoundException e) {
			throw new SyntaxErrorException(ret, 
					"Returning from non-existant function/method.");
		}
	}

	public void visit(CondInstr cond) {
		Expression e = cond.getCondition();

		e.bindStaticContext(current);
		e.visitNode(this);
		switch (PrimitiveType.BOOLEAN.compareTo(e.getType())) {
		case EQUAL:
		case LESSER:
			/* ok */
			break;
		default:
			throw new TypeCheckException(cond, "Condition must have "
					+ "boolean type - got: " + e.getType().toString());
		}

		Instruction i;
		
		/* Visit "true" branch */
		i = cond.getBranch(true);
		i.bindStaticContext(current);
		i.visitNode(this);
		
		/* Visit "false" branch */
		i = cond.getBranch(false);
		i.bindStaticContext(current);
		i.visitNode(this);
	}

	public void visit(NullExpression v) {
		// nothing
	}

	public void visit(EmptyInstruction v) {
		// nothing
	}

	

}
