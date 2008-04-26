package org.lqc.jxc;

import java.util.HashMap;

import org.lqc.jxc.tokens.FunctionDecl;
import org.lqc.jxc.tokens.VarDecl;
import org.lqc.jxc.types.Type;
import org.lqc.util.DAGraph;
import org.lqc.util.ElementNotFoundException;
import org.lqc.util.NonUniqueElementException;
import org.lqc.util.POSet;

public class Context {
	
	private HashMap<String, POSet<Type, FunctionDecl>> fmap;
	private HashMap<String, VarDecl> vmap;
	private Context parent;
		
	protected Context() {		
		this(null);	
	}
	
	public static Context getBuiltins() {
		return new Context();
	}
	
	public Context(Context parent) {
		fmap = new HashMap<String, POSet<Type, FunctionDecl>>();
		vmap = new HashMap<String, VarDecl>();
		this.parent = parent;
	}
		
	public FunctionDecl getFunction(String id, Type t)	
		throws ElementNotFoundException
	{
		POSet<Type, FunctionDecl> set = fmap.get(id);
		try {
			return set.find(t);
		} catch(ElementNotFoundException e) {
			return parent.getFunction(id, t);
		}
	}
	
	public VarDecl getVariable(String id)
		throws ElementNotFoundException
	{
		VarDecl var = vmap.get(id);
		return (var != null ? var : parent.getVariable(id));		
	}	
		
	public void put(FunctionDecl d)
		throws NonUniqueElementException
	{
		POSet<Type, FunctionDecl> set = fmap.get(d.getID());
		
		if(set == null) {
			set = new DAGraph<Type, FunctionDecl>();
			fmap.put(d.getID(), set);			
		}
		
		set.insert(d.getType(), d);			
	}
	
	public void put(VarDecl d)
		throws NonUniqueElementException
	{
		vmap.put(d.getID(), d);
	}

}
