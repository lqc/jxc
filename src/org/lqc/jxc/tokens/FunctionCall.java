package org.lqc.jxc.tokens;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.lqc.jxc.types.Type;
import org.lqc.util.PathID;

public class FunctionCall extends Expression {
	
	private PathID fid;
	private ArrayList<Expression> args;		
	private FunctionDecl ref;	
	
	public FunctionCall(int l, int c, String fid, List<Expression> args) {
		this(l, c, new PathID(fid), args);
	}	
	
	public FunctionCall(int l, int c, String fid, Expression... args) {
		this(l, c, new PathID(fid), args);
	}
	
	public FunctionCall(int l, int c, PathID fid, List<Expression> args) {
		super(l, c, Type.ANY);
		
		this.fid = fid;		
		this.args = new ArrayList<Expression>(args);	
		
		this.ref = null;
	}
	
	public FunctionCall(int l, int c, PathID fid, Expression... args) {
		this(l, c, fid, new Vector<Expression>(args.length));
		for(Expression e : args) { this.args.add(e); }		
	}
	
	@Override
	public void visitNode(TreeVisitor v) {
		v.visit(this);		
	}	
	
	/**
	 * @return the fid
	 */
	public PathID getFid() {
		return fid;
	}

	/**
	 * @return the args
	 */
	public ArrayList<Expression> getArgs() {
		return args;
	}
	
	public void bindRef(FunctionDecl d) {
		this.ref = d;
		this.valueType = d.getType().getReturnType();
	}
	
	public FunctionDecl getRef() {
		return this.ref;
	}	
	
}
