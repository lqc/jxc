package org.lqc.jxc.transform;

import java.util.Stack;

import org.lqc.jxc.il.Assignment;
import org.lqc.jxc.il.Block;
import org.lqc.jxc.il.Branch;
import org.lqc.jxc.il.Call;
import org.lqc.jxc.il.Constant;
import org.lqc.jxc.il.Function;
import org.lqc.jxc.il.Loop;
import org.lqc.jxc.il.Operation;
import org.lqc.jxc.il.Return;
import org.lqc.jxc.il.ReturnVoid;
import org.lqc.jxc.il.TypeConversion;
import org.lqc.jxc.il.VariableValue;

public abstract class AbstractILVisitor<T> implements ILVisitor<T> {
	
	private Stack<T> contextStack;
	
	protected abstract T createContext();
	protected abstract void finalizeContext(T ctx);
	
	protected AbstractILVisitor() {
		contextStack = new Stack<T>();
	}

	protected void push(T context) {
		contextStack.push(context);
	}
	
	protected T pop() {
		return contextStack.pop();		
	}

	protected T current() {
		if(contextStack.isEmpty())
			return null;
		else
			return contextStack.peek();
	}
	
	public void begin(Function f) {
		push(createContext());
		enter(f);
	}

	public void end(Function f) {
		leave(f);
		finalizeContext(pop());		
	}

	public void process(Assignment op) {
		enter(op);
		op.getArgument().visit(this);
		leave(op);
	}

	public void process(Branch op) {
		T a, b;
		
		push(createContext());
		enter(op);		
		op.getCondition().visit(this);
				
		push(createContext());
		beforeBranchA(op);
		op.getOperationA().visit(this);
		afterBranchA(op);
		finalizeContext(a = pop());
		
		push(createContext());
		beforeBranchB(op);
		op.getOperationB().visit(this);
		afterBranchB(op);
		finalizeContext(b = pop());
		
		leave(op, a, b);
		finalizeContext(pop());
	}
	
	public void process(Block op) {
		push(createContext());
		enter(op);
		for(Operation o : op)
			o.visit(this);
		leave(op);
		finalizeContext(pop());
	}

	public void process(Call op) {
		enter(op);
		for(Operation arg : op.args())
			arg.visit(this);		
		leave(op);
	}	

	public void process(Loop op) 
	{		
		push(createContext());
		enter(op);		
		op.getBodyBlock().visit(this);
		leave(op);
		finalizeContext(pop());
	}

	public void process(Return op) {	
		enter(op);
		op.returnValue.visit(this);
		leave(op);
	}
		
	protected abstract void enter(Function f);
	protected abstract void leave(Function f);
	
	protected abstract void leave(Assignment op);
	protected abstract void enter(Assignment op);
	
	protected abstract void leave(Block op);
	protected abstract void enter(Block op);
	
	protected abstract void enter(Branch op);
	protected abstract void beforeBranchA(Branch op);
	protected abstract void afterBranchA(Branch op);
	protected abstract void beforeBranchB(Branch op);
	protected abstract void afterBranchB(Branch op);
	protected abstract void leave(Branch op, T a, T b);
	
	protected abstract void enter(Call op);
	protected abstract void leave(Call op);
	
	protected abstract void enter(Loop op);
	protected abstract void leave(Loop op);
	
	protected abstract void enter(Return op);
	protected abstract void leave(Return op);
	
	public abstract void process(VariableValue op);
	public abstract void process(TypeConversion op);
	public abstract void process(ReturnVoid op);
	public abstract <S> void process(Constant<S> op);
}
