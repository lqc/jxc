package org.lqc.jxc.il;

import org.lqc.jxc.types.Type;
import org.lqc.util.TriStateLogic;

public class Variable<C extends StaticContainer<C>> {
	
	private static long __nextID = 1L;	
	private static long getNextID() {
		return __nextID++;
	}
		
	protected int localID;	
	protected long globalID; 
		
	/** Context of this variable. */
	protected C slink;
	
	/** Variables signature. */
	protected Signature<? extends Type> signature;	
	
	/** Is this variable written to. */
	protected TriStateLogic write;
	
	/** Is this variable read from. */
	protected TriStateLogic read;
	
	protected Variable() {
		this.globalID = getNextID();
	}
	
	public int localID() {
		return this.localID;
	}
	
	public Variable(C container, 
			int localID,
			Signature<? extends Type> sig)
	{
		this();
		
		slink = container;
		signature = sig;
		this.localID = localID;
		
		this.read = TriStateLogic.FALSE;
		this.write = TriStateLogic.FALSE;
	}
	
	public Signature<? extends Type> getSignature() {
		return signature;
	}
	
	public String toString() {
		return signature.toString();
	}
	
	public C slink() {
		return slink;
	}	
			
}
