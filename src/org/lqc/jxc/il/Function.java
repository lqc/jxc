package org.lqc.jxc.il;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.lqc.jxc.CompilerException;
import org.lqc.jxc.javavm.JType;
import org.lqc.jxc.transform.ILVisitor;
import org.lqc.jxc.types.FunctionType;
import org.lqc.jxc.types.KlassType;
import org.lqc.jxc.types.Type;

/** Definition of a standalone function. */
public class Function implements StaticContainer<Function>, Callable,
	Iterable<Operation>
{		
	public static class Frame extends Klass {		
		private static String produceName(String n) {
			int i = n.lastIndexOf('/');
			String base = n.substring(0, i);
			String fname = n.substring(i+1);
			
			return base + "$" + fname + "_frame";			
		}		
		
		public Frame(Function f) {
			super(Klass.forJavaClass(Object.class), produceName(f.getAbsoluteName()) );
			
			this.slink = f;
		}
		
		public boolean isEmpty() {
			return this.vmap.isEmpty();
		}
	}
	
	public static String escapeFieldName(String name) {
		return name.replace('/', 'Z').replace('$', 'X');
	}
	
	/** Number of the last label generated. */ 
	private int _labelID = 0;
		
	/** Identifier of last added variable. */
	private int _lastID = -1;
	
	/** Argument mapping. Need for calls. */
	protected Map<Signature<? extends Type>, Integer> amap;
	
	/** Local field holding the frame. */ 
	protected Variable<?> callFrameVar;
	
	/** Function call signature. */
	protected List<Type> catypes;		
	
	/** Frame for exported/non-local variables. */
	protected Frame frame;
				
	/** Is this an abstract function definition. */
	protected boolean isAbstract;
	
	/** Is this function declared inside some other funciton 
	 * and thus needs local variable translation. */
	protected boolean isLambda;
		
	/** Is this an abstract function definition. */
	protected boolean isStatic;
	
	/** Source line of declaration */
	protected int line;
	
	/** List of local variables. */
	protected ArrayList<Variable<?>> localVars;
	
	/** localID to address map. */
	protected int[] lvmap;
	
	/** List of instructions. */
	protected List<Operation> ops;
	
	/** Argument mapping. Need for calls. */
	protected Map<Variable<?>, Variable<?>> proxy_map;
	
	/** If this is an instance method, the _self argument. */ 	
	protected Variable<?> selfVar;
	
	/** Function declared signature. */
	protected Signature<FunctionType> signature;
	
	/** Static link. */
	protected Klass slink;
					
	/** Variable mapping. */
	protected Map<Signature<? extends Type>, Integer> vmap;
	
	public Function(Klass container,
			int line, Signature<FunctionType> sig,
			boolean abs, boolean stat, boolean lambda)
	{		
		slink = container;
		signature = sig;
		catypes = new Vector<Type>();
		
		this.line = line;
				
		vmap = new HashMap<Signature<? extends Type>, Integer>();
		amap = new HashMap<Signature<? extends Type>, Integer>();
		proxy_map = new HashMap<Variable<?>, Variable<?>>();
		
		localVars = new ArrayList<Variable<?>>();		
		ops = new Vector<Operation>();
		
		if(! slink.isExternal()) {
			frame = new Frame(this);
			Signature<KlassType> ffsig = 
				new Signature<KlassType>(
						Function.escapeFieldName("frameof_" + 
								container.getKlassName() + "/" + sig.name)
					, frame.type );
			
			this.callFrameVar = this.newVar(ffsig, true);
		}
						
		this.isAbstract = abs;
		this.isStatic = stat;
		this.isLambda = lambda;
		
		if(!isStatic) this.newSelf();

	}
	
	public String getAbsoluteName() {		
		return slink.getAbsoluteName() + "/" + this.signature.name;
	}
	
	public void addOp(Operation op) {		
		ops.add(op);
	}
	
	public Collection<Callable> allCallables() {
		return Collections.EMPTY_LIST;
	}
	
	public Collection<Variable<?>> allVariables() {
		Collection<Variable<?>> copy = 
			(Collection<Variable<?>>) localVars.clone();
		
		copy.addAll(frame.allVariables());
		
		return copy;		
	}

	public Signature<FunctionType> callSignature() {
		return new Signature<FunctionType>(signature.name, 
				new FunctionType(signature.type.getReturnType(), catypes) );		
	}	
	
	public Klass container() {
		return slink;
	}

	public Signature<FunctionType> declSignature() {
		return signature;
	}
	
	private int genLUID() {
		return ++_lastID;
	}

	public Callable get(Signature<FunctionType> sig) {
		return slink.get(sig);		
	}

	public Variable<?> get(Signature<Type> sig) 
	{
		Variable<?> v;
		Integer idx = vmap.get(sig);
		
		if(idx != null)
			v = localVars.get(vmap.get(sig));
		else {
			v = frame.get(this.callFrameVar, sig);
		}
		
		if(v == null) 
			return slink.get(sig);
		
		return v;
	}
		
	public Variable<?> getCallFrameVar() {
		return callFrameVar;
	}
	
	public Frame getFrame() {
		return this.frame;		
	}

	public int[] getLVMap() {
		return lvmap;
	}

	public Klass getNearestKlass() {
		return slink.getNearestKlass();
	}
	
	public Variable<?> getSelf() {
		return this.selfVar;
	}
	public Label getUniqueLabel() {
		return new Label("Label" + _labelID++);		
	}

	public String getUniqueLambdaName() {
		return this.slink.getUniqueLambdaName();		
	}

	/**
	 * @return the isAbstract
	 */
	public boolean isAbstract() {
		return isAbstract;
	}

	/**
	 * @return the isLambda
	 */
	public boolean isLambda() {
		return isLambda;
	}
	
	/**
	 * @return the isStatic
	 */
	public boolean isStatic() {
		return isStatic;
	}
	public Iterator<Operation> iterator() {
		return ops.iterator();
	}	
	
	public int lastLineNumber() {
		if(!ops.isEmpty())
			return this.ops.get(ops.size()-1).line;
		else
			return this.line;
	}
	
	public Variable<?> newArg(Signature<? extends Type> signature, boolean islocal) 
	{
		Variable<?>	v;

		// non local arguments are tricky
		// we always get them at call time in locals
		// but we want everyone to use the value in frame
		
		// also, we need to store the value on start
		// right after frame construction
		
		if(islocal) {
			 v = new Variable<Function>(this, genLUID(), signature);
			 localVars.add(v.localID, v);
			
			 amap.put(signature, v.localID);
			 vmap.put(signature, v.localID);
		}
		else {
			Variable<?> proxy = 
				new Variable<Function>(this, genLUID(), signature);			
			
			localVars.add(proxy.localID, proxy);	
			amap.put(signature, proxy.localID);
						 
			v = frame.newInstanceVar(signature, false).deref(callFrameVar);
			proxy_map.put(proxy, v);
		}
		
		catypes.add(signature.type);		
		return v;
	}

	public Function newFunc(int line, Signature<FunctionType> t, boolean isstatic) {
		return this.slink.newFunc(line, t, this.isStatic && isstatic);
	}

	public int newLVMap() {
		lvmap = new int[localVars.size()];
		int k = 0;
				
		/* first parse arguments */
		for(Variable<?> v : localVars) {
			if(!amap.containsKey(v.signature)) continue;						
			lvmap[v.localID] = k;
			
			k += JType.sizeof(v.getSignature().type);
		}	
		
		/* then the local variables */
		for(Variable<?> v : localVars) {
			if(amap.containsKey(v.signature)) continue;
			
			// if(v.read.equals(TriStateLogic.FALSE)) continue;
			
			lvmap[v.localID] = k;			
			k += JType.sizeof(v.getSignature().type);
		}
		
		return k;
	}

	public Variable<?> newSelf() {
		if(!amap.isEmpty())
			throw new CompilerException(
				"[Function] Self must be the first argument.");
		
		if(isStatic)
			throw new CompilerException(
				"[Function] Only methods have self.");
			
		
		this.selfVar = newArg(
				new Signature<KlassType>("_self",	this.slink.type), true);
		return this.selfVar;
	}

	public Variable<?> newVar(Signature<? extends Type> signature, boolean islocal) 
	{		
		Variable<?> v;
		
		if(islocal) {
			v = new Variable<Function>(this, genLUID(), signature);
			localVars.add(v.localID, v);
			vmap.put(signature, v.localID);
		}
		else {
			v = frame.newInstanceVar(signature, false).deref(callFrameVar);			
		}
		
		return v;
	}

	public void remove(Callable f) {
		this.slink.remove(f);		
	}

	public String toString() {
		return this.signature.toString();
	}	
	
	public Variable<?> unproxy(Variable<?> proxy) 
	{
		if(!amap.containsKey(proxy.signature))
			return null;
		
		return proxy_map.get(proxy);		
	}

	public <T> void visit(ILVisitor<T> v) {
		v.begin(this);
		
		for(Operation op : ops)
			op.visit(v);
		
		v.end(this);
	}
	
}
