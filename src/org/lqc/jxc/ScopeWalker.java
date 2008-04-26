package org.lqc.jxc;

import java.util.List;
import java.util.Stack;
import java.util.Vector;

import org.lqc.jxc.tokens.ArgumentDecl;
import org.lqc.jxc.tokens.AssignmentInstr;
import org.lqc.jxc.tokens.ComplexInstr;
import org.lqc.jxc.tokens.CondInstr;
import org.lqc.jxc.tokens.ConstantExpr;
import org.lqc.jxc.tokens.EmptyInstruction;
import org.lqc.jxc.tokens.Expression;
import org.lqc.jxc.tokens.FunctionCall;
import org.lqc.jxc.tokens.FunctionDecl;
import org.lqc.jxc.tokens.Instruction;
import org.lqc.jxc.tokens.LoopInstr;
import org.lqc.jxc.tokens.NullExpression;
import org.lqc.jxc.tokens.Program;
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

public class ScopeWalker implements TreeVisitor {
	
	private Stack<Context> envStack;	
	private Context current; 
	
	private void push(Context ctx) {
		envStack.push(ctx);
		current = ctx;
	}
	
	private void pop() {
		current = envStack.pop();
	}
	
	public ScopeWalker() {
		envStack = new Stack<Context>();
		push(Context.getBuiltins());
	}

	public void visit(Program prog) {		
		Context ctx = new Context(current);
		
		try {
			for(FunctionDecl f : prog.getFunctions()) {
				ctx.put(f);			
			}
			
			push(ctx);
			
			for(FunctionDecl f : prog.getFunctions()) {
				f.bindStaticContext(ctx);
				f.visitNode(this);			
			}
		}
		catch(NonUniqueElementException e) {
			throw new CompilerException("[ERROR] Function redeclaration\n");					
		}		
	}

	public void visit(ArgumentDecl decl) {		
		try {
			current.put(new VarDecl(decl.getType(), decl.getID()) );
		} catch (NonUniqueElementException e) {			
			throw new CompilerException(String.format("[ERROR] Function argument '%s' redeclaration\n", decl.getID()));
		}		
	}

	public void visit(FunctionDecl decl) {
		push(new Context(decl.getStaticContext()) );	
		
		/* process arguments */
		for(ArgumentDecl d : decl.getArgs()) {
			d.bindStaticContext(current);
			d.visitNode(this);			
		}
		
		/* process instructions - don't create 
		 * context for body */
		for(Instruction i : decl.getBody().getInstructions()) {
			i.bindStaticContext(current);
			i.visitNode(this);
			if(i instanceof Expression) {
				Expression e = (Expression)i;
				/* check if expression returns void */
				if(Type.VOID.compareTo(e.getType()).
						equals(Relation.EQUAL) ) 
				{
					throw new TypeCheckException(
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
		switch(decl.getType().compareTo(e.getType())) {
			case EQUAL:
			case GREATER:
				/* ok */
				break;
			default:
				throw new TypeCheckException(
					String.format("Can't initialize variable '%s' of type '%s' with '%s' type expression",
							decl.getID(), decl.getType(), e.getType()) );
		}
				
		try { 
			current.put(decl);
		} catch (NonUniqueElementException ex) {
			throw new CompilerException("Multiply declaration of" +
					"variable: " + decl.getID() );
		}
	}

	public void visit(FunctionCall call) {
		List<Type> types = new Vector<Type>();
		
		for(Expression e : call.getArgs()) {
			e.bindStaticContext(current);
			e.visitNode(this);
			types.add(e.getType());
		}
		
		FunctionType alpha = new FunctionType(Type.ANY, types);
		
		try {
			FunctionDecl d = current.getFunction(call.getFid(), alpha);
			call.bindRef(d);			
		} catch (ElementNotFoundException e) {
			throw new CompilerException(
					String.format("No match for '%s' with type '%s'\n",
					call.getFid(), alpha.toString()) );			
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
			throw new CompilerException(
					String.format("No match for variable '%s' in current scope.\n",
					e.getId()) );			
		}		
	}

	public void visit(ComplexInstr instr) {
		push(new Context(current));
		
		for(Instruction i : instr.getInstructions()) 
		{
			i.bindStaticContext(current);
			i.visitNode(this);
		}
		
		pop();
	}

	public void visit(AssignmentInstr instr) 
	{
		try {
			VarDecl d = current.getVariable(instr.getId());
			instr.setRef(d);
			
			Expression e = instr.getValue();
			e.visitNode(this);
			switch( e.getType().compareTo(d.getType())) {
				case EQUAL:
				case LESSER:
					/* Type ok */
					break;
				default:
					throw new TypeCheckException(
							String.format("Cannot assign expression of type '%s' to" +
							"variable '%s' of type '%s'.",
							e.getType().toString(),
							instr.getId(),
							d.getType().toString())							
							);
			}		
		} catch (ElementNotFoundException e) {
			throw new CompilerException(
					String.format("No match for variable '%s' in current scope.\n",
					instr.getId()) );			
		}		
	}

	public void visit(LoopInstr loop) {
		loop.getInitInstr().visitNode(this);
		Expression e = loop.getCondition();
		
		/* calculate type */
		e.visitNode(this);
		switch(PrimitiveType.BOOLEAN.compareTo(e.getType())) {
			case EQUAL:
			case LESSER:
				/* ok */
				break;
			default:
				throw new TypeCheckException("Condition must have " +
						"boolean type - got: " + e.getType().toString());			
		}
		
		loop.getPostInstr().visitNode(this);
		loop.getBody().visitNode(this);		 
	}

	public void visit(ReturnInstr ret) {
		// TODO - compare return expr type with current block type
	}

	public void visit(CondInstr cond) {
		Expression e = cond.getCondition();
		
		e.visitNode(this);
		switch(PrimitiveType.BOOLEAN.compareTo(e.getType())) {
			case EQUAL:
			case LESSER:
				/* ok */
				break;
			default:
				throw new TypeCheckException("Condition must have " +
						"boolean type - got: " + e.getType().toString());			
		}
		
		cond.getBranch(true).visitNode(this);
		cond.getBranch(false).visitNode(this);
	}

	public void visit(NullExpression v) {
		// nothing
	}

	public void visit(EmptyInstruction v) {
		// nothing
	}

}
