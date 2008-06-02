package org.lqc.jxc.types;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.lqc.jxc.CompilerException;
import org.lqc.util.POUtil;
import org.lqc.util.Relation;
import org.lqc.util.Tuple;

public class FunctionType extends Type {
	
	public FunctionType(Type rt, Collection<Type> types) {		
		this.returnType = rt;
		this.argsType = new Tuple<Type>(types);				
	} 
	
	public FunctionType(Type rt, Type... types) {		
		this.returnType = rt;
		this.argsType = new Tuple<Type>(types);				
	}

	private Type returnType;
	private Tuple<Type> argsType;

	@Override
	public String toString() {
		StringBuffer bf = new StringBuffer("{");
		if (argsType != null) {
			Iterator<Type> i = argsType.iterator();
			if(i.hasNext()) {	
				Type t = i.next();
				bf.append(argsType.get(0).toString());				
				while(i.hasNext()) {
					t = i.next();
					bf.append(", ");
					bf.append(t.toString());					
				}
			}			
		}
		bf.append(" => ");
		bf.append(returnType.toString());
		bf.append("}");

		return bf.toString();
	}	
	
	public Type getReturnType() {
		return this.returnType;
	}
	
	public Tuple<Type> getArgumentTypes() {
		return this.argsType;
	}
	
	public int getArity() {
		return this.argsType.size();
	}
	

	public Relation compareTo(Type t) {
		if(t instanceof FunctionType) 
			return this.compareTo((FunctionType)t);
		
		return Relation.NONCOMPARABLE;	
	}
	
	public Relation compareTo(FunctionType t) {
		Relation ar = this.argsType.compareTo(t.argsType);
		Relation rr = this.returnType.compareTo(t.returnType);
				
		if(ar.equal() && rr.equal()) 
			return Relation.EQUAL;
		
		if(ar.greaterOrEqual() && rr.lesserOrEqual()) 
			return Relation.GREATER;
				
		if(ar.lesserOrEqual() && rr.greaterOrEqual())
			return Relation.LESSER;
		
		return Relation.NONCOMPARABLE;		
	}
	
	public void correct(FunctionType t) {
		Iterator<? extends Type> him = t.argsType.iterator(); 
		Iterator<? extends Type> me = this.argsType.iterator();
		
		List<Type> newlist = new Vector<Type>();
				
		while(him.hasNext() && me.hasNext()) {
			Type a = him.next();
			Type b = me.next();
			
			Type c = POUtil.min(a, b);
			if(c == null) {
				throw new CompilerException("[FT] Trying to correct type with non-comparable");
			}
			
			newlist.add(c);
		}
		
		if(him.hasNext() || me.hasNext()) {
			throw new CompilerException("[FT] Cannot correct type with type with diffrent arity");
		}
		
		this.argsType = new Tuple<Type>(newlist);
		
		if(this.returnType.equals(Type.ANY))
			this.returnType = t.returnType;
		else 
			this.returnType = POUtil.max(t.returnType, this.returnType);
	}
	
	public String getShorthand() 
	{
		StringBuffer b = new StringBuffer();
		b.append("Cq");
		for(Type t : argsType)
			b.append(t.getShorthand());
		b.append("w");
		b.append(returnType.getShorthand());
		b.append("e");		
		return b.toString();
	}
}
