package org.lqc.jxc.il;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * 
 * Groups operations.
 *
 */
public class CompoundOperation 
	extends Operation implements Iterable<Operation> {
	
	/** Operations. */
	protected List<Operation> operations;
	
	public CompoundOperation(StaticContainer c, int line) {
		super(c, line);
		
		operations = new Vector<Operation>();
	}
	
	public boolean append(Operation op) {
		return operations.add(op);
	}

	public Iterator<Operation> iterator() {
		return operations.iterator();		
	}
	
	public boolean isNop() {
		boolean yes = true;
		
		for(Operation op : this) {
			yes &= op.isNop();
			if(!yes) return false;
		}
		
		return true;
	}

}
