package org.lqc.jxc.javavm;

public class ModifierSet {
	
	public final static ModifierSet PUBLIC 		= new ModifierSet(0x00);
	public final static ModifierSet PRIVATE 	= new ModifierSet(0x01);	
	public final static ModifierSet PROTECTED 	= new ModifierSet(0x02);
	public final static ModifierSet FINAL	 	= new ModifierSet(0x04);
	public final static ModifierSet STATIC 		= new ModifierSet(0x08);
	public final static ModifierSet ABSTRACT	= new ModifierSet(0x10);
	
	// public final static ModifierSet INLINE 		= new ModifierSet();
		
	private int flag;
	
	private ModifierSet(int ord) {
		flag = ord;		
	}	
	
	public ModifierSet sum(ModifierSet s) {
		this.flag |= s.flag;
		return this;
	}
	
	public ModifierSet union(ModifierSet s) {
		this.flag &= s.flag;
		return this;
	}
	
	public ModifierSet mask() {
		ModifierSet x = new ModifierSet(0);
		x.flag = 0xffffffff ^ this.flag;
		return x;		
	}
	
	public boolean isEmpty() {
		return (this.flag == 0);		
	}
}
