package org.lqc.jxc.types;

import org.lqc.util.PartiallyOrdered;

public abstract class Type implements PartiallyOrdered<Type> {	
	public static Type ANY = new AnyType();
	public static Type VOID = new VoidType();	
}
