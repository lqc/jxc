package org.lqc.jxc.javavm;

import java.util.List;
import java.util.Vector;

public class JMethod {
	
	private ModifierSet modifiers;
	private List<JInstr> instructions;
			
	private int maxstack;	
	private int maxlocals;
	private String signature;
		
	public JMethod(String signature, int locals) 
	{
		this.maxlocals = locals;
		this.maxstack = 0;	
		this.signature = signature;
		
		instructions = new Vector<JInstr>();
		modifiers = ModifierSet.PUBLIC;
	}	
	
	public void addInstr(JInstr i) {
		this.instructions.add(i);
		this.maxstack = Math.max(this.maxstack, 
				i.maxStackSize() );		
	}
	
	public void addModifier(ModifierSet m) {
		modifiers.sum(m);		
	}
	
	public void removeModifier(ModifierSet m) {
		modifiers.union(m.mask());
	}
	
	public boolean isPublic() {
		return modifiers.union(ModifierSet.PRIVATE).isEmpty();
	}
	
	public boolean isPrivate() {
		return !modifiers.union(ModifierSet.PRIVATE).isEmpty();
	}
	
	public boolean isFinal() {
		return !modifiers.union(ModifierSet.FINAL).isEmpty();
	}	

}
