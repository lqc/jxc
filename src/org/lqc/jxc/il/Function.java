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
import org.lqc.jxc.types.Type;

/** Definition of a standalone function. */
public class Function implements StaticContainer, Callable,
	Iterable<Operation>
{
	
	/** Function declared signature. */
	protected Signature<FunctionType> signature;
	
	/** Function call signature. */
	protected List<Type> catypes;
		
	/** List of local variables. */
	protected ArrayList<Variable> localVars;
	
	/** localID to address map. */
	protected int[] lvmap;
	
	/** Variable mapping. */
	protected Map<Signature<Type>, Integer> vmap;
	
	/** Argument mapping. Need for calls. */
	protected Map<Signature<Type>, Integer> amap;		
				
	/** List of instructions. */
	protected List<Operation> ops;
	
	/** Static link. */
	protected Klass slink;
		
	/** Is this an abstract function definition. */
	protected boolean isAbstract;
	
	/** Is this an abstract function definition. */
	protected boolean isStatic;
	
	/** Source line of declaration */
	protected int line;
	
	/** Identifier of last added variable */
	private int _lastID = -1;
	
	private int genLUID() {
		return ++_lastID;
	}
					
	public Function(Klass container,
			int line, Signature<FunctionType> sig,
			boolean abs, boolean stat)
	{		
		slink = container;
		signature = sig;
		catypes = new Vector<Type>();
		
		this.line = line;
				
		vmap = new HashMap<Signature<Type>, Integer>();
		amap = new HashMap<Signature<Type>, Integer>();
		
		localVars = new ArrayList<Variable>();		
		ops = new Vector<Operation>();
		
		this.isAbstract = abs;
		this.isStatic = stat;
	}
	
	public Variable newArg(Signature<Type> signature) {
		Variable v = new Variable(this, genLUID(), signature);
		
		localVars.add(v.localID, v);
		
		amap.put(signature, v.localID);
		vmap.put(signature, v.localID);
		
		catypes.add(signature.type);
		
		return v;
	}
	
	public Variable newVar(Signature<Type> signature) {
		Variable v = new Variable(this, genLUID(), signature);
		
		localVars.add(v.localID, v);
		vmap.put(signature, v.localID);
		return v;
	}
	
	public Callable get(Signature<FunctionType> sig) {
		return slink.get(sig);		
	}

	public Variable get(Signature<Type> sig) {
		Variable v = localVars.get(vmap.get(sig));
		if(v == null) 
			return slink.get(sig);
		
		return v;
	}	
	
	public void addOp(Operation op) {
		/* TODO: do something more here ? */
		ops.add(op);
	}

	public Function newFunc(int line, Signature<FunctionType> t) {
		return this.slink.newFunc(line, t);
	}
	
	public void remove(Callable f) {
		this.slink.remove(f);		
	}

	public Collection<Callable> allCallables() {
		return Collections.EMPTY_LIST;
	}

	public Collection<Variable> allVariables() {
		return localVars;		
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
		int i = 0;
		
		for(Variable v : localVars) {
			lvmap[i] = k;
			i++;
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

	
}
