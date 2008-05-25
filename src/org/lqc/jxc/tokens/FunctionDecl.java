package org.lqc.jxc.tokens;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.lqc.jxc.transform.Context;
import org.lqc.jxc.types.FunctionType;
import org.lqc.jxc.types.Type;

public class FunctionDecl extends Declaration	
{	
	protected Instruction body;
	protected ArrayList<ArgumentDecl> args;
	
	/** Table of local variables. This is filled in
	 * during scope analysis. 
	 */
	protected Context innerContext;
	
	public FunctionDecl(int l, int c, String fid, FunctionType t) {
		super(l, c, t, fid);
		this.args = new ArrayList<ArgumentDecl>();
		
		int i = 0;
		for(Type at : t.getArgumentTypes())
			args.add( new ArgumentDecl(l, c, at, "arg"+i++) );
					
		body = Instruction.EMPTY;		
	}
	
	public FunctionDecl(int l, int c, String fid, Type rt, List<ArgumentDecl> args, Instruction b) {
		super(l, c, new FunctionType(rt, listToArray(args)), fid);
		this.args = new ArrayList<ArgumentDecl>();
		this.args.addAll(args);		
		body = b;		
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
	public Instruction getBody() {
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
	
	public void initInnerContext(Context parent) {
		this.innerContext = new Context(parent, this.entityID);
				
	}
	
	public Context innerContext() {
		return innerContext;
	}	
	
}
