package org.lqc.jxc.tokens;

import java.util.List;
import java.util.Vector;

import org.lqc.jxc.types.Type;
import org.lqc.util.PartialyComparable;

public class FunctionDecl extends Declaration
	implements PartialyComparable<FunctionDecl>
{
	
	protected ComplexInstr body;
	protected List<String> argIDs;

	public FunctionDecl(Type t, String fid, List<String> aids, ComplexInstr b) {
		super(t, fid);
		argIDs = new Vector<String>(aids);
		body = b;
	}
	
	/**
	 * @return the body
	 */
	public ComplexInstr getBody() {
		return body;
	}

	/**
	 * @return the argIDs
	 */
	public List<String> getArgIDs() {
		return argIDs;
	}

	@Override
	public void visitNode(TreeVisitor v) {
		v.visit(this);		
	}

	public boolean isComparable(FunctionDecl x) {
		return this.entityType.isComparable(x.getType());		
	}

	public boolean isGreaterEqual(FunctionDecl x) {
		return this.entityType.isGreaterEqual(x.getType());
	}

}
