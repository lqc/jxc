package org.lqc.jxc.javavm;

import java.util.List;

/**
 * 
 * Representation of JVM class.
 *
 */
public class JClass {	
	
	private ModifierSet modifiers;
	private List<JMethod> methods;
	
	private String pckg;
	private String name; 
	
	public JClass(String pckg, String name) {
		this.pckg = pckg;
		this.name = name;		
	}
	
	public void addMethod(JMethod m) {
		methods.add(m);
	}
	
	public void addModifier(ModifierSet m) {
		modifiers.sum(m);		
	}
	
	public void removeModifier(ModifierSet m) {
		modifiers.union(m.mask());
	}
	
	public boolean isPublic() {
		return modifiers.union(ModifierSet.PUBLIC).isEmpty();
	}
	
	public boolean isPrivate() {
		return modifiers.union(ModifierSet.PRIVATE).isEmpty();
	}
	
	public boolean isFinal() {
		return modifiers.union(ModifierSet.FINAL).isEmpty();
	}	

}
