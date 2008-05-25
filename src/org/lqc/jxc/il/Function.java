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
import org.lqc.jxc.types.FunctionType;
import org.lqc.jxc.types.Type;

/** Definition of a standalone function. */
public class Function implements StaticContainer, Callable,
	Iterable<Operation>
{
	
	/** Function name. */
	protected Signature<FunctionType> signature;
		
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
		
		/*		
		for(Signature<Type> s : args) {
			Variable v = new Variable(this, genLUID(), s);
			
			localVars.add(v.localID, v);
			amap.put(s, v.localID);
		}
		*/ 
		
		ops = new Vector<Operation>();
	}
	
	public Variable newArg(Signature<Type> signature) {
		Variable v = new Variable(this, genLUID(), signature);
		
		localVars.add(v.localID, v);
		amap.put(signature, v.localID);
		vmap.put(signature, v.localID);
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

	public Callable newFunc(Signature<FunctionType> t,
			Signature<Type>... args) {
		throw new UnsupportedOperationException();
	}

	public Collection<Callable> allFunctions() {
		return Collections.EMPTY_LIST;
	}

	public Collection<Variable> allVariables() {
		return localVars;		
	}
	
	public Signature<FunctionType> callSignature() {
		return signature;
	}

	public Iterator<Operation> iterator() {
		return ops.iterator();
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

	public StaticContainer container() {
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
}
