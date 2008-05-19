package org.lqc.jxc.transform;

import static org.lqc.util.TriStateLogic.FALSE;

import org.lqc.util.TriStateLogic;

/** Liveness Analysis information. */
public class VarInfo {
	/** Has this entity been read. */
	public TriStateLogic read;
	
	/** Has this entity been written to. */
	public TriStateLogic write;
			
	public VarInfo() {
		read = FALSE;
		write = FALSE;		
	}
	
	public String toString() {
		return String.format("{R=%s, W=%s}", read, write);
	}
}