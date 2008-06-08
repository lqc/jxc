package org.lqc.jxc.javavm;

import java.lang.reflect.Field;
import java.util.Locale;

import org.lqc.jxc.CompilerException;
import org.lqc.jxc.il.Signature;
import org.lqc.jxc.types.FunctionType;
import org.lqc.jxc.types.KlassType;
import org.lqc.jxc.types.PrimitiveType;
import org.lqc.jxc.types.Type;
import org.lqc.jxc.types.TypeParser;

/** Types supported natively by JVM. */
public class JType {	
	
	public static int REFERENCE_SIZE = 1;
	
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
	
	public static Type toILType(Class<?> cls) {
		Type t = null;
		
		if(cls.equals(Integer.TYPE))
			t = PrimitiveType.INT;
		else if(cls.equals(Boolean.TYPE))
			t = PrimitiveType.BOOLEAN;
		else if(cls.equals(Double.TYPE))
			t = PrimitiveType.REAL;
		else if(cls.equals(Void.TYPE))
			t = PrimitiveType.VOID;
		else if(cls.equals(String.class))
			t = PrimitiveType.STRING;
		else if(cls.isInterface() 
			&& lang.jx.Closure.class.isAssignableFrom(cls)) 
		{
			try {
				Field f = cls.getField("_type_signature");				 
				t = TypeParser.parse( (String) f.get(null) );
			} catch (SecurityException e) {				
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {				
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}			
		}
		
		if(t == null)
			System.out.println("[Warning] Couldn't covert " + cls.getCanonicalName());
		else
			System.out.printf("JType %s is %s\n", cls.getCanonicalName(), t.toString());
				
		return t;
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
			return refType("Closure" + t.getShorthand());
		
		if(t instanceof KlassType) 
			return refType(((KlassType)t).getKlass().getKlassName());
		
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
	
	public String loadVar(int num) {
		if(num < 3)
			return prefix + "load_" + num;
		else
			return prefix + "load " + num;
	}
	
	public String storeVar(int num) {
		if(num < 3)
			return prefix + "store_" + num;
		else
			return prefix + "store " + num;
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
