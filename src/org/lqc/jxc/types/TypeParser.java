package org.lqc.jxc.types;

import java.util.List;
import java.util.Vector;

public class TypeParser {	
	
	private String str;	
	
	public TypeParser(String str) {
			this.str = str;	
	}
	
	public static Type parse(String str) {
		try {
			return (new TypeParser(str)).parseType();
		}
		catch (RuntimeException e) {
			e.printStackTrace();
			System.err.println(e.getMessage() + "  (parse " + str + ")");			
			return null;
		}
	}
	
	private void accept(String x) {
		if(!str.startsWith(x)) 
			throw new RuntimeException("Parse error at: " + x);
		
		move(x.length());
		str = str.trim();		
	}
	
	private char next() {
		return str.charAt(0);	
	}
	private void move(int n) {
		str = str.substring(n);
	}
	
	private String yieldUntilSpace() {
		StringBuffer buf = new StringBuffer();
		while( (next() != ' ') && (next() != '\t')) {
			buf.append(next());
			move(1);
		}
		
		return buf.toString();			
	}
	
	private Type parseType() {
		char tok;
		switch(tok = next()) {
			case '{':
				return parseFunctionType();
			case 'i':
				accept("int");
				return PrimitiveType.INT;
			case 'b':
				accept("boolean");
				return PrimitiveType.BOOLEAN;
			case 's':
				accept("string");
				return PrimitiveType.STRING;				
			case 'd':
				accept("double");
				return PrimitiveType.REAL;
			case 'v':
				accept("void");
				return Type.VOID;
			//case '&':				
				/* read until space ommiting ^ */
			//	String s = yieldUntilSpace().substring(1);				
			//	return parseObjectType();
			default:
				throw new RuntimeException("Type parse error at: " + tok);
		}		
	}
	
	public List<Type> parseTypeList() {
		Vector<Type> list = new Vector<Type>();		
		
		if( (next() == '-') || (next() == '}')) 
			return list;
				
		list.add(parseType());
		while(next() == ',') {
			accept(",");
			list.add(parseType());			
		}
		
		return list;			
	}

	private FunctionType parseFunctionType() {		
		this.accept("{");
		List<Type> args = this.parseTypeList();
		this.accept("->");
		Type rt = this.parseType();
		this.accept("}");
		
		return new FunctionType(rt, args);		
	}	
}
