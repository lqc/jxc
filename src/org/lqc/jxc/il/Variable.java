package org.lqc.jxc.il;

import org.lqc.jxc.types.Type;
import org.lqc.util.TriStateLogic;

public class Variable {
	
	private static long __nextID = 1L;	
	private static long getNextID() {
		return __nextID++;
	}
		
	protected int localID;	
	protected long globalID; 
		
	/** Context of this variable. */
	protected StaticContainer slink;
	
	/** Variables signature. */
	protected Signature<Type> signature;	
	
	/** Is this variable written to. */
	protected TriStateLogic write;
	
	/** Is this variable read from. */
	protected TriStateLogic read;
	
	protected Variable() {
		this.globalID = getNextID();
	}
	
	public Variable(StaticContainer container, 
			long localId,
			Signature<Type> sig)
	{
		this();
		
		slink = container;
		signature = sig;
	}
			
}