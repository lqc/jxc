package org.lqc.jxc.il;

import org.lqc.jxc.transform.ILVisitor;

public class Closure extends Klass {
	
	public Closure(Function f) {
		super(f.signature.name, false);
						
		f.signature.name = "_call";
		this.slink = f.slink;				
		f.slink = this;
		
		/* put the call */
		this.fmap.put(f.signature, f);
		
		/* we also need a constructor to wrap the frame */
		
	}

	public <T> void visit(ILVisitor<T> v) {
		v.process(this);
	}

}
