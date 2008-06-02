package org.lqc.jxc.types;

import org.lqc.util.PartiallyOrdered;

public abstract class Type implements PartiallyOrdered<Type> {	
	public static Type ANY = new AnyType();
	public static Type VOID = new VoidType();	
	
	public abstract String getShorthand();
	
	public boolean equals(Object o) {
		if(! (o instanceof Type)) return false;
		
		switch( this.compareTo((Type)o)) {
			case EQUAL:
				return true;
			default:
				return false;
		}		
	}
	
	
}
