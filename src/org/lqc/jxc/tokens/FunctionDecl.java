package org.lqc.jxc.tokens;

import java.util.List;
import java.util.Vector;

import org.lqc.jxc.types.FunctionType;
import org.lqc.jxc.types.Type;

public class FunctionDecl extends Declaration	
{	
	protected ComplexInstr body;
	protected List<ArgumentDecl> args;

	public FunctionDecl(String fid, Type rt, List<ArgumentDecl> args, ComplexInstr b) {
		super(new FunctionType(rt, listToArray(args)), fid);
		this.args = new Vector<ArgumentDecl>();
		this.args.addAll(args);		
		body = b;
	}
	
	public FunctionDecl(String fid, Type rt, Type... argTypes) {
		super(new FunctionType(rt, argTypes), fid);
		this.args = new Vector<ArgumentDecl>();
		this.body = new ComplexInstr();
	}
	
	private static Type[] listToArray(List<ArgumentDecl> args) {
		Type[] types = new Type[args.size()];
		int i = 0;
		for(ArgumentDecl d : args) {
		       types[i++] = d.getType();			       
		}
		return types;		
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
