package org.lqc.jxc.il;

import java.util.Vector;

import org.lqc.jxc.javavm.JType;
import org.lqc.jxc.types.FunctionType;
import org.lqc.jxc.types.Type;

public class Signature<T extends Type> {	
	public String name;
	public T type;
	
	public Signature(String name, T type) {	
		this.name = name;
		this.type = type;
	}	
	
	public Signature(java.lang.reflect.Method m) {
		this.name = m.getName();
		Type rt = JType.toILType(m.getReturnType());
		Vector<Type> args = new Vector<Type>();
		
		for(Class cls : m.getParameterTypes()) {
			args.add(JType.toILType(cls));			
		}
		
		this.type = (T)(new FunctionType(rt, args));
	}
	
	public String toString() {
		return name + ": " + type.toString();
	}
}
