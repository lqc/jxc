package org.lqc.jxc;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.lqc.jxc.tokens.Declaration;
import org.lqc.jxc.types.Type;

public class Environment {
	
	private HashMap<String, List<Declaration>> mappings;
	private Environment parent;
		
	protected Environment() {
		mappings = new HashMap<String, List<Declaration>>();
		parent = null;
	}
	
	public static Environment getBuiltins() {
		return new Environment();
	}
	
	public Environment(Environment parent) {
		mappings = new HashMap<String, List<Declaration>>();
		this.parent = parent;
	}
	
	public List<Declaration> getMapping(String id)
		throws UnmatchedIdentifierException	
	{
		List<Declaration> l = mappings.get(id);
		if(l == null) 
			if(parent != null)
				return parent.getMapping(id);
			else 
				throw new UnmatchedIdentifierException(id);		
		return l;
	}
	
	public Declaration getMapping(String id, Type t)
		throws UnmatchedIdentifierException
	{	 
		for(Declaration d : mappings.get(id)) {
			if(d.getType().equals(t)) return d;
		}
		
		if(parent != null)
			return parent.getMapping(id, t);
		else 
			throw new UnmatchedIdentifierException(id, t);	
	}
	
	public void putMapping(Declaration d) {
		List<Declaration> l = mappings.get(d.getID());
		if(l == null){
			l = new Vector<Declaration>();
			mappings.put(d.getID(), l);
		}
		l.add(d);		
	}

}
