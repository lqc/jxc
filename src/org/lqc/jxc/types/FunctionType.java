package org.lqc.jxc.types;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FunctionType extends Type {

	public FunctionType(Type t, List<Type> tl) {
		this.returnType = t;
		this.argsType = tl;
	}
	
	public FunctionType(Type rt, Type... types) {		
		this.returnType = rt;
		this.argsType = new ArrayList<Type>(types.length);
		for(Type t : types) this.argsType.add(t);		
	}

	private Type returnType;
	private List<Type> argsType;

	@Override
	public String toString() {
		StringBuffer bf = new StringBuffer("[func:");
		if (argsType != null) {
			if (argsType.size() == 1) {
				bf.append(argsType.get(0).toString());
			} else if (argsType.size() > 1) {
				for (Type t : argsType) {
					bf.append(t.toString());
					bf.append(", ");
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
	
	public List<Type> getArgumentTypes() {
		return this.argsType;
	}
	
	public int getArity() {
		return this.argsType.size();
	}

	/** Function type is comparable with other function types and "any". */ 
	public boolean isComparable(Type x) 
	{
		if (x instanceof AnyType) return true;
		
		if (x instanceof FunctionType) {
			FunctionType f = (FunctionType)x;
			
			/* If return types are not comparable, then f !~ g */
			if (!this.returnType.isComparable(f.returnType)) 
				return false;
			
			if (this.getArity() != f.getArity())
				return false;
			
			Iterator<Type> gi = this.argsType.iterator();
			Iterator<Type> fi = f.argsType.iterator();
			
			/* ( E(k) g.arg[k] !~ f.arg[k] ) => g !~ f */
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
		/* any is greater then any function type */
		if(x instanceof AnyType) return false;
		
		FunctionType f = (FunctionType)x;
		
		/* let this = g;
		 * (g.rt >= f.rt and FA(k) f.arg[k] >= g.arg[k])
		 *  	=> f ~ g	 	
		 */
		
		/* if g.rt > f.rt => f < g */ 
		if(! this.returnType.isGreaterEqual(f.returnType) )
			return false;
		
		Iterator<Type> gi = this.argsType.iterator();
		Iterator<Type> fi = f.argsType.iterator();
		
		/* ( E(k) g.arg[k] < f.arg[k] ) => g < f */
		while(gi.hasNext()) {
			Type gt = gi.next();
			Type ft = fi.next();
			
			if(! gt.isGreaterEqual(ft) ) return false;
		}
		
		return true;		
	}
}
