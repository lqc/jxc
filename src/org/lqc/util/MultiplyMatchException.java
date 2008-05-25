package org.lqc.util;

import java.util.List;

import org.lqc.util.DAGraph.Node;

@SuppressWarnings("serial")
public class MultiplyMatchException extends Exception {

	private final List<Node> matches;
	
	public MultiplyMatchException(List m) {				
		this.matches = m;		
	}
	
	public List<Node> matches() {
		return this.matches;
	}
	
	@Override
	public String getMessage() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("Candidates:\n");
		for(Node n : matches) {
			buf.append(n.value.toString());
			buf.append("\n");			
		}
		
		return buf.toString();		
	}
	

}
