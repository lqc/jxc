package org.lqc.jxc.il;

import org.lqc.jxc.types.Type;

public class Signature<T extends Type> {	
	public String name;
	public T type;
	
	public Signature(String name, T type) {	
		this.name = name;
		this.type = type;
	}	
	
	public Signature(Signature<? extends T> signature) {
		this.name = signature.name;
		this.type = signature.type;
	}

	public String toString() {
		return name + ": " + type.toString();
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(! (o instanceof Signature)) {
			return false;
		}
		
		Signature<? extends Type> sig = (Signature<? extends Type>)o;

		return name.equals(sig.name) && type.equals(sig.type);
		
	}
}
