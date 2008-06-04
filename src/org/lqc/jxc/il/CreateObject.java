package org.lqc.jxc.il;

import org.lqc.jxc.transform.ILVisitor;
import org.lqc.jxc.types.KlassType;

public class CreateObject extends Expression<KlassType> {
	
	public CreateObject(StaticContainer<?> cont, int line, KlassType t) {
		super(cont, line, t);
	}

	@Override
	public <T> void visit(ILVisitor<T> v) {
		// TODO Auto-generated method stub
	}

}
