package org.lqc.jxc.javavm;

import org.lqc.jxc.types.PrimitiveType;
import org.lqc.jxc.types.Type;

/** Types supported natively by JVM. */
public class JType {	
	
	public static final JType INTEGER = new JType(1, "I");
	public static final JType BOOLEAN = new JType(1, "B");
	public static final JType FLOAT = new JType(1, "F");
	public static final JType DOUBLE = new JType(2, "D");
	public static final JType LONG = new JType(2, "L");
	
	public static final JType VOID = new JType(0, "V");
	public static final JType RETURNADDRESS = new JType(1, "");
	
	public static JType refType(String classname) 
	{
		return new JType(1, classname.replace('.', '/')+";");
	}
	
	public static JType arrayType(JType t) 
	{
		return new JType(1, "["+t.signature);
	}
	
	private int opsize;	
	private String signature;
	
	private JType(int opsize, String sig) {
		this.opsize = opsize;
		this.signature = sig;
	}
	
	public int size() {
		return opsize;		
	}
	
	public String toString() {
		return this.signature;
	}
	
	public static JType fromType(Type t) {
		if(t.equals(PrimitiveType.INT)) 
			return INTEGER;
		
		if(t.equals(PrimitiveType.BOOLEAN)) 
			return BOOLEAN;
		
		if(t.equals(PrimitiveType.REAL)) 
			return DOUBLE;
		
		if(t.equals(PrimitiveType.STRING))
			return refType("java.lang.String");
		
		if(t.equals(Type.VOID))
			return VOID;
		
		return refType("java.lang.Object");
	}
}
