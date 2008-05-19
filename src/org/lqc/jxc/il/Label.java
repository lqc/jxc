package org.lqc.jxc.il;

public class Label {
	
	private String name;
	private boolean wasUsed;
	
	public Label(String name) {
		this.name = name;
		this.wasUsed = false;
	}
	
	public String getName() {
		wasUsed = true;
		return name;
	}
	
	public String emmit() {
		if(wasUsed) 
			return name + ":\n";
		else
			return "";
	}
}
