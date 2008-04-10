package org.lqc.jxc.tokens;

import java.util.List;
import java.util.Vector;

public class Program extends SyntaxTreeNode {
	
	public Program(String name, List<FunctionDecl> fl) {
		functions = new Vector<FunctionDecl>(fl);
	}
	
	private List<FunctionDecl> functions;

	/**
	 * @return the functions
	 */
	public List<FunctionDecl> getFunctions() {
		return functions;
	}

	@Override
	public void visitNode(TreeVisitor v) {
		v.visit(this);		
	}
}
