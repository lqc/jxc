package org.lqc.util;

public interface PartialyComparable<T> {
	
	/** Retruns true if this two objects are comparable. */
	public boolean isComparable(T x); 
	
	/** Compares two objects (a, b) for which isComparable(a,b) == true.
	 * Returns (a >= b). 
	 */
	public boolean isGreaterEqual(T x);
}
