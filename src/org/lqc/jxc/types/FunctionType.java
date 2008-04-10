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

	@Override
	public boolean isSupertypeOf(Type t) {
		if(t instanceof FunctionType) {
			FunctionType ft = (FunctionType)t;		
			boolean result = this.returnType.isSupertypeOf(ft.getReturnType());
			
			Iterator<Type> myIter = this.argsType.iterator();
			Iterator<Type> ftIter = ft.getArgumentTypes().iterator();
			
			while( myIter.hasNext() && ftIter.hasNext() && result) {
				Type a = myIter.next();
				Type b = ftIter.next();
			
				result = result && b.isSupertypeOf(a);
			}
			
			return result;
		}
		
		return super.isSupertypeOf(t);		
	}
	
	public Type getReturnType() {
		return this.returnType;
	}
	
	public List<Type> getArgumentTypes() {
		return this.argsType;
	}
}
