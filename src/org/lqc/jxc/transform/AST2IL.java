package org.lqc.jxc.transform;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

import org.lqc.jxc.CompilerException;
import org.lqc.jxc.il.Assignment;
import org.lqc.jxc.il.Branch;
import org.lqc.jxc.il.Call;
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
import org.lqc.jxc.tokens.ComplexInstr;
import org.lqc.jxc.tokens.CondInstr;
import org.lqc.jxc.tokens.ConstantExpr;
import org.lqc.jxc.tokens.EmptyInstruction;
import org.lqc.jxc.tokens.Expression;
import org.lqc.jxc.tokens.CompileUnit;
import org.lqc.jxc.tokens.FunctionCall;
import org.lqc.jxc.tokens.FunctionDecl;
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
	private Map<FunctionDecl, Function> fmap;
	
	private AST2IL() {
		vmap = new HashMap<VarDecl, Variable>();
		fmap = new HashMap<FunctionDecl, Function>();
		
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
	
	public static Module convert(CompileUnit f) {		
		AST2IL cv = new AST2IL();				
		f.visitNode(cv);		
		
		return (Module)cv.xstack.peek().owner;
	}

	public void visit(CompileUnit file) 
	{		
		Module m = new Module(file.getName());		
		xstack.push( new Frame(m));
		
		for(FunctionDecl d : file.getFunctions())
			d.visitNode(this);		
	}

	public void visit(ArgumentDecl decl) {
		// ignore
	}

	public void visit(FunctionDecl fd) {
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
		
		Function f = container().newFunction(sig, args);
		fmap.put(fd, f);
		
		/* put our frame on stack */
		xstack.push( new Frame(f) );				
		
		/* traverse instructions */
		fd.getBody().visitNode(this);	
		
		/* instructions are left on operation list */
		while(moreOps())
			f.addOp(nextOp());
		
		/* remove our frame from stack */
		xstack.pop();
		
		/* function declarations do nothing */
		appendOp( new Nop(container()) );
	}

	public void visit(VarDecl decl) 
	{
		Signature<Type> sig = new Signature<Type>(
				decl.getID(), decl.getType());
		
		Variable v = container().newVariable(sig);
		
		/* add a maping to global symbol table */
		vmap.put(decl, v);
		
		/* result is an assignment */
		if(!decl.getInitialValue().equals(Expression.NULL)) {			
			decl.getInitialValue().visitNode(this);
						
			org.lqc.jxc.il.Expression e = popExpr();
			appendOp( new Assignment(container(), v, e) );						
		}
		else {
			appendOp( new Nop(container()) );
		}
	}

	public void visit(FunctionCall call) {		
		Call c = new Call(container(), fmap.get(call.getRef()));
		
		for(Expression e : call.getArgs()) {
			e.visitNode(this);			
			c.addArgument(popExpr());			
		}			
	}

	public void visit(ConstantExpr c) {
		pushExpr(new Constant(container(), c.getType(), c.getValue()) );
	}

	public void visit(VarExpr var) {
		Variable v = vmap.get(var.getRef());		
		pushExpr( new VariableValue(container(), v) );		
	}

	public void visit(ComplexInstr ci) {
		for(Instruction i : ci.getInstructions())
		{
			i.visitNode(this);
			
			if(hasExpr()) {
				/* TODO only some expression are statements */ 
				appendOp(this.peekExpr());
		
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
			
		appendOp( new Assignment(container(), v, e));
	}

	public void visit(LoopInstr loop) {
		/* append loop init block */
		loop.getInitInstr().visitNode(this);
		
		Expression e = loop.getCondition();
		e.visitNode(this);
		
		org.lqc.jxc.il.Expression ex = popExpr();
		
		CompoundOperation body = new CompoundOperation(container());
		
		/* embrance body and post */
		xstack.push( new Frame(container()));
		
		loop.getBody().visitNode(this);
		loop.getPostInstr().visitNode(this);
		
		while(moreOps())
			body.append(nextOp());		
		xstack.pop();
		
		/* append loop operation */
		appendOp(new Loop(container(), ex, body));
	}

	public void visit(ReturnInstr ret) {
		if(!ret.getValue().equals(Expression.VOID)) {
			ret.getValue().visitNode(this);
			
			org.lqc.jxc.il.Expression e = popExpr();			
			appendOp(new Return(container(), e));
			
			return;
		}
		
		appendOp(new ReturnVoid(container()));		
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
		
		branchA = new CompoundOperation(container());		
		while(moreOps())
			branchA.append(nextOp());			
		xstack.pop();
		
		/* same for second branch */
		xstack.push( new Frame(container()));		
		/* transform "true" branch */
		cond.getBranch(false).visitNode(this);		
		
		branchB = new CompoundOperation(container());		
		while(moreOps())
			branchA.append(nextOp());		
		xstack.pop();
		
		appendOp( new Branch(container(), ex, branchA, branchB));
	}

	public void visit(NullExpression v) {
		// XXX should/could push a "null" or something 
	}

	public void visit(EmptyInstruction v) {
		appendOp(new Nop(container()));
	}

}
