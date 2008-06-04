package org.lqc.jxc.transform;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

import org.lqc.jxc.CompilerException;
import org.lqc.jxc.il.Assignment;
import org.lqc.jxc.il.Block;
import org.lqc.jxc.il.Branch;
import org.lqc.jxc.il.Call;
import org.lqc.jxc.il.Callable;
import org.lqc.jxc.il.Closure;
import org.lqc.jxc.il.Constant;
import org.lqc.jxc.il.CreateObject;
import org.lqc.jxc.il.Expression;
import org.lqc.jxc.il.Function;
import org.lqc.jxc.il.Klass;
import org.lqc.jxc.il.KlassField;
import org.lqc.jxc.il.Loop;
import org.lqc.jxc.il.Nop;
import org.lqc.jxc.il.Operation;
import org.lqc.jxc.il.Return;
import org.lqc.jxc.il.ReturnVoid;
import org.lqc.jxc.il.Signature;
import org.lqc.jxc.il.StaticContainer;
import org.lqc.jxc.il.TypeConversion;
import org.lqc.jxc.il.Variable;
import org.lqc.jxc.il.VariableValue;
import org.lqc.jxc.tokens.ArgumentDecl;
import org.lqc.jxc.tokens.AssignmentInstr;
import org.lqc.jxc.tokens.CallableRef;
import org.lqc.jxc.tokens.CompileUnit;
import org.lqc.jxc.tokens.CondInstr;
import org.lqc.jxc.tokens.ConstantExpr;
import org.lqc.jxc.tokens.EmptyInstruction;
import org.lqc.jxc.tokens.ExprToken;
import org.lqc.jxc.tokens.ExternalFuncDecl;
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
import org.lqc.jxc.types.KlassType;
import org.lqc.jxc.types.Type;


public class AST2IL implements TreeVisitor {
	
	private static class Frame 
	{
		public StaticContainer<?> owner;
		public Queue<Operation> ops;
		public Stack<Expression<? extends Type>> estack;
		
		public Frame(StaticContainer<?> owner) {
			this.owner = owner;
			
			this.ops = new LinkedList<Operation>();
			this.estack = new Stack<Expression<? extends Type>>();
		}		
	}
		
	private Stack<Frame> xstack;	
	
	private Map<VarDecl<?>, Variable<?>> vmap;
	private Map<CallableRef, Callable> fmap;
		
	// map of names to class objects -- needed for closures
	private Map<String, Klass> kmap;
	
	private AST2IL() {
		vmap = new HashMap<VarDecl<?>, Variable<?>>();
		fmap = new HashMap<CallableRef, Callable>();
		kmap = new HashMap<String, Klass>();
		
		xstack = new Stack<Frame>();
	}
	
	private StaticContainer<?> container() {
		return xstack.peek().owner;
	}
	
	private void appendOp(Operation op) {
		xstack.peek().ops.offer(op);		
	}
	
	private Operation nextOp() {
		return xstack.peek().ops.poll();
	}
	
	private Operation peekOp() {
		return xstack.peek().ops.peek();
	}
	
	private boolean moreOps() {
		return !xstack.peek().ops.isEmpty();
	}
	
	private void pushExpr(Expression<? extends Type> e) {
		xstack.peek().estack.push(e);		
	}
	
	private Expression<? extends Type> popExpr() {
		return xstack.peek().estack.pop();		
	}
	
	private boolean hasExpr() {
		return !xstack.peek().estack.isEmpty();
	}
	
	private Expression<? extends Type> peekExpr() {
		return xstack.peek().estack.peek();
	}
	
	private Signature<FunctionType> sigFor(FunctionDecl f) {
		return	new Signature<FunctionType>(
				f.getLocalID(), f.getType());		
	}
		
	private void pickupExpr() {
		if(hasExpr()) {			 
			Operation op = this.popExpr();
	
			/* more then 1 expression should yield an error */
			if(hasExpr()) {
				throw new CompilerException(
						"[INTERNAL] Unmatched expression left on operand stack.");
			}
			
			appendOp(op);			
		} 			
	}
	
	public static Collection<Klass> convert(CompileUnit f, 
			Collection<ExternalContext> externals) {		
		AST2IL cv = new AST2IL();
		
		for(ExternalContext ex : externals) {
			for(ExternalFuncDecl fd : ex.getAllFunctionDecl())
				cv.fmap.put(fd, fd.getCallable());
		}
				
		f.visitNode(cv);		
		
		return cv.kmap.values();
	}

