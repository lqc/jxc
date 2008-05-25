package org.lqc.jxc.il;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.lqc.jxc.javavm.FunctionAnnotation;
import org.lqc.jxc.javavm.JVMBranch;
import org.lqc.jxc.javavm.JVMPrimitive;
import org.lqc.jxc.types.FunctionType;
import org.lqc.jxc.types.Type;
import org.lqc.jxc.types.TypeParser;

/** 
 * 
 * Program module.
 *
 */
public class Module implements StaticContainer {
	
	private static final StaticContainer NULL_CONTAINER = new NullContainer();	
	
	protected StaticContainer slink;	
	protected String moduleName;
	
	protected Map<Signature<Type>, Variable> vmap;
	protected Map<Signature<FunctionType>, Callable> fmap;
	
	public <T> Module(Class<T> cls) {
		/* reconstruct module definition from class */
		this.moduleName = cls.getName();
		slink = NULL_CONTAINER;
		this.fmap = new HashMap<Signature<FunctionType>, Callable>();
		
		for(Method m : cls.getMethods()) {
			Signature<FunctionType> fsig;
			Signature<Type>[] asigs;
									
			FunctionAnnotation fa = m.getAnnotation(FunctionAnnotation.class);			
			
			if(fa != null) { 
				FunctionType type = (FunctionType)TypeParser.parse(fa.type());				
				fsig = new Signature<FunctionType>(fa.name(), type);
				
			}
			else {
				fsig = new Signature<FunctionType>(m);				
			}
				
			JVMPrimitive pa = m.getAnnotation(JVMPrimitive.class);
			JVMBranch ba = m.getAnnotation(JVMBranch.class);
						
			if(pa != null) {
				String bt = pa.value() + "\nifeq %s";
				
				if(ba != null)
					bt = ba.value();
				
				this.fmap.put(fsig, new Builtin(fsig, pa.value(), bt) );
				continue;
			}
				
			asigs = new Signature[fsig.type.getArity()];
			int i=0;
			for(Type t : fsig.type.getArgumentTypes()) 
				asigs[i++] = new Signature<Type>("arg"+i, t);				
			
			
			this.newFunc(fsig, asigs);
		}
	}
	
	public Module(String name) {
		this.moduleName = name;
		this.fmap = new HashMap<Signature<FunctionType>, Callable>();		
		slink = NULL_CONTAINER; 
	}
	
	public Callable get(Signature<FunctionType> sig) {
		Callable f = fmap.get(sig);
		if(f == null)
			return slink.get(sig);
		
		return f;
	}

	public Variable get(Signature<Type> sig) {
		Variable v = vmap.get(sig);
		if(v == null)
			return slink.get(sig);
		
		return v;
	}

	public StaticContainer container() {
		return slink;
	}

	public Function newFunc(Signature<FunctionType> t,
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
	
	public Variable newVar(Signature<Type> t) {
		Variable v = new Variable(this, genLUID(), t);
		vmap.put(t, v);
		return v;
	}

	/**
	 * @return the moduleName
	 */
	public String getModuleName() {
		return moduleName;
	}
	
	public Collection<Callable> allFunctions() {
		return fmap.values();
	}

	public Collection<Variable> allVariables() {
		return vmap.values();
	}
	
	private int _labelID = 0;

	public Label getUniqueLabel() {
		return new Label("Label" + _labelID++);		
	}

	public String absolutePath() {
		if(slink.name() == null)
			return name();
		
		return slink.absolutePath() + "/" + name();		
		
	}

	public String name() {
		return this.moduleName;
	}
		
	private static class NullContainer implements StaticContainer {
		public Function get(Signature<FunctionType> t) {				
			return null;
		}

		public Variable get(Signature<Type> t) {			
			return null;
		}

		public StaticContainer container() {
			return null;
		}

		public Callable newFunc(Signature<FunctionType> t,
				Signature<Type>... args) {
			throw new UnsupportedOperationException();				
		}

		public Variable newVar(Signature<Type> t) {				
			throw new UnsupportedOperationException();
		}

		public Collection<Callable> allFunctions() {
			return Collections.EMPTY_LIST;
		}

		public Collection<Variable> allVariables() {
			return Collections.EMPTY_LIST;
		}

		public Label getUniqueLabel() {
			throw new UnsupportedOperationException();
		}

		public String absolutePath() {
			return "";
		}

		public String name() {
			return null;
		}	
	};
}
