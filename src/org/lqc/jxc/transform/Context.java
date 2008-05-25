package org.lqc.jxc.transform;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.lqc.jxc.tokens.Declaration;
import org.lqc.jxc.tokens.FunctionDecl;
import org.lqc.jxc.tokens.VarDecl;
import org.lqc.jxc.types.FunctionType;
import org.lqc.jxc.types.Type;
import org.lqc.util.DAGraph;
import org.lqc.util.ElementNotFoundException;
import org.lqc.util.MultiplyMatchException;
import org.lqc.util.NonUniqueElementException;
import org.lqc.util.POSet;
import org.lqc.util.Tuple;

public class Context {
	
	public final static String RETURN_ID = "<RETURN>"; 

	private HashMap<String, POSet<Tuple<Type>, FunctionDecl>> fmap;
	private HashMap<String, VarDecl> vmap;
	private Context parent;
	private String name;

	public Context(Context parent, String name) {
		fmap = new HashMap<String, POSet<Tuple<Type>, FunctionDecl>>();
		vmap = new HashMap<String, VarDecl>();
		this.parent = parent;
		this.name = name;
	}

	public FunctionDecl getFunction(String id, FunctionType t)
			throws ElementNotFoundException, MultiplyMatchException
	{
		POSet<Tuple<Type>, FunctionDecl> set = fmap.get(id);
		try {
			return set.find(t.getArgumentTypes());
		} catch (ElementNotFoundException e) {
			if (parent != null)
				return parent.getFunction(id, t);
			throw e;
		} catch (NullPointerException e) {
			if (parent != null)
				return parent.getFunction(id, t);
			throw new ElementNotFoundException();
		}
	}

	public VarDecl getVariable(String id) throws ElementNotFoundException {
		VarDecl var = vmap.get(id);

		if (var != null)
			return var;

		if (parent != null)
			return parent.getVariable(id);

		throw new ElementNotFoundException();
	}

	public void put(FunctionDecl d) throws NonUniqueElementException {
		POSet<Tuple<Type>, FunctionDecl> set = fmap.get(d.getID());

		if (set == null) {
			set = new DAGraph<Tuple<Type>, FunctionDecl>();
			fmap.put(d.getID(), set);
		}

		set.insert(d.getType().getArgumentTypes(), d);
	}

	public void put(VarDecl d) throws NonUniqueElementException {
		if (vmap.containsKey(d.getID()))
			throw new NonUniqueElementException();

		vmap.put(d.getID(), d);
	}

	public String toString() {
		if (parent != null)
			return parent.toString() + " :: " + this.name;

		return this.name;
	}
	
	public Set<FunctionDecl> getAllFunctionDecl() {
		Set<FunctionDecl> v = new HashSet<FunctionDecl>();
		
		for(POSet<Tuple<Type>, FunctionDecl> poset : fmap.values()) {
			for(FunctionDecl d : poset.values()) {
				v.add(d);			
			}
		}				
		return v;
	}
	
	public Set<Declaration> getAllDeclarations() {
		Set<Declaration> v = new HashSet<Declaration>();
		
		for(POSet<Tuple<Type>, FunctionDecl> poset : fmap.values()) {
			for(FunctionDecl d : poset.values()) {
				v.add(d);			
			}
		}
		
		for(VarDecl d : vmap.values()) 
			v.add(d);
				
		return v;
	}
}
