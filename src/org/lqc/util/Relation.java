/**
 * 
 */
package org.lqc.util;

public enum Relation {
	GREATER,
	EQUAL,
	LESSER,
	NONCOMPARABLE;
	
	
	public boolean greaterOrEqual() {
		return this.equals(GREATER) || this.equals(EQUAL);
	}
	
	public boolean lesserOrEqual() {
		return this.equals(LESSER) || this.equals(EQUAL);
	}
	
	public boolean equal() {
		return this.equals(Relation.EQUAL);
	}
	
	
}