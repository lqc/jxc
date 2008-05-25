package org.lqc.jxc.types;

import org.lqc.util.Relation;

public abstract class PrimitiveType extends Type {
	
	public static PrimitiveType INT = new IntegerType("int");
	public static PrimitiveType REAL = new RealType("double");
	public static PrimitiveType BOOLEAN = new BooleanType("boolean");
	public static PrimitiveType STRING = new StringType("string");
		
	private String name;
	
	protected PrimitiveType(String name) {
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
	
}