	public void visit(CompileUnit file) 
	{		
		Klass m = new Klass(file.getName());
		kmap.put(m.getKlassName(), m);
		xstack.push(new Frame(m));
				
		for(FunctionDecl fd : file.getFunctions())
		{
			/* Construct signatures */
			Signature<FunctionType> sig = 
				new Signature<FunctionType>(
					fd.getLocalID(), fd.getType());
			
			Signature<Type>[] args = 
				new Signature[fd.getArgs().size()];
			
						
			Function f = container().newFunc(fd.getLine(), sig, true);
								
			for(ArgumentDecl ad : fd.getArgs()) {
				f.newArg(new Signature<Type>(
						ad.getLocalID(), ad.getType()) );				
			}
			
			fmap.put(fd, f);			
		}
		
		for(FunctionDecl fd : file.getFunctions())
		{		
			fd.visitNode(this);
		}
		xstack.pop();		
	}

	public void visit(FunctionDecl fd) {			
		Callable c = fmap.get(fd);
		
		/* put our frame on stack */
		if(c instanceof Function) {
			Function f = (Function)c;
			
			xstack.push( new Frame(f) );
					
			/* map arguments */
			for(ArgumentDecl vd : fd.getArgs())
			{
				vd.visitNode(this);
			}			
		
			/* traverse instructions */
			fd.getBody().visitNode(this);
			
			Function.Frame cframe = f.getFrame();
			
			if(!cframe.isEmpty()) {
				kmap.put(cframe.getKlassName(), cframe);
								
				/* create constructor */
				Function constr = cframe.newFunc(fd.getLine(),
						"<init>", new FunctionType(Type.VOID), false );
				
				Variable<?> _self = 
					constr.newArg(new Signature<KlassType>(
							"_self", cframe.getType()) );
				
				Klass objklass = cframe.getBaseKlass();
				Callable supconstr = objklass.get("<init>", Type.VOID);
								
				Call call;
				constr.addOp( call = new Call(constr, fd.getLine(), 
						supconstr, Call.Proto.NONVIRTUAL) );
				call.addArgument( 
					new VariableValue(constr, fd.getLine(), _self) );
				constr.addOp( new ReturnVoid(constr, fd.getLine()) );
				
				/* invoke it */
				f.addOp(
					new Assignment(f, fd.getLine(),	f.getCallFrameVar(),
						call = new Call(f, fd.getLine(), constr, Call.Proto.CONSTR) 
					)
				);				
				
				/* create the frame before call */				
				call.addArgument( new CreateObject(f, 
						fd.getLine(), cframe.getType()) );
				
			}
		
			/* instructions are left on operation list */
			while(moreOps())
				f.addOp(nextOp());
		
			/* remove our frame from stack */
			xstack.pop();			
		}
		
		/* function declarations do nothing */
		appendOp( new Nop(container(), fd.getLine()) );
	}
	
	public void visit(VarDecl decl) 
	{
		Signature<Type> sig = new Signature<Type>(
				decl.getLocalID(), decl.getType());
				
		Variable<?> v = container().newVar(sig, !decl.isUsedNonLocally());
		
		/* add a maping to global symbol table */
		vmap.put(decl, v);
		
		/* result is an assignment */
		if(!decl.getInitialValue().equals(ExprToken.NULL)) {			
			decl.getInitialValue().visitNode(this);
						
			Expression<? extends Type> e = popExpr();
			appendOp( new Assignment(container(), decl.getLine(), v, e) );						
		}
		else {
			appendOp( new Nop(container(), decl.getLine()) );
		}
	}
	
	public void visit(ArgumentDecl decl) {
		Signature<Type> sig = new Signature<Type>(
				decl.getLocalID(), decl.getType());
				
		Variable v = ((Function)container()).get(sig);
		
		/* add a maping to global symbol table */
		vmap.put(decl, v);		
	}

