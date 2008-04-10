package org.lqc.jxc;

public class Lexem extends java_cup.runtime.Symbol {

	private String s;
	
	public Lexem(String n, int id, int l, int r) {
		super(id, l, r);
		s = n;
	}
	
	public Lexem(String n, int id, int l, int r, Object o) {
		super(id, l, r, o);
		s = n;
	}
	
	public String toString() {
		return s;
	}

}
