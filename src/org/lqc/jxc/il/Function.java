package org.lqc.jxc.il;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.lqc.jxc.javavm.JType;
import org.lqc.jxc.transform.ILVisitor;
import org.lqc.jxc.types.FunctionType;
import org.lqc.jxc.types.KlassType;
import org.lqc.jxc.types.Type;
import org.lqc.util.TriStateLogic;

/** Definition of a standalone function. */
public class Function implements StaticContainer<Function>, Callable,
	Iterable<Operation>
{	
	/** Function declared signature. */
	protected Signature<FunctionType> signature;
	
	/** Function call signature. */
	protected List<Type> catypes;
		
	/** List of local variables. */
	protected ArrayList<Variable<?>> localVars;
	
	/** localID to address map. */
	protected int[] lvmap;
	
	/** Variable mapping. */
	protected Map<Signature<? extends Type>, Integer> vmap;
	
	/** Argument mapping. Need for calls. */
	protected Map<Signature<? extends Type>, Integer> amap;		
				
	/** List of instructions. */
	protected List<Operation> ops;
	
	/** Static link. */
	protected Klass slink;
		
	/** Is this an abstract function definition. */
	protected boolean isAbstract;
	
	/** Is this an abstract function definition. */
	protected boolean isStatic;
	
	/** Is this function declared inside some other funciton 
	 * and thus needs local variable translation. */
	protected boolean isLambda;
	
	/** Source line of declaration */
	protected int line;
	
	/** Identifier of last added variable */
	private int _lastID = -1;
	
	/** Frame for exported/non-local variables. */
	protected Frame frame;
	protected Variable<?> callFrameVar;
	
	private int genLUID() {
		return ++_lastID;
	}
					
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
		
		localVars = new ArrayList<Variable<?>>();		
		ops = new Vector<Operation>();
		
		frame = new Frame(this);
			
		this.callFrameVar = this.newVar(
			new Signature<KlassType>("_" + sig.name + "_frame", frame.getType()), true);
				
		this.isAbstract = abs;
		this.isStatic = stat;
		this.isLambda = lambda;		

	}
	
	public Variable<Function> newArg(Signature<? extends Type> signature) 
	{
		Variable<Function> v = new Variable<Function>(this, genLUID(), signature);
		
		localVars.add(v.localID, v);
		
		amap.put(signature, v.localID);
		vmap.put(signature, v.localID);
		
		catypes.add(signature.type);
		
		return v;
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
			v = frame.newInstanceVar(callFrameVar, signature, false);			
		}
		
		return v;
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
		else
			v = frame.get(sig);
		
		if(v == null) 
			return slink.get(sig);
		
		return v;
	}	
	
	public void addOp(Operation op) {
		/* TODO: do something more here ? */
		ops.add(op);
	}

	public Function newFunc(int line, Signature<FunctionType> t, boolean isstatic) {
		return this.slink.newFunc(line, t, this.isStatic && isstatic);
	}
	
	public void remove(Callable f) {
		this.slink.remove(f);		
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
	
	public Signature<FunctionType> declSignature() {
		return signature;
	}

	public Iterator<Operation> iterator() {
		return ops.iterator();
	}

	public <T> void visit(ILVisitor<T> v) {
		v.begin(this);
		
		for(Operation op : ops)
			op.visit(v);
		
		v.end(this);
	}
	
	private int _labelID = 0;
	public Label getUniqueLabel() {
		return new Label("Label" + _labelID++);		
	}

	public String absolutePath() {		
		return slink.absolutePath() + "/" + name();
	}

	public String name() {
		return this.signature.name;
	}

	public Klass container() {
		return slink;
	}
	
	public int[] getLVMap() {
		return lvmap;
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
	
	public int lastLineNumber() {
		if(!ops.isEmpty())
			return this.ops.get(ops.size()-1).line;
		else
			return this.line;
	}
	
	public String toString() {
		return this.signature.toString();
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
	 * @return the isStatic
	 */
	public boolean isStatic() {
		return isStatic;
	}

	public Signature<FunctionType> callSignature() {
		return new Signature<FunctionType>(signature.name, 
				new FunctionType(signature.type.getReturnType(), catypes) );		
	}

	public Klass getNearestKlass() {
		return slink.getNearestKlass();
	}

	public static class Frame extends Klass {		
		public Frame(Function f) {
			super(Klass.forJavaClass(Object.class), produceName(f.absolutePath()));			
		}			
		
		private static String produceName(String n) {
			int i = n.lastIndexOf('/');
			String base = n.substring(0, i);
			String fname = n.substring(i+1);
			
			return base + "$" + "Frame_" + fname;			
		}
		
		public boolean isEmpty() {
			return this.vmap.isEmpty();
		}
	}

	public Frame getFrame() {
		return this.frame;		
	}	
	
	public Variable<?> getCallFrameVar() {
		return callFrameVar;
	}

	/**
	 * @return the isLambda
	 */
	public boolean isLambda() {
		return isLambda;
	}
	
}