	public void visit(FunctionCall call) {
		CallableRef cref = call.getRef();
		Callable ref;
		Call.Proto protocol;
		
		Expression<? extends Type> self = null;
		
		if(cref instanceof FunctionDecl) { 
			ref = fmap.get(cref);
			protocol = Call.Proto.STATIC;
		
			if(ref == null) {
				throw new CompilerException(String.format(
					"[AST2IL] Reference bound to named function '%s' at line %d not found.",
					call.getFid(), call.getLine()) );
			}						
		
		}
		else if(cref instanceof MethodRef) {
			MethodRef mref = (MethodRef)cref;
			
			// we need the instance as first argument			 
			Variable<?> v = vmap.get(mref.getInstance());
			FunctionType ft = (FunctionType) v.getSignature().type;
			
			// we need to pass a callable, which is
			// actually the closure's method
			String ksig = Closure.PREFIX + v.getSignature().type.getShorthand();
			
			Klass klass = kmap.get(ksig);
			if(klass == null) {
				// create on runtime
				klass = createKlassForClosure(ft);						
				kmap.put(ksig, klass);
			}
			
			ref = klass.get( new Signature<FunctionType>("_call", ft) );
			
			self = new VariableValue(container(), call.getLine(), v);
			protocol = Call.Proto.VIRTUAL;
		} else {
			throw new CompilerException(String.format(
					"[AST2IL] Invalid call to '%s' at line %d.",
						call.getFid(), call.getLine()) );
		}
		
		Call c = new Call(container(), call.getLine(), ref, protocol);
		
		if(self != null)
			c.addArgument(self);
		
		for(ExprToken<? extends Type> e : call.getArgs()) {
			e.visitNode(this);			
			c.addArgument(popExpr());			
		}
		
		pushExpr(c);
		return;		
	}

	private static Klass createKlassForClosure(FunctionType type) {
		
		String ksig = Closure.PREFIX + type.getShorthand();
		Klass klass = new Klass(ksig, true);			 
		Function f = klass.newFunc(0, "_call", type, false);
		
		f.newArg( new Signature<Type>("_self", new KlassType(klass) ) );
		
		int i=0;
		for(Type t : type.getArgumentTypes()) 
		{
			f.newArg( new Signature<Type>("arg" + i, t) );
			i++;
		};
		
		return klass;
	}

	public void visit(ConstantExpr c) {
		pushExpr(new Constant(container(), c.getLine(), 
				c.getType(), c.getValue()) );
	}

	public void visit(VarExpr var) {
		Variable<?> v = vmap.get(var.getRef());
		
		/* somehow we have to remap frame position 
		 * to the closure pointer */
		if( (v instanceof KlassField) 
		 && (container() instanceof Function)
		 && ((Function)container()).isLambda() )
		{
			KlassField field  = (KlassField)v;
			Variable<?> source = field.getSource();
			Function lambda = (Function)container();
			Closure closure = (Closure)lambda.container();
			
			/* this variable needs to be able to access
			 * it's source. We should find a coresponding
			 * entry in the closure (if we are in one) */
			boolean found = false;
			
			for(Variable<?> f : closure.allVariables())
			{
				if(f.getSignature().equals(source.getSignature()))
				{
					found = true;
					/* make a new variable instance 
					 * with diffrenr source */
					KlassType kt = (KlassType) f.getSignature().type;
					v = new KlassField(kt.getKlass(), f,
							new Signature<Type>(v.getSignature()) );					
					break;
				}
			}
		
			if(!found) 
				throw new CompilerException(
					"AST2IL: Unmatched Frame reference inside closure");			
			
		}
		pushExpr( new VariableValue(container(),
				var.getLine(), v) );		
	}

	public void visit(InstrBlock ci) {
		for(Instruction i : ci)
		{			
			i.visitNode(this);			
			pickupExpr();			
		}					
	}
	
	public void visit(InstrList instrList) {
		for(Instruction i : instrList) {
			i.visitNode(this);			
			pickupExpr();
		}		
	}

	public void visit(AssignmentInstr as) {
		Variable v = vmap.get(as.getRef());
		
		as.getValue().visitNode(this);		
		Expression<? extends Type> e = popExpr();
			
		pushExpr( new Assignment(container(), as.getLine(), v, e));
	}
	
	public void visit(IncrementInstr i) {
		i.getAction().visitNode(this);				
	}

