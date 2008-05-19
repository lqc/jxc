package org.lqc.jxc.javavm;

import java.util.Locale;

import org.lqc.jxc.CompilerException;
import org.lqc.jxc.il.Signature;
import org.lqc.jxc.types.FunctionType;
import org.lqc.jxc.types.PrimitiveType;
import org.lqc.jxc.types.Type;

/** Types supported natively by JVM. */
public class JType {	
	
	public static final JType INTEGER = new JType(1, "I", "i");
	public static final JType BOOLEAN = new JType(1, "Z", "i");
	public static final JType FLOAT = new JType(1, "F", "f");
	public static final JType DOUBLE = new JType(2, "D", "d");
	public static final JType LONG = new JType(2, "L", "l");
	
	public static final JType VOID = new JType(0, "V", "");
	public static final JType RETURNADDRESS = new JType(1, "", "");
	
	public static JType refType(String classname) 
	{
		return new JType(1, "L" + classname.replace('.', '/')+";", "a");
	}
	
	public static JType arrayType(JType t) 
	{
		return new JType(1, "["+t.signature, "a");
	}
	
	private int opsize;	
	private String signature;
	private String prefix;
	
	protected JType(int opsize, String sig, String isig) {
		this.opsize = opsize;
		this.signature = sig;
		this.prefix = isig;
	}
	
	public int opsize() {
		return opsize;		
	}
	
	public String toString() {
		return this.signature;
	}
	
	public static Type toILType(Class cls) {
		if(cls.equals(Integer.TYPE))
			return PrimitiveType.INT;
		
		if(cls.equals(Boolean.TYPE))
			return PrimitiveType.BOOLEAN;
		
		if(cls.equals(Double.TYPE))
			return PrimitiveType.REAL;
		
		if(cls.equals(Void.TYPE))
			return PrimitiveType.VOID;
		
		if(cls.equals(String.class))
			return PrimitiveType.STRING;
		
		/*if(cls.isAnnotationPresent()))
			return PrimitiveType.BOOLEAN; */
		return null;		
	}
	
	public static JType fromILType(Type t) {
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
		
		if(t instanceof FunctionType) 
			return new JType(1, fsig((FunctionType)t), "C");
		
		return refType("java.lang.Object");
	}
	
	public static int sizeof(Type t) {
		if(t.equals(PrimitiveType.REAL))
			return 2;
		
		return 1;
	}
		
	public String returnOp() {
		return prefix+"return";
	}
	
	public String loadVar() {
		return prefix + "load";
	}
	
	public String storeVar() {
		return prefix + "store";
	}
	
	public String prefix() {
		return prefix;
	}
	
	public String loadConstant(Object v) {
		if(this.equals(INTEGER))
			return String.format("ldc %d", (Integer)v);
		
		if(this.equals(BOOLEAN))
		{
			Boolean b = (Boolean)v;
			if( b.equals(Boolean.TRUE) ) 
				return "iconst_1";			
			else 
				return "iconst_0";
		}
		
		if(this.equals(DOUBLE))
			return String.format(Locale.UK,"ldc2_w %f", (Double)v);					
		
		if(signature.equals("Ljava/lang/String;"))
			return String.format("ldc \"%s\"", v);
		
		throw new CompilerException(
			String.format(
			"Type '%s' doesn't provide constant expressions",
			signature) );
	}
	
	private static String fsig(FunctionType ft) {
		StringBuffer buf = new StringBuffer();
		buf.append("Llang/jx/Function");
		for(Type t : ft.getArgumentTypes()) {			
			buf.append(JType.fromILType(t).prefix);			
		}
		buf.append("_");
		buf.append(JType.fromILType(ft.getReturnType()).prefix);
		buf.append(";");
		
		return buf.toString();
	}
	
	/**
	 * 
	 * Create a method signature for specified function signature
	 * 
	 */
	public static String methodSignature(Signature<FunctionType> sig) {
		StringBuffer buf = new StringBuffer();
		
		buf.append(sig.name);
		buf.append("(");
		
		for(Type t : sig.type.getArgumentTypes()) {
			buf.append(JType.fromILType(t).signature);									
		}
		buf.append(")");
		buf.append(JType.fromILType(sig.type.getReturnType()).signature);		
		
		return buf.toString();		
	}
}
