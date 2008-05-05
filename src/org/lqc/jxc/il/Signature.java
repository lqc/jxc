package org.lqc.jxc.il;

import org.lqc.jxc.types.Type;

public class Signature<T extends Type> {	
	public String name;
	public T type;
	
	public Signature(String name, T type) {	
		this.name = name;
		this.type = type;
	}

}
