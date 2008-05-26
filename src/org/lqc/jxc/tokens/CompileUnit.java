package org.lqc.jxc.tokens;

import java.util.List;
import java.util.Vector;

public class CompileUnit extends SyntaxTreeNode {

	private List<FunctionDecl> functions;
	private List<ImportStmt> imports;
	private String name;

	public CompileUnit(int l, int c, String name, List<ImportStmt> importList,
			List<FunctionDecl> funcList) {
		super(l, c);

		functions = new Vector<FunctionDecl>(funcList);
		imports = new Vector<ImportStmt>(importList);
		this.name = name;
	}

	/**
	 * @return the functions
	 */
	public List<FunctionDecl> getFunctions() {
		return functions;
	}
	
	public List<ImportStmt> getImports() {
		return imports;
	}

	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void visitNode(TreeVisitor v) {
		v.visit(this);
	}
}
