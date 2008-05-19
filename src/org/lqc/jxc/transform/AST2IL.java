package org.lqc.jxc.transform;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

import org.lqc.jxc.CompilerException;
import org.lqc.jxc.il.Assignment;
import org.lqc.jxc.il.Branch;
import org.lqc.jxc.il.Builtin;
import org.lqc.jxc.il.Call;
import org.lqc.jxc.il.Callable;
import org.lqc.jxc.il.CompoundOperation;
import org.lqc.jxc.il.Constant;
import org.lqc.jxc.il.Function;
import org.lqc.jxc.il.Loop;
import org.lqc.jxc.il.Module;
import org.lqc.jxc.il.Nop;
import org.lqc.jxc.il.Operation;
import org.lqc.jxc.il.Return;
import org.lqc.jxc.il.ReturnVoid;
import org.lqc.jxc.il.Signature;
import org.lqc.jxc.il.StaticContainer;
import org.lqc.jxc.il.Variable;
import org.lqc.jxc.il.VariableValue;
import org.lqc.jxc.tokens.ArgumentDecl;
import org.lqc.jxc.tokens.AssignmentInstr;
import org.lqc.jxc.tokens.BuiltinDecl;
import org.lqc.jxc.tokens.CompileUnit;
import org.lqc.jxc.tokens.ComplexInstr;
import org.lqc.jxc.tokens.CondInstr;
import org.lqc.jxc.tokens.ConstantExpr;
import org.lqc.jxc.tokens.Declaration;
import org.lqc.jxc.tokens.EmptyInstruction;
import org.lqc.jxc.tokens.Expression;
import org.lqc.jxc.tokens.ExternalFuncDecl;
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
import org.lqc.jxc.types.Type;


public class AST2IL implements TreeVisitor {
	
	private static class Frame 
	{
		public StaticContainer owner;
		public Queue<Operation> ops;
		public Stack<org.lqc.jxc.il.Expression> estack;
		
		public Frame(StaticContainer owner) {
			this.owner = owner;
			
			this.ops = new LinkedList<Operation>();
			this.estack = new Stack<org.lqc.jxc.il.Expression>();
		}		
	}
		
	private Stack<Frame> xstack;	
	
	private Map<VarDecl, Variable> vmap;
	private Map<FunctionDecl, Callable> fmap;
	
	private AST2IL() {
		vmap = new HashMap<VarDecl, Variable>();
		fmap = new HashMap<FunctionDecl, Callable>();
		
		xstack = new Stack<Frame>();
	}
	
