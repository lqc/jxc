package org.lqc.jxc.types;

public abstract class Type {	
	public static Type ANY = new AnyType();
	public static Type VOID = new VoidType();
	
	public boolean isSupertypeOf(Type t) {
		return false;		
	}
}
