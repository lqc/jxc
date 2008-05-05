package org.lqc.jxc.il;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lqc.jxc.types.FunctionType;
import org.lqc.jxc.types.Type;

/** Definition of a standalone function. */
public class Function implements StaticContainer {
	
	/** Function name. */
	protected Signature<FunctionType> signature;
		
	/** List of local variables. */
	protected ArrayList<Variable> localVars;
	
	/** Variable mapping. */
	protected Map<Signature<Type>, Integer> vmap;
	
	/** Argument mapping. Need for calls. */
	protected Map<Signature<Type>, Integer> amap;		
				
	/** List of instructions. */
	protected List<Operation> ops;
	
	/** Static link. */
	protected StaticContainer slink;
	
	/** Identifier of last added variable */
	private int _lastID = -1;
	
	private int genLUID() {
		return ++_lastID;
	}
	
	public Function(StaticContainer container, 
			Signature<FunctionType> sig,
			Signature<Type>... args)
	{
		slink = container;
		signature = sig;
				
		vmap = new HashMap<Signature<Type>, Integer>();
		amap = new HashMap<Signature<Type>, Integer>();
		
		localVars = new ArrayList<Variable>();		
				 
		for(Signature<Type> s : args) {
			Variable v = new Variable(this, genLUID(), s);
			
			localVars.add(v.localID, v);
			amap.put(s, v.localID);
		}		
	}
	
	public Variable newVariable(Signature<Type> signature) {
		Variable v = new Variable(this, genLUID(), signature);
		
		localVars.add(v.localID, v);
		vmap.put(signature, v.localID);
		return v;
	}
	
	public Function getFunction(Signature<FunctionType> sig) {
		return slink.getFunction(sig);		
	}

	public Variable getVariable(Signature<Type> sig) {
		Variable v = localVars.get(vmap.get(sig));
		if(v == null) 
			return slink.getVariable(sig);
		
		return v;
	}	

	public StaticContainer parent() {
		return slink;
	}	
	
	
	public void addOp(Operation op) {
		/* TODO: do something more here ? */
		ops.add(op);
	}

	public Function newFunction(Signature<FunctionType> t,
			Signature<Type>... args) {
		throw new UnsupportedOperationException();
	}
}
