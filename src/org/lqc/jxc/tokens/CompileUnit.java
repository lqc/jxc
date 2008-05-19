package org.lqc.jxc.tokens;

import java.util.List;
import java.util.Vector;

import org.lqc.jxc.Lexem;

public class CompileUnit extends SyntaxTreeNode {
	
	private String name;
	
	public CompileUnit(int l, int c, String name, List<FunctionDecl> fl) {
		super(l, c);
		functions = new Vector<FunctionDecl>(fl);
		this.name = name;
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
	
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
}
