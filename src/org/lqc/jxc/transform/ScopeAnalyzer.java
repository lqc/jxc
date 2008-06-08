package org.lqc.jxc.transform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

import org.lqc.jxc.CompilerException;
import org.lqc.jxc.Pair;
import org.lqc.jxc.SyntaxErrorException;
import org.lqc.jxc.TypeCheckException;
import org.lqc.jxc.il.Klass;
import org.lqc.jxc.tokens.ArgumentDecl;
import org.lqc.jxc.tokens.AssignmentInstr;
import org.lqc.jxc.tokens.CallableRef;
import org.lqc.jxc.tokens.CompileUnit;
import org.lqc.jxc.tokens.CondInstr;
import org.lqc.jxc.tokens.ConstantExpr;
import org.lqc.jxc.tokens.EmptyInstruction;
import org.lqc.jxc.tokens.ExprToken;
import org.lqc.jxc.tokens.FunctionCall;
import org.lqc.jxc.tokens.FunctionDecl;
import org.lqc.jxc.tokens.ImportStmt;
import org.lqc.jxc.tokens.IncrementInstr;
import org.lqc.jxc.tokens.InstrBlock;
import org.lqc.jxc.tokens.InstrList;
import org.lqc.jxc.tokens.Instruction;
import org.lqc.jxc.tokens.LambdaExpr;
import org.lqc.jxc.tokens.LoopInstr;
import org.lqc.jxc.tokens.MethodRef;
import org.lqc.jxc.tokens.NullExpression;
import org.lqc.jxc.tokens.ReturnInstr;
import org.lqc.jxc.tokens.TypeCast;
import org.lqc.jxc.tokens.VarDecl;
import org.lqc.jxc.tokens.VarExpr;
import org.lqc.jxc.types.FunctionType;
import org.lqc.jxc.types.PrimitiveType;
import org.lqc.jxc.types.Type;
import org.lqc.util.ElementNotFoundException;
import org.lqc.util.MultiplyMatchException;
import org.lqc.util.NonUniqueElementException;
import org.lqc.util.PathID;
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
	
	protected Map<String, ExternalContext> externals;
	
	private ClassLoader loader;

	private void push(Context ctx) {
		envStack.push(ctx);
		current = ctx;
	}

	private void pop() {
		envStack.pop();
		current = envStack.peek();
	}
	
	private ExternalContext loadModule(Context parent, PathID path) {
		// load module		
		try {
			Class<?> c = loader.loadClass(path.absoluteName());			
			
			ExternalContext x = 
				new ExternalContext(parent, Klass.forJavaClass(c) );
			
			if(externals.containsKey(x.name))
				throw new CompilerException("Duplicate import" +
						" of module '" + x.name +"'");
			
			externals.put(x.name, x);
			
			return x;			
		} catch (ClassNotFoundException e) {
			throw new CompilerException("Could not load module " + 
					path.absoluteName() );
		}	
		
	}
	
	private	FunctionDecl lookupCallable(PathID path, FunctionType t)
		throws ElementNotFoundException, MultiplyMatchException
	{
		return this.lookupCallable(current, path, t);		
	}
	
	private	FunctionDecl lookupCallable(
			Context c, PathID path, FunctionType t)
		throws ElementNotFoundException, MultiplyMatchException
	{
		// if the path is relative to context 
		if(path.isRelative()) {
			final String bn = path.basename();
			try {
				return c.getFunction(bn, t);										
			} catch(ElementNotFoundException e) {
				/* look in externals */
				List<FunctionDecl> dl = new Vector<FunctionDecl>();
				for(Context x : externals.values())
				{					
					try {
						dl.add(x.getFunction(bn, t));						
					} catch(ElementNotFoundException ex) {
						// ignore
					}					
				}
				
				if(dl.size() > 1)
					throw new MultiplyMatchException(dl);
				
				if(dl.size() == 0)
					throw new ElementNotFoundException();
				
				return dl.get(0);				
			}
		}
		
		// if not then lookup the context
		Pair<PathID, String> p = path.tailSplit();
		PathID xpath = new PathID(p.second());
		
		while(p.first() != null) {
			Context x = lookupContext(c, p.first().absoluteName());
			
			if(x != null)
				return lookupCallable(x, xpath, t);
			
			p = p.first().tailSplit();
			xpath.prepend(p.second());
		}
		
		throw new ElementNotFoundException();			
	}
	
	private VarDecl lookupVariable(PathID path)
		throws ElementNotFoundException
	{
		return this.lookupVariable(current, current, path);		
	}
	
	private VarDecl lookupVariable(Context callee, Context c, PathID path)
		throws ElementNotFoundException
	{				
		// if the path is relative to context 
		if(path.isRelative()) {
			VarDecl d = c.getVariable(path.basename());
			
			if(d == null) return null; 
						
			Context cur = callee; // callee is under declaration context
			Context decl = d.getStaticContext();
			
			/* TODO: <RETURN> variable seems have wrong initial SL */
			if(decl != null)
			{
				boolean lambda = false;
				while(cur != null && !cur.equals(decl)) {
					if(cur.name.equals("<lambda>")) lambda = true;
					cur = cur.parent;										
				}
				
				if(cur == null) /* this can't happend */
					throw new CompilerException("[INTERNAL] Found variable, but can't find it's context above us.");
				
				if(lambda) d.markNonLocalUsage();
			}				
			
			return d;			
		}
		
		// if not then lookup the context
		Pair<PathID, String> p = path.tailSplit();
		PathID xpath = new PathID(p.second());
		
		while(p.first() != null) {
			Context x = lookupContext(c, p.first().absoluteName());
			
			if(x != null)
				return lookupVariable(callee, x, xpath);
			
			p = p.first().tailSplit();
			xpath.prepend(p.second());
		}
		
		throw new ElementNotFoundException();		
	}
	
	private Context lookupContext(Context base, String name)		
	{
		return externals.get(name);		
	}
	
	private <E extends Exception> ExprToken<? extends Type> typeCheck(ExprToken<? extends Type> e, Type b,
			E ex) throws E {
		if (!e.getType().equals(Type.ANY)) {
			switch (e.getType().compareTo(b)) {
			case LESSER:
				/* apply convertion */
				return new TypeCast(e.getLine(), e.getColumn(), e, b);
			case EQUAL:
				/* Type ok */
				break;
			default:
				throw ex;
			}
		}
		return e;
	}
	
	public Map<String, ExternalContext> externals() {
		return this.externals;
	}

	public ScopeAnalyzer() {
		envStack = new Stack<Context>();
		loader = ClassLoader.getSystemClassLoader();
		externals = new HashMap<String,ExternalContext>();
		
		push(loadModule(null, new PathID("lang.jx.System")) );
	}

	public void visit(CompileUnit prog) {
		prog.bindStaticContext(current);
		Context ctx = new Context(current, "<global>");
		
		for(ImportStmt is : prog.getImports()) {
			is.bindStaticContext(ctx);
			is.visitNode(this);
		}

		for (FunctionDecl f : prog.getFunctions()) {
			try {
				ctx.put(f);
			} catch (NonUniqueElementException e) {
				throw new SyntaxErrorException(f, "Function redeclaration\n");
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
					"Function argument '%s' redeclaration\n", decl.getLocalID()));
		}
	}
	
	public void visit(LambdaExpr lambda) {
		lambda.bindStaticContext(current);
		lambda.initInnerContext(lambda.getStaticContext());
		push(lambda.innerContext());		

		// during the process we will infer lambda's type
		// argument types are known but return type might not 
		
		List<Type> args_type = new Vector<Type>();
		FunctionType infered;
		
		// process arguments
		for (ArgumentDecl d : lambda.getArgs()) {			
			d.bindStaticContext(current);
			d.visitNode(this);
			args_type.add(d.getType());
		}
		
		// process body
		lambda.getBody().bindStaticContext(current);
		for(Instruction i : lambda.getBody())
		{
			i.bindStaticContext(current);
			i.visitNode(this);		
			
			if ((i instanceof ExprToken) && !(i instanceof AssignmentInstr)) {
				ExprToken<Type> e = (ExprToken<Type>) i;
				/* check if expression returns void */
				if (!e.getType().compareTo(Type.VOID).equals(Relation.EQUAL)) {
					throw new TypeCheckException(e,
							"Instruction must be 'void' type");
				}
			}			
		}
		
		// now process the yield
		ExprToken<? extends Type> y = lambda.getYield();
		
		y.bindStaticContext(current);
		y.visitNode(this);
		
		// infer the lambda type
		infered = new FunctionType(y.getType(), args_type);
		
		lambda.getType().correct(infered);
		
		pop();		
	}

	public void visit(FunctionDecl decl) {
		decl.initInnerContext(decl.getStaticContext());
		push(decl.innerContext());

		int localVars = 0;

		try {
			current.put(new VarDecl<Type>(decl.getType().getReturnType(),
					Context.RETURN_ID.basename()));
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

			if ((i instanceof ExprToken) && !(i instanceof AssignmentInstr)) {
				ExprToken<Type> e = (ExprToken<Type>) i;
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
		ExprToken<? extends Type> e = decl.getInitialValue();

		e.visitNode(this);
		e = typeCheck(
				e,
				decl.getType(),
				new TypeCheckException(
						decl,
						String
								.format(
										"Can't initialize variable '%s' of type '%s' with '%s' type expression",
										decl.getLocalID(), decl.getType(), e
												.getType())));
		decl.setInitialValue(e);

		try {
			current.put(decl);
		} catch (NonUniqueElementException ex) {
			throw new SyntaxErrorException(decl, "Multiply declaration of"
					+ "variable: " + decl.getLocalID());
		}
	}

	public void visit(FunctionCall call) {
		Type[] types = new Type[call.getArgs().size()];
		int i = 0;

		for (ExprToken<? extends Type> e : call.getArgs()) {
			e.bindStaticContext(current);
			e.visitNode(this);
			types[i++] = e.getType();
		}

		FunctionType alpha = new FunctionType(Type.ANY, types);
		
		CallableRef ref;
		
		try {
			VarDecl<? extends Type> v = lookupVariable(call.getFid());			
			System.out.printf("Checking call to closure %s of type %s\n",
					v.getLocalID(), v.getType().toString() );
			
			if(!(v.getType() instanceof FunctionType)) {
				throw new CompilerException("Variable" + v.getLocalID()
						+ "doesn't contain a callable element");				
			}
			
			VarDecl<? extends FunctionType> closure = 
				(VarDecl<? extends FunctionType>)v;
			
			FunctionType ctype = closure.getType();			
			
			switch(ctype.getArgumentTypes().compareTo(alpha.getArgumentTypes()))
			{
				case LESSER: 
				case NONCOMPARABLE:
					throw new ElementNotFoundException();					
			}
			
			// we can apply - cast is safe 
			ref = new MethodRef(closure, "<call>", alpha);			
		} catch(ElementNotFoundException e) {
			try {
				ref = lookupCallable(call.getFid(), alpha);				
			} catch (ElementNotFoundException ex) {
				throw new SyntaxErrorException(call, String.format(
					"ScopeAnalyzer: No match for '%s' with type '%s'\n", call.getFid(), alpha
							.toString()));
			} catch (MultiplyMatchException ex) {
				throw new SyntaxErrorException(call, "Disambigous call:"
					+ e.getMessage());
			}
		}	
		
		/* bind reference */
		call.bindRef(ref);

		/* check for possible casts */
		ArrayList<ExprToken<? extends Type>> call_args = call.getArgs();
		for (int j = 0; j < call_args.size(); j++) {
			ExprToken<? extends Type> e = typeCheck(call_args.get(j), 
					ref.getType().getArgumentTypes().get(j), 
					new CompilerException("ScopeAnalyzer: Illegal implicit cast") );

			call_args.set(j, e);
		}
		
	}

	public void visit(ConstantExpr c) {
		// nothing to do
	}

	public void visit(VarExpr e) {
		try {
			VarDecl d = lookupVariable(e.getId());
			e.bindRef(d);
		} catch (ElementNotFoundException exc) {
			throw new SyntaxErrorException(e, String
					.format("ScopeAnalyzer->VarExpr: No match for variable '%s' in current scope.\n", e
							.getId()));
		}
	}

	public void visit(InstrBlock instr) {
		push(new Context(current, "block"));

		for (Instruction i : instr) {
			i.bindStaticContext(current);
			i.visitNode(this);
		}

		pop();
	}
	
	public void visit(InstrList instrList) {
		for(Instruction i : instrList) {
			i.bindStaticContext(current);
			i.visitNode(this);			
		}		
	}

	public void visit(AssignmentInstr instr) {
		try {
			VarDecl d = lookupVariable(instr.getId());
			instr.setRef(d);

			ExprToken<? extends Type> e = instr.getValue();
			e.visitNode(this);

			e = typeCheck(e, d.getType(),
					new TypeCheckException(instr, String.format(
							"ScopeAnalyzer->Assign: Cannot assign expression of type '%s' to"
									+ " variable '%s' of type '%s'.", e
									.getType().toString(), instr.getId(), d
									.getType().toString())));
			instr.setValue(e);
		} catch (ElementNotFoundException e) {
			throw new SyntaxErrorException(instr, String.format(
					"ScopeAnalyzer->Assign: No match for variable '%s' in current scope.\n", instr
							.getId()));
		}
	}

	public void visit(IncrementInstr instr) {
		VarDecl d;

		try {
			d = lookupVariable(instr.getId());
			instr.setRef(d);
		} catch (ElementNotFoundException e) {
			throw new SyntaxErrorException(instr, String.format(
					"No match for variable '%s' in current scope.\n", instr
							.getId()));
		}

		/* match increment */
		FunctionDecl fd;
		FunctionCall fc;

		/* the type has no defined _INC function */
		ConstantExpr ce = new ConstantExpr(instr.getLine(), instr.getColumn(),
				new Integer(instr.getChange()));
		ce.visitNode(this);

		VarExpr ve = new VarExpr(instr.getLine(), instr.getColumn(), d.getAbsoluteID());
		ve.visitNode(this);

		/*
		 * try { fd = current.getFunction("_INC", new FunctionType(Type.VOID,
		 * d.getType(), PrimitiveType.INT) ); fc = new FunctionCall(
		 * instr.getLine(), instr.getColumn(), "_INC", ve, ce);
		 * fc.bindStaticContext(current); fc.bindRef(fd); instr.setAction(fc); }
		 * catch (MultiplyMatchException e) { throw new
		 * SyntaxErrorException(instr, "Disambigous call:" + e.getMessage() ); }
		 * catch (ElementNotFoundException e) {
		 */

		FunctionType ft = null;
		try {
			fd = lookupCallable(new PathID("_ADD"), ft = new FunctionType(d.getType(),
					d.getType(), PrimitiveType.INT));
		} catch (ElementNotFoundException ex) {
			throw new SyntaxErrorException(instr, String.format(
					"No match for _ADD that matches type '%s'\n", ft));
		} catch (MultiplyMatchException ex) {
			throw new SyntaxErrorException(instr, "Disambigous call:"
					+ ex.getMessage());
		}

		fc = new FunctionCall(instr.getLine(), instr.getColumn(), "_ADD", ve, ce);

		AssignmentInstr as = new AssignmentInstr(instr.getLine(), instr
				.getColumn(), d.getAbsoluteID(), fc);
		
		as.visitNode(this);
		instr.setAction(as);
	}

	public void visit(LoopInstr loop) {
		
		/* create local block */
		push(new Context(current, "loop-block"));
		
		loop.getInitInstr().visitNode(this);
		ExprToken<? extends Type> e = loop.getCondition();

		/* calculate type */
		e.bindStaticContext(current);
		e.visitNode(this);

		e = typeCheck(e, PrimitiveType.BOOLEAN, new TypeCheckException(loop,
				"Condition must have " + "boolean type - got: "
						+ e.getType().toString()));

		loop.setCondition(e);

		loop.getPostInstr().bindStaticContext(current);
		loop.getPostInstr().visitNode(this);

		loop.getBody().bindStaticContext(current);
		loop.getBody().visitNode(this);
		
		pop();
	}

	public void visit(ReturnInstr ret) {
		try {
			VarDecl d = lookupVariable(Context.RETURN_ID);
			ret.getValue().visitNode(this);

			ret.setValue(typeCheck(ret.getValue(), d.getType(),
					new SyntaxErrorException(ret,
							"Non-compatible return expression.")));

		} catch (ElementNotFoundException e) {
			throw new SyntaxErrorException(ret,
					"Returning from non-existant function/method.");
		}
	}

	public void visit(CondInstr cond) {
		ExprToken<? extends Type> e = cond.getCondition();

		e.bindStaticContext(current);
		e.visitNode(this);

		e = typeCheck(e, PrimitiveType.BOOLEAN, new TypeCheckException(cond,
				"Condition must have " + "boolean type - got: "
						+ e.getType().toString()));

		cond.setCondition(e);

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

	public void visit(TypeCast cast) {
		ExprToken<Type> e = cast.getExpression();
		e.visitNode(this);
	}

	public void visit(ImportStmt importStmt) 
	{		
		// we assume module names are absolute		
		loadModule(current, importStmt.getPath());		
	}

	

}
