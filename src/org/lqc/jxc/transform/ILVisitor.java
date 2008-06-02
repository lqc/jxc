package org.lqc.jxc.transform;

import org.lqc.jxc.il.Assignment;
import org.lqc.jxc.il.Block;
import org.lqc.jxc.il.Branch;
import org.lqc.jxc.il.Call;
import org.lqc.jxc.il.Closure;
import org.lqc.jxc.il.Constant;
import org.lqc.jxc.il.Function;
import org.lqc.jxc.il.Loop;
import org.lqc.jxc.il.Return;
import org.lqc.jxc.il.ReturnVoid;
import org.lqc.jxc.il.TypeConversion;
import org.lqc.jxc.il.VariableValue;

public interface ILVisitor<T> {	
	public void begin(Function f);
	public void end(Function f);
	
	public void process(Assignment op);
	public void process(Block op);
	public void process(Branch op);	
	public void process(Call op);
	public <S> void process(Constant<S> op);
	public void process(Loop op);
	public void process(Return op);
	public void process(ReturnVoid op);	
	public void process(TypeConversion op);
	public void process(VariableValue op);
	public void process(Closure closure);
}
