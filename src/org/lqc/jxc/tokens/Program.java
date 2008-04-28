package org.lqc.jxc.tokens;

import java.util.List;
import java.util.Vector;

import org.lqc.jxc.Lexem;

public class Program extends SyntaxTreeNode {
	
	public Program(int l, int c, String name, List<FunctionDecl> fl) {
		super(l, c);
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
