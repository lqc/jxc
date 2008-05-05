package org.lqc.jxc.il;

import java.util.Map;

import org.lqc.jxc.types.FunctionType;
import org.lqc.jxc.types.Type;

/** 
 * 
 * Program module.
 *
 */
public class Module implements StaticContainer {
	
	protected StaticContainer slink;	
	protected String moduleName;
	
	protected Map<Signature<Type>, Variable> vmap;
	protected Map<Signature<FunctionType>, Function> fmap;
	
	public Module(String name) {
		this.moduleName = name;
		
		slink = new StaticContainer() {
			public Function getFunction(Signature<FunctionType> t) {				
				return null;
			}

			public Variable getVariable(Signature<Type> t) {			
				return null;
			}

			public StaticContainer parent() {
				return null;
			}

			public Function newFunction(Signature<FunctionType> t,
					Signature<Type>... args) {
				throw new UnsupportedOperationException();				
			}

			public Variable newVariable(Signature<Type> t) {
				// TODO Auto-generated method stub
				throw new UnsupportedOperationException();
			}			
		};
	}
	
	public Function getFunction(Signature<FunctionType> sig) {
		Function f = fmap.get(sig);
		if(f == null)
			return slink.getFunction(sig);
		
		return f;
	}

	public Variable getVariable(Signature<Type> sig) {
		Variable v = vmap.get(sig);
		if(v == null)
			return slink.getVariable(sig);
		
		return v;
	}

	public StaticContainer parent() {
		return slink;
	}

	public Function newFunction(Signature<FunctionType> t,
			Signature<Type>... args) {
		Function f = new Function(this, t, args);
		fmap.put(t, f);
		
		return f;
	}

	/** Identifier of last added variable */
	private int _lastID = -1;
	
	private int genLUID() {
		return ++_lastID;
	}
	
	public Variable newVariable(Signature<Type> t) {
		Variable v = new Variable(this, genLUID(), t);
		vmap.put(t, v);
		return v;
	}}
