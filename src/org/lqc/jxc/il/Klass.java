package org.lqc.jxc.il;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.lqc.jxc.javavm.FunctionAnnotation;
import org.lqc.jxc.javavm.JVMBranch;
import org.lqc.jxc.javavm.JVMPrimitive;
import org.lqc.jxc.javavm.JxNoExport;
import org.lqc.jxc.transform.ILVisitor;
import org.lqc.jxc.types.FunctionType;
import org.lqc.jxc.types.KlassType;
import org.lqc.jxc.types.Type;
import org.lqc.jxc.types.TypeParser;

/** 
 * 
 * Program module.
 *
 */
public class Klass extends Expression<KlassType> 
	implements StaticContainer 
{
	
	private static final StaticContainer NULL_CONTAINER = new NullContainer();	
		
	protected String moduleName;
	protected boolean isInterface;
	
	protected Map<Signature<Type>, Variable> vmap;
	protected Map<Signature<FunctionType>, Callable> fmap;
	
	public <T> Klass(Class<T> cls) {
		super(NULL_CONTAINER, -2, null);		
		this.type = new KlassType(this);
		
		/* reconstruct module definition from class */
		this.moduleName = cls.getName();		
		this.fmap = new HashMap<Signature<FunctionType>, Callable>();
		
		System.out.println("Importing " + moduleName);
		JxNoExport nex = cls.getAnnotation(JxNoExport.class);
		if(nex != null) {
			for(String s : nex.hidden_methods())
				System.out.println("Hidden field in module: " + s);
		}
		
		this.isInterface = cls.isInterface();
		
		for(Method m : cls.getMethods()) {
			Signature<FunctionType> fsig;
			Signature<Type>[] asigs;
									
			FunctionAnnotation fa = m.getAnnotation(FunctionAnnotation.class);
			 
			JVMPrimitive pa = m.getAnnotation(JVMPrimitive.class);
			JVMBranch ba = m.getAnnotation(JVMBranch.class);
			
			if(fa != null) { 
				FunctionType type = (FunctionType)TypeParser.parse(fa.type());				
				fsig = new Signature<FunctionType>(fa.name(), type);				
			}
			else {
				// if(pa != null || ba != null)
					fsig = new Signature<FunctionType>(m);
				/* {
					System.out.println("Ommiting: " + m.getName());
					continue; // ignore java native functions
				} */
			}			
			
			if(pa == null && ba != null) {				
				this.fmap.put(fsig, new BranchBuiltin(fsig, ba.value()) );
				continue;				
			}
						
			if(pa != null) {
				String bt = pa.value() + "\nifeq %s";
				
				if(ba != null)
					bt = ba.value();
				
				this.fmap.put(fsig, new Builtin(fsig, pa.value(), bt) );
				continue;
			}
											
						
			Function f = this.newFunc(0, fsig);
						
			int i = 0;
			for(Type t : fsig.type.getArgumentTypes()) { 
				f.newArg( new Signature<Type>("arg"+i, t) );
				i++;
			}
		}
	}
	
	
	public Klass(String name) {
		this(name, false);	
	}
	
	public Klass(String name, boolean bool) {
		super(NULL_CONTAINER, -2, null);
		this.type = new KlassType(this);
		
		this.moduleName = name;
		this.fmap = new HashMap<Signature<FunctionType>, Callable>();		
		 
		this.isInterface = bool;
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
	
	public Function newFunc(int line, String n, FunctionType t) 
	{
		return this.newFunc(line, new Signature<FunctionType>(n,t) );
	}
	
	public Function newFunc(int line, Signature<FunctionType> t) {
		Function f = new Function(this, line, t, 
				this.isInterface, !this.isInterface);
		fmap.put(t, f);
		
		return f;
	}
	
	public void remove(Callable f) {
		this.fmap.remove(f);
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
	
	public Collection<Callable> allCallables() {
		return fmap.values();
	}

	public Collection<Variable> allVariables() {
		return vmap.values();
	}
	
	private int _labelID = 0;

	public Label getUniqueLabel() {
		return new Label("Label" + _labelID++);		
	}
	
	public String getUniqueLambdaName() {		
		return moduleName + "$Lambda" + _labelID++;
	};

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

		public Function newFunc(int line, Signature<FunctionType> t) {
			throw new UnsupportedOperationException();				
		}

		public Variable newVar(Signature<Type> t) {				
			throw new UnsupportedOperationException();
		}

		public Collection<Callable> allCallables() {
			return Collections.EMPTY_LIST;
		}

		public Collection<Variable> allVariables() {
			return Collections.EMPTY_LIST;
		}

		public Label getUniqueLabel() {
			throw new UnsupportedOperationException();
		}

		public String absolutePath() {
			return "<null>";
		}

		public String name() {
			return null;
		}

		public String getUniqueLambdaName() {			
			throw new UnsupportedOperationException();
		}

		public void remove(Callable f) {
			throw new UnsupportedOperationException();
		}

		public Klass getNearestKlass() {
			throw new UnsupportedOperationException();
		}	
	}	
	
	public String toString() {
		return this.getModuleName();
	}


	/**
	 * @return the isInterface
	 */
	public boolean isInterface() {
		return isInterface;
	}


	@Override
	public <T> void visit(ILVisitor<T> v) {
		// 		
	}

	public Klass getNearestKlass() {
		return this;
	}	
}
