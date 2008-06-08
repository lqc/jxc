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
import org.lqc.jxc.il.KlassFieldRef;
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
import org.lqc.jxc.types.Type;


public class AST2IL implements TreeVisitor {
	
	private static class Frame 
	{
		public StaticContainer<?> owner;
		public Queue<Operation> ops;
		public Stack<Expression<? extends Type>> estack;
		public Map<Variable<?>, KlassField> remap; 
		
		public Frame(StaticContainer<?> owner) {
			this.owner = owner;
			
			this.ops = new LinkedList<Operation>();
			this.estack = new Stack<Expression<? extends Type>>();
			this.remap = new HashMap<Variable<?>, KlassField>();
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
						ad.getLocalID(), ad.getType()),
						!ad.isUsedNonLocally() );				
			}
			
			fmap.put(fd, f);			
		}
		
		for(FunctionDecl fd : file.getFunctions())
		{		
			fd.visitNode(this);
		}
		xstack.pop();		
	}

	private void initCallFrame(int line, Function.Frame cframe) 
	{
		Function f = (Function)cframe.container();
		kmap.put(cframe.getKlassName(), cframe);
								
		/* create constructor */
		Function constr = cframe.newFunc(0,
				"<init>", new FunctionType(Type.VOID), false );
			
		Variable<?> _cself = constr.getSelf();					
			
		Klass objklass = cframe.getBaseKlass();
		Callable supconstr = objklass.get("<init>", Type.VOID);
							
		Call call;
		
		/* put super invokation into the constructor */
		constr.addOp( call = new Call(constr, 1, 
				supconstr, Call.Proto.NONVIRTUAL) );
		
		call.addArgument( new VariableValue(constr, 1, _cself) );
		constr.addOp( new ReturnVoid(constr, 1) );
			
		/* invoke constructor */
		f.addOp(
			new Assignment(f, line, f.getCallFrameVar(),
					call = new Call(f, line, constr, Call.Proto.CONSTR) 
				)
			);				
			
		/* create the frame before call */				
		call.addArgument( 
			new CreateObject(f, line, cframe.getType()) 
		);

		// now our frame is ready
		// we need to initialize it with values
		// of non-local arguments
		for(Variable<?> proxy : f.allVariables())
		{
			Variable<?> real = f.unproxy(proxy);

			if(real == null) 
				continue;

			f.addOp( new Assignment(f, line, real,
					new VariableValue(f, line, proxy))
			);							 
		}		
	}
	
	public void visit(FunctionDecl fd) {			
		Callable c = fmap.get(fd);
		
		/* put our operation frame on stack */
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
			
			// initialize the call frame
			Function.Frame cframe = f.getFrame();
			
			initCallFrame(fd.getLine(), cframe);			
		
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
		
		this.ensureInstantiable(v.getSignature().type);
		
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
				
		Variable<?> v = ((Function)container()).get(sig);
		
		this.ensureInstantiable(v.getSignature().type);
		
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
			this.ensureInstantiable(v.getSignature().type);

			Klass klass = kmap.get(Closure.PREFIX + v.getSignature().type.getShorthand());			
			Function _call = (Function)klass.get( new Signature<FunctionType>("_call", ft) );
						
			self = new VariableValue(container(), call.getLine(), 
					this.remapSelf(v) );			
			ref = _call;
			
			protocol = Call.Proto.VIRTUAL;
		} else {
			throw new CompilerException(String.format(
					"[AST2IL] Invalid call to '%s' at line %d.",
						call.getFid(), call.getLine()) );
		}
		
		Call c = new Call(container(), call.getLine(), ref, protocol);
		
		if(self != null) {			
			c.addArgument(self);
		}
		
		for(ExprToken<? extends Type> e : call.getArgs()) {
			e.visitNode(this);			
			c.addArgument(popExpr());			
		}
		
		pushExpr(c);
		return;		
	}

	private static Klass createInterfaceForClosure(FunctionType type) 
	{
		/* this creates an interface for given type */
		String ksig = Closure.PREFIX + type.getShorthand();
		Klass klass = new Klass(ksig, true);
		klass.addImplements("lang.jx.Closure");
		
		Function f = klass.newFunc(0, "_call", type, false);
				
		int i=0;
		for(Type t : type.getArgumentTypes()) 
		{
			f.newArg( new Signature<Type>("arg" + i, t), true );
			i++;
		};
		
		KlassFieldRef x = klass.newClassVar("_type_signature", 
				new Constant<String>(klass, 0, type.toString()) );
		
		return klass;
	}
	
	private void ensureInstantiable(Type t) 
	{
		if(t instanceof FunctionType) 
		{
			FunctionType ftype = (FunctionType) t;
			
			/* generate interface for closure conversion */
			String ksig = Closure.PREFIX + ftype.getShorthand();
			
			Klass klass = kmap.get(ksig);
			if(klass == null) {
				// create on runtime
				klass = createInterfaceForClosure(ftype);						
				kmap.put(ksig, klass);
			}
		}
	}
	

	private Variable<?> remapSelf(Variable<?> v)
	{
		if(xstack.peek().remap.containsKey(v))
			return xstack.peek().remap.get(v);
		
		if(v instanceof KlassFieldRef) {
			throw new CompilerException("ooops ?");			
		}
		
		if(v instanceof KlassField)
		{
			/* This is the lambda bottom _call */ 
			Function _call = (Function)container();
			
			/* the closure where resides parent call frame */
			if(! (_call.container() instanceof Closure) ) return v;
			
			Closure closure = (Closure)_call.container();
			
			KlassField field  = (KlassField)v;
			Variable<?> source = field.getSource();	
			
			if(source == null)
				throw new CompilerException(
					"AST2IL: Variable with null source or Unmatched Frame reference inside closure");

			/* this variable needs to be able to access
			 * it's source. We should find a coresponding
			 * entry in the closure (if we are in one) */
			
			Variable<?> frame;
			for(Variable<?> cf : closure.allVariables())
			{
				if(! cf.matches(source) )
					continue;
				
				if(cf instanceof KlassFieldRef) {
					frame = ((KlassFieldRef)cf).deref(_call.getSelf());
				}
				else {
					frame = cf;
				}

				/* make a new variable instance 
				 * with diffrent source */				
				KlassField nv = new KlassField(frame, field.template());						

				xstack.peek().remap.put(v, nv);					
				return nv;
			}
			
			KlassField nsource = new KlassField(
					closure.getParent().getCallFrameVar(), 
					new KlassFieldRef(
							closure.getParent().getFrame(),
							source.getSignature(), false
			));
			
			/* try to remap the source */			
			KlassField nv = new KlassField(
					this.remapSelf(nsource), field.template());
			
			xstack.peek().remap.put(v, nv);
			return nv;										
		}
		
		return v;		
	}
	
	public void visit(ConstantExpr c) {
		pushExpr(
			new Constant(container(), c.getLine(), c.getValue()) );
	}

	
	public void visit(VarExpr var) {
		Variable<?> v = vmap.get(var.getRef());
				
		pushExpr( new VariableValue(container(),
				var.getLine(), this.remapSelf(v)) );		
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
		Variable<?> v = vmap.get(as.getRef());
		
		v = this.remapSelf(v);
		
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
						
		Closure closure = new Closure((Function)container());
		kmap.put(closure.getKlassName(), closure);
		
		/* lambda call function - this will be called
		 * later with normal arguments */
		Signature<FunctionType> sig = 
			new Signature<FunctionType>(
				"_call", (FunctionType)lambda.getType() );
		
		Function f = new Function(closure, 
				lambda.getLine(), sig, false, false, true);
		
		ensureInstantiable(f.declSignature().type);
		
		/* lambda construction */
		closure.produceLambda(f);		
		
		xstack.push( new Frame(f) );		
		Variable<?> _self = f.getSelf();
		
		/* map arguments */
		for(ArgumentDecl vd : lambda.getArgs()) {
			f.newArg(new Signature<Type>(vd.getLocalID(), vd.getType()),
					!vd.isUsedNonLocally() );
			vd.visitNode(this);						
		}
		
		/* traverse instructions */
		lambda.getBody().visitNode(this);	
				
		// our call frame
		Function.Frame cframe = f.getFrame();
		
		/* during traverse of instructions, some remaps might have happened 
		 * some variables want not our frame, but our parents. 
		 * In this case we need to provide the parent frame in our frame. */
		
		/* Closure's instance field holding the call frame value */
		KlassFieldRef pcall_frame = closure.getParentCallFrame();
		
		/* Add a copy to our call frame */
		KlassFieldRef pcf_ref = 
			cframe.newInstanceVar(pcall_frame.getSignature(), false);
		
		/* XXX :init the call frame */
		initCallFrame(lambda.getLine(), cframe);
		
		/* copy from closure to our call frame */
		{
			
			VariableValue v = new VariableValue(
				f, lambda.getLine(), pcall_frame.deref(_self) ); 
			
			Assignment a = new Assignment(f, lambda.getLine(),
				pcf_ref.deref(f.getCallFrameVar()), v);
			
			f.addOp(a);
		}					
		
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