	private StaticContainer container() {
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
	
	private void pushExpr(org.lqc.jxc.il.Expression e) {
		xstack.peek().estack.push(e);		
	}
	
	private org.lqc.jxc.il.Expression popExpr() {
		return xstack.peek().estack.pop();		
	}
	
	private boolean hasExpr() {
		return !xstack.peek().estack.isEmpty();
	}
	
	private org.lqc.jxc.il.Expression peekExpr() {
		return xstack.peek().estack.peek();
	}
	
	private Signature<FunctionType> sigFor(FunctionDecl f) {
		return	new Signature<FunctionType>(
				f.getID(), f.getType());		
	}
	
	public static Module convert(CompileUnit f) {		
		AST2IL cv = new AST2IL();
		Context builtin = f.getStaticContext();
		
		for(Declaration d : builtin.getAllDeclarations()) 
		{
			if(d instanceof ExternalFuncDecl)
				cv.fmap.put((FunctionDecl)d, 
					((ExternalFuncDecl)d).getCallable());
		}
		
		f.visitNode(cv);		
		
		return (Module)cv.xstack.peek().owner;
	}

	public void visit(CompileUnit file) 
	{		
		Module m = new Module(file.getName());		
		xstack.push(new Frame(m));
				
		for(FunctionDecl fd : file.getFunctions())
		{
			/* Construct signatures */
			Signature<FunctionType> sig = 
				new Signature<FunctionType>(
					fd.getID(), fd.getType());
			
			Signature<Type>[] args = 
				new Signature[fd.getArgs().size()];
			
			int i = 0;
			for(ArgumentDecl ad : fd.getArgs()) {
				args[i] = new Signature<Type>(
						ad.getID(), ad.getType());
				i++;
			}
			
			Callable f = container().newFunc(sig, args);
			fmap.put(fd, f);			
		}
		
		for(FunctionDecl fd : file.getFunctions())
		{		
			fd.visitNode(this);
		}
	}

	public void visit(ArgumentDecl decl) {
		this.visit((VarDecl)decl);
	}

	public void visit(FunctionDecl fd) {		
		/* TODO: this isn't good for nested functions */		
		Callable c = fmap.get(fd);
		
		/* put our frame on stack */
		if(c instanceof Function) {
			Function f = (Function)c;
			xstack.push( new Frame(f) );
		
			/* map arguments */
			for(ArgumentDecl vd : fd.getArgs())
				vd.visitNode(this);						
		
			/* traverse instructions */
			fd.getBody().visitNode(this);	
		
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
				decl.getID(), decl.getType());
		
		Variable v = container().newVar(sig);
		
		/* add a maping to global symbol table */
		vmap.put(decl, v);
		
		/* result is an assignment */
		if(!decl.getInitialValue().equals(Expression.NULL)) {			
			decl.getInitialValue().visitNode(this);
						
			org.lqc.jxc.il.Expression e = popExpr();
			appendOp( new Assignment(container(), decl.getLine(), v, e) );						
		}
		else {
			appendOp( new Nop(container(), decl.getLine()) );
		}
	}

	public void visit(FunctionCall call) {
		FunctionDecl fd = call.getRef();
		Callable ref;	
		
		ref = fmap.get(call.getRef());
		if(ref == null) {
			throw new CompilerException("[AST2IL] Fcall conversion failure of: " + call.getFid());
		}			
		
		Call c = new Call(container(), call.getLine(), ref);
		
		for(Expression e : call.getArgs()) {
			e.visitNode(this);			
			c.addArgument(popExpr());			
		}			
		
		/* push on to the stack */
		pushExpr(c);
	}

	public void visit(ConstantExpr c) {
		pushExpr(new Constant(container(), c.getLine(), 
				c.getType(), c.getValue()) );
	}

	public void visit(VarExpr var) {
		Variable v = vmap.get(var.getRef());		
		pushExpr( new VariableValue(container(),
				var.getLine(), v) );		
	}

	public void visit(ComplexInstr ci) {
		for(Instruction i : ci)
		{			
			i.visitNode(this);
			
			if(hasExpr()) {
				/* TODO only some expression are statements */ 
				appendOp(this.popExpr());
		
				/* more then 1 expression should yield an error */
				if(hasExpr()) {
					throw new CompilerException(
							"[INTERNAL] Unmatched expression left on operand stack.");
				}				
			} 
		}					
	}

	public void visit(AssignmentInstr as) {
		Variable v = vmap.get(as.getRef());
		
		as.getValue().visitNode(this);		
		org.lqc.jxc.il.Expression e = popExpr();
			
		appendOp( new Assignment(container(), as.getLine(), v, e));
	}
	
	public void visit(IncrementInstr i) {
		Variable v = vmap.get(i.getRef());
		
		
				
	}

	public void visit(LoopInstr loop) {
		/* append loop init block */
		loop.getInitInstr().visitNode(this);
		
		Expression e = loop.getCondition();
		e.visitNode(this);
		
		org.lqc.jxc.il.Expression ex = popExpr();
		
		CompoundOperation body = new CompoundOperation(
				container(), loop.getLine());
		
		/* embrance body and post */
		xstack.push( new Frame(container()));
		
		loop.getBody().visitNode(this);
		loop.getPostInstr().visitNode(this);
		
		while(moreOps())
			body.append(nextOp());		
		xstack.pop();
		
		/* append loop operation */
		appendOp(new Loop(container(), loop.getLine(), ex, body));
	}

	public void visit(ReturnInstr ret) {
		if(!ret.getValue().equals(Expression.VOID)) {
			ret.getValue().visitNode(this);
			
			org.lqc.jxc.il.Expression e = popExpr();			
			appendOp(new Return(container(), ret.getLine(), e));
			
			return;
		}
		
		appendOp(new ReturnVoid(container(), ret.getLine()));		
	}

	public void visit(CondInstr cond) {
		Expression e = cond.getCondition();
		e.visitNode(this);
		
		org.lqc.jxc.il.Expression ex = popExpr();
		
		CompoundOperation branchA, branchB;
		
		/* create a fictional frame */
		xstack.push( new Frame(container()));		
		/* transform "true" branch */
		cond.getBranch(true).visitNode(this);		
		
		branchA = new CompoundOperation(container(), 
				cond.getBranch(true).getLine());		
		while(moreOps())
			branchA.append(nextOp());			
		xstack.pop();
		
		/* same for second branch */
		xstack.push( new Frame(container()));		
		/* transform "true" branch */
		cond.getBranch(false).visitNode(this);		
		
		branchB = new CompoundOperation(container(), 
				cond.getBranch(false).getLine());
		
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

	

}
