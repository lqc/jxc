package org.lqc.jxc.types;

public class PrimitiveType extends Type {
	
	public static PrimitiveType INT = new PrimitiveType("int");
	public static PrimitiveType REAL = new PrimitiveType("double");
	public static PrimitiveType BOOLEAN = new PrimitiveType("boolean");
	public static PrimitiveType STRING = new PrimitiveType("string");
		
	private String name;
	
	private PrimitiveType(String name) {
		this.name = name;
	}	
	
	public static PrimitiveType forValue(Object c) {
		if(c instanceof Integer) 
			return PrimitiveType.INT;
		else if(c instanceof Double) 
			return PrimitiveType.REAL;
		else if(c instanceof Boolean)
			return PrimitiveType.BOOLEAN;
		else if(c instanceof String)
			return PrimitiveType.STRING;
	
		throw new RuntimeException("Unknown primitive type: " + c);
	}
	
	@Override
	public String toString() {
		return "p:"+ name;
	}

	public boolean isComparable(Type x) {
		return x.equals(ANY) || x.equals(this);		
	}

	public boolean isGreaterEqual(Type x) {		
		return x.equals(this);
	}
	
	
}
