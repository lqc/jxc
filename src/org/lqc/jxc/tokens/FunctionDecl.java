package org.lqc.jxc.tokens;

import java.util.List;
import java.util.Vector;

import org.lqc.jxc.types.FunctionType;

public class FunctionDecl extends Declaration	
{	
	protected ComplexInstr body;
	protected List<ArgumentDecl> args;

	public FunctionDecl(FunctionType t, String fid, List<ArgumentDecl> args, ComplexInstr b) {
		super(t, fid);
		this.args = new Vector<ArgumentDecl>();
		this.args.addAll(args);		
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
	public List<ArgumentDecl> getArgs() {
		return args;
	}

	@Override
	public void visitNode(TreeVisitor v) {
		v.visit(this);		
	}
	
	@Override
	public FunctionType getType() {
		return (FunctionType)this.entityType;
	}
}
