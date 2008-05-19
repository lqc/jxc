package org.lqc.jxc.types;

import java.util.Collection;
import java.util.Iterator;

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
		StringBuffer bf = new StringBuffer("[func:");
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
		bf.append("]");

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
	

	/** Function type is comparable with other function types and "any". *//* 
	public boolean isComparable(Type x) 
	{
		if (x instanceof AnyType) return true;
		
		if (x instanceof FunctionType) {
			FunctionType f = (FunctionType)x;
			
			 If return types are not comparable, then f !~ g 
			if (!this.returnType.isComparable(f.returnType)) 
				return false;
			
			if (this.getArity() != f.getArity())
				return false;
			
			Iterator<Type> gi = this.argsType.iterator();
			Iterator<Type> fi = f.argsType.iterator();
			
			 ( E(k) g.arg[k] !~ f.arg[k] ) => g !~ f 
			while(gi.hasNext()) {
				Type gt = gi.next();
				Type ft = fi.next();
				
				if(! gt.isComparable(ft) ) 
					return false;
			}
			
			return true;		
		}	
		
		return false;
	}

	public boolean isGreaterEqual(Type x) {
		 any is greater then any function type 
		if(x instanceof AnyType) return false;
		
		FunctionType f = (FunctionType)x;
		
		 let this = g;
		 * (g.rt >= f.rt and FA(k) f.arg[k] >= g.arg[k])
		 *  	=> f ~ g	 	
		 
		
		 if g.rt > f.rt => f < g  
		if(! this.returnType.isGreaterEqual(f.returnType) )
			return false;
		
		Iterator<Type> gi = this.argsType.iterator();
		Iterator<Type> fi = f.argsType.iterator();
		
		 ( E(k) g.arg[k] < f.arg[k] ) => g < f 
		while(gi.hasNext()) {
			Type gt = gi.next();
			Type ft = fi.next();
			
			if(! gt.isGreaterEqual(ft) ) return false;
		}
		
		return true;		
	}*/

	public Relation compareTo(Type t) {
		//if(t instanceof FunctionType) 
		//	return this.compareTo((FunctionType)t);
		
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
}