	public void visit(LoopInstr loop) {
		/* append loop init block */
		loop.getInitInstr().visitNode(this);
		
		ExprToken<Type> e = loop.getCondition();
		e.visitNode(this);
		
		Expression<? extends Type> ex = popExpr();
		
		Block body = new Block(
				container(), loop.getLine());
		
		/* embrance body and post */
		xstack.push( new Frame(container()));
		
		loop.getBody().visitNode(this);
		loop.getPostInstr().visitNode(this);
		
		pickupExpr();
		
		while(moreOps())
			body.append(nextOp());		
		xstack.pop();
		
		/* append loop operation */
		appendOp(new Loop(container(), loop.getLine(), ex, body));
	}

	public void visit(ReturnInstr ret) {
		if(!ret.getValue().equals(ExprToken.VOID)) {
			ret.getValue().visitNode(this);
			
			Expression<? extends Type> e = popExpr();			
			appendOp(new Return(container(), ret.getLine(), e));
			
			return;
		}
		
		appendOp(new ReturnVoid(container(), ret.getLine()));		
	}

	public void visit(CondInstr cond) {
		ExprToken<? extends Type> e = cond.getCondition();
		e.visitNode(this);
		
		Expression<? extends Type> ex = popExpr();
		
		Block branchA, branchB;
		
		/* create a fictional frame */
		xstack.push( new Frame(container()));		
		
		/* transform "true" branch */
		cond.getBranch(true).visitNode(this);		
		pickupExpr();
		
		branchA = new Block(container(), 
				cond.getBranch(true).getLine());
		/* add all ops */
		while(moreOps())
			branchA.append(nextOp());
		
		
		xstack.pop();
		
		/* same for second branch */
		xstack.push( new Frame(container()));		
		/* transform "true" branch */
		cond.getBranch(false).visitNode(this);
		pickupExpr();
		
		branchB = new Block(container(), 
				cond.getBranch(false).getLine());
		
		/* add all ops */
		while(moreOps())
			branchB.append(nextOp());
		
		xstack.pop();
		
		appendOp( new Branch(container(), cond.getLine(), 
				ex, branchA, branchB));
	}

	public void visit(NullExpression v) {
		// XXX should/could push a "null" or something 
	}

	public void visit(EmptyInstruction v) {
		appendOp(new Nop(container(), v.getLine()));
	}

	public void visit(TypeCast cast) {
		cast.getExpression().visitNode(this);		
		Expression<? extends Type> e = popExpr();
		
		pushExpr( new TypeConversion(
			container(), cast.getLine(), e, cast.dstType()) );
		
	}

	public void visit(ImportStmt importStmt) {
		// this is no longer needed		
	}

	public void visit(LambdaExpr lambda) {					
		// TODO: double check this
				
		Closure closure = new Closure((Function)container());
		kmap.put(closure.getKlassName(), closure);
		
		Signature<FunctionType> sig = 
			new Signature<FunctionType>(
				"_call", (FunctionType)lambda.getType() );
		
		Function f = new Function(closure, 
				lambda.getLine(), sig, false, false, true);
		
		closure.produceLambda(f);
		
		xstack.push( new Frame(f) );		
		f.newArg( new Signature<KlassType>("_self", closure.getType()) );
		
		/* map arguments */
		for(ArgumentDecl vd : lambda.getArgs()) {
			f.newArg(new Signature<Type>(vd.getLocalID(), vd.getType()) );
			vd.visitNode(this);						
		}
		
		/* traverse instructions */
		lambda.getBody().visitNode(this);	
		
		/* instructions are left on operation list */
		while(moreOps())
			f.addOp(nextOp());
		
		/* add yield as a return */
		ExprToken<? extends Type> y = lambda.getYield();
		
		if(y.getType().equals(Type.VOID))
			f.addOp( new ReturnVoid(container(), f.lastLineNumber()) );
		else {
			y.visitNode(this);
			Expression<? extends Type> re = popExpr();			
			f.addOp( new Return(container(), f.lastLineNumber(), re) );
		}
						
		/* remove our frame from stack */
		xstack.pop();
						
		Function constr = closure.getDefaultConstructor();
		Call invk;
					
		invk = new Call(container(), lambda.getLine(), 
				constr, Call.Proto.CONSTR);
		
		/* first argument is the instance */
		invk.addArgument( new CreateObject(container(), lambda.getLine(),
				closure.getType()) );
				
		/* second is the function's frame */				
		invk.addArgument( new VariableValue(
			container(), lambda.getLine(), ((Function)container()).getCallFrameVar()) );
				
		pushExpr(invk);						
	}

	

}
