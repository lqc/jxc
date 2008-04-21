package org.lqc.jxc.types;

import org.lqc.util.PartialyComparable;

public abstract class Type implements PartialyComparable<Type> {	
	public static Type ANY = new AnyType();
	public static Type VOID = new VoidType();	
}
