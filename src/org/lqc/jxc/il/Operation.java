package org.lqc.jxc.il;

import org.lqc.util.TriStateLogic;

public abstract class Operation {
	
	public Operation(StaticContainer cont) {
		slink = cont;
		reachable = TriStateLogic.FALSE;		
	}
	
	protected StaticContainer slink;	
	protected TriStateLogic reachable;
}
