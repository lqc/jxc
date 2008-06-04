package org.lqc.jxc.il;

import org.lqc.jxc.transform.ILVisitor;
import org.lqc.util.TriStateLogic;

public abstract class Operation {
	
	public int line;
	
	public Operation(StaticContainer<?> cont, int line) {
		slink = cont;
		reachable = TriStateLogic.FALSE;
		this.line = line;
	}
	
	protected StaticContainer<?> slink;	
	protected TriStateLogic reachable;
	
	public StaticContainer<?> slink() {
		return slink;
	}
	
	public boolean isNop() {
		return (this instanceof Nop);
	}
	
	public abstract <T> void visit(ILVisitor<T> v);

	public TriStateLogic isReachable() {
		return reachable;
	}
	
	public void setReachable(TriStateLogic reachable) {
		this.reachable = reachable;
	}
		
}
