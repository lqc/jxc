package org.lqc.jxc.tokens;

import java.util.List;
import java.util.Vector;

import org.lqc.jxc.types.Type;

public class FunctionCall extends Expression {
	
	private String fid;
	private List<Expression> args;
	private boolean isBuiltin;
	
	private FunctionDecl ref;	
	
	public FunctionCall(String fid, boolean builtin, List<Expression> args) {
		super(Type.ANY);
		
		this.fid = fid;
		this.isBuiltin = builtin;
		this.args = args;	
		
		this.ref = null;
	}
	
	public FunctionCall(String fid, boolean builtin, Expression... args) {
		this(fid, builtin, new Vector<Expression>(args.length));
		for(Expression e : args) { this.args.add(e); }		
	}
	
	@Override
	public void visitNode(TreeVisitor v) {
		v.visit(this);		
	}	
	
	/**
	 * @return the fid
	 */
	public String getFid() {
		return fid;
	}

	/**
	 * @return the args
	 */
	public List<Expression> getArgs() {
		return args;
	}

	/**
	 * @return the isBuiltin
	 */
	public boolean isBuiltin() {
		return isBuiltin;
	}
	
	public void bindRef(FunctionDecl d) {
		this.ref = d;
		this.valueType = d.getType().getReturnType();
	}
	
	public FunctionDecl getRef() {
		return this.ref;
	}	
	
}
