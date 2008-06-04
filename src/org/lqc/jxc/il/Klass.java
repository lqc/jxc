package org.lqc.jxc.il;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import lang.jx.FunctionAnnotation;
import lang.jx.JVMBranch;
import lang.jx.JVMPrimitive;
import lang.jx.JxNoExport;

import org.lqc.jxc.javavm.JType;
import org.lqc.jxc.transform.ILVisitor;
import org.lqc.jxc.types.FunctionType;
import org.lqc.jxc.types.KlassType;
import org.lqc.jxc.types.Type;
import org.lqc.jxc.types.TypeParser;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/** 
 * 
 * Program module.
 *
 */
public class Klass extends Expression<KlassType> 
	implements StaticContainer<Klass> 
{	
	private static final StaticContainer<?> NULL_CONTAINER = new NullContainer();
					
	protected String klassName;
	protected Klass parentKlass;
	protected List<String> implementsNames;
	
	protected boolean isInterface;
	protected boolean external;
	
	protected Map<Signature<? extends Type>, Variable<Klass>> vmap;
	protected Map<Signature<FunctionType>, Callable> fmap;
		
	public <T> Klass(Class<T> cls) {
		super(NULL_CONTAINER, -2, null);
		
		this.type = new KlassType(this);
		this.fmap = new HashMap<Signature<FunctionType>, Callable>();
		
		/* reconstruct module definition from class */
		this.klassName = cls.getName();
		
		getKlassCache().put(cls.getCanonicalName(), this);
		
		System.out.println("Importing " + klassName);
		
		
		this.external = true;
		
		
		if(cls.equals(Object.class)) {
			this.parentKlass = this;		
		}
		else
			this.parentKlass = Klass.forJavaClass(cls.getSuperclass());
		
		this.implementsNames = null;		
		
		
		JxNoExport nex = cls.getAnnotation(JxNoExport.class);
		if(nex != null) {
			for(String s : nex.hidden_methods())
				System.out.println("Hidden field in module: " + s);
		}
		
		this.isInterface = cls.isInterface();
		
		for(Method m : cls.getDeclaredMethods()) 
		{
			// ommit private methods
			if((m.getModifiers() & Modifier.PRIVATE) != 0)
				continue;
			
			Signature<FunctionType> fsig;			
									
			FunctionAnnotation fa = m.getAnnotation(FunctionAnnotation.class);
			JVMPrimitive pa = m.getAnnotation(JVMPrimitive.class);
			JVMBranch ba = m.getAnnotation(JVMBranch.class);
			
			if(fa != null) { 
				FunctionType type = (FunctionType)TypeParser.parse(fa.type());				
				fsig = new Signature<FunctionType>(fa.name(), type);				
			}
			else {
				// if(pa != null || ba != null)
				fsig = Klass.signFor(m);			
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
						
			Function f = this.newFunc(0, fsig, 
				(m.getModifiers() & Modifier.STATIC) != 0);
						
			int i = 0;
			for(Type t : fsig.type.getArgumentTypes()) { 
				f.newArg( new Signature<Type>("arg"+i, t) );
				i++;
			}
		}
		
		// add constructores 
		for(Constructor<?> c : cls.getDeclaredConstructors())
		{
			if((c.getModifiers() & Modifier.PRIVATE) != 0)
				continue;
			
			Signature<FunctionType> fsig = Klass.signFor(c);
			Function f = this.newFunc(0, fsig, false);
			
			f.newArg( new Signature<KlassType>("_self", 
					parentKlass.getType() ));
			
			int i = 0;
			for(Type t : fsig.type.getArgumentTypes()) { 
				f.newArg( new Signature<Type>("arg"+i, t) );
				i++;
			}			
		}	
		
				
	}
	
	private static Signature<FunctionType> signFor(java.lang.reflect.Method m) 
	{		
		Type rt = JType.toILType(m.getReturnType());
		Vector<Type> args = new Vector<Type>();
		
		for(Class<?> cls : m.getParameterTypes()) {
			args.add(JType.toILType(cls));			
		}
		return new Signature<FunctionType>(
				m.getName(), new FunctionType(rt, args));
	}
	
	private static Signature<FunctionType> 
		signFor(java.lang.reflect.Constructor<?> m) 
	{		
		Vector<Type> args = new Vector<Type>();
		for(Class<?> cls : m.getParameterTypes()) {
			args.add(JType.toILType(cls));			
		}

		return new Signature<FunctionType>(
				"<init>", new FunctionType(Type.VOID, args));
	}
	
	public Klass(String name) {
		this(name, false);
	}
	
	public Klass(String name, boolean isinterface) {
		this((isinterface ? 
				forJavaClass(Object.class) : 
				forJavaClass(lang.jx.Module.class) ), 
				name, isinterface);
	}
	
	public Klass(Klass base, String name) {
		this(base, name, false);	
	}
	
	public Klass(Klass base, String name, boolean bool) {
		super(NULL_CONTAINER, -2, null);
		this.type = new KlassType(this);
		
		this.klassName = name;
		this.parentKlass = base;
		this.implementsNames = new Vector<String>();
		this.fmap = new HashMap<Signature<FunctionType>, Callable>();		
		this.vmap = new HashMap<Signature<? extends Type>, Variable<Klass>>();
		 
		this.isInterface = bool;
		this.external = false;		
	}
	
	public Callable get(String name, Type rt, Type... args) {
		return this.get( new Signature<FunctionType>(
				name, new FunctionType(rt, args) ));		
	}
	
	public Callable getLocal(String name, Type rt, Type... args) {
		return this.getLocal( new Signature<FunctionType>(
				name, new FunctionType(rt, args) ));		
	}
	
	public Callable getLocal(Signature<FunctionType> sig) {
		return fmap.get(sig);		
	}
		
	public Callable get(Signature<FunctionType> sig) {
		Callable f = getLocal(sig);		
		if((f == null) && (slink != null))
			return slink.get(sig);
		
		return f;
	}

	public Variable<?> get(Signature<Type> sig) {
		Variable<Klass> v = vmap.get(sig);
		if(v == null)
			return slink.get(sig);
		
		return v;
	}

	public StaticContainer<?> container() {
		return slink;
	}
	
	public Function newFunc(int line, String n, FunctionType t, boolean isstatic) 
	{
		return this.newFunc(line, new Signature<FunctionType>(n,t), isstatic );
	}
	
	public Function newFunc(int line, Signature<FunctionType> t, boolean isstatic) {
		Function f = new Function(this, line, t, 
				this.isInterface, isstatic && !this.isInterface, false);
		fmap.put(t, f);
		
		return f;
	}
	
	public void remove(Callable f) {
		this.fmap.remove(f);
	}
	
	public Variable<Klass> newInstanceVar(Variable<?> instance,
			Signature<? extends Type> t, boolean islocal) 
	{
		Variable<Klass> v = new KlassField(this, instance, t);
		vmap.put(t, v);
		return v;
	}

	/**
	 * @return the moduleName
	 */
	public String getKlassName() {
		return klassName;
	}
	
	public Collection<Callable> allCallables() {
		return fmap.values();
	}

	public Collection<Variable<?>> allVariables() {
		return (Collection)vmap.values();
	}
	
	private int _labelID = 0;

	public Label getUniqueLabel() {
		return new Label("Label" + _labelID++);		
	}
	
	public String getUniqueLambdaName() {		
		return klassName + "$Lambda" + _labelID++;
	};

	public String absolutePath() {
		if(slink.name() == null)
			return name();
		
		return slink.absolutePath() + "/" + name();		
		
	}

	public String name() {
		return this.klassName;
	}
		
	private static class NullContainer 
			implements StaticContainer<NullContainer> 
	{
		public Function get(Signature<FunctionType> t) {				
			return null;
		}

		public Variable<?> get(Signature<Type> t) {			
			return null;
		}

		public StaticContainer<?> container() {
			return null;
		}

		public Function newFunc(int line, Signature<FunctionType> t, boolean b) {
			throw new UnsupportedOperationException();				
		}

		public Variable<NullContainer> newVar(Signature<? extends Type> t, boolean islocal) {				
			throw new UnsupportedOperationException();
		}

		public Collection<Callable> allCallables() {
			return Collections.EMPTY_LIST;
		}

		public Collection<Variable<?>> allVariables() {
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
		return this.getKlassName();
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

	/**
	 * @return the parentKlassName
	 */
	public Klass getBaseKlass() {
		return parentKlass;
	}	
	
	public void addImplements(String klassName) {
		this.implementsNames.add(klassName);
	}
	
	public List<String> getImplementsNames() {
		return this.implementsNames;
	}
	
	private static HashMap<String, Klass> klass_cache;
		
	public static Map<String, Klass> getKlassCache() {
		if(klass_cache == null) {
			klass_cache = new HashMap<String, Klass>();					
		}
		
		return klass_cache;		
	}
	
	public static Klass forJavaClass(Class<?> cls) 
	{			
		Klass k = getKlassCache().get(cls.getCanonicalName());
		
		if(k == null) { 
			k = new Klass(cls);
			klass_cache.put(cls.getCanonicalName(), k);
		}
		
		return k;		
	}
		
	/**
	 * @return the external
	 */
	public boolean isExternal() {
		return external;
	}

	public Variable<Klass> newVar(Signature<? extends Type> t, boolean islocal) {
		throw new NotImplementedException();
	}	
	
}
