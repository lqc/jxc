package org.lqc.jxc.tokens;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.lqc.jxc.transform.TreeVisitor;
import org.lqc.jxc.types.Type;
import org.lqc.util.PathID;

public class FunctionCall extends ExprToken<Type> {
	
	private PathID fid;
	private ArrayList<ExprToken<? extends Type>> args;		
	private CallableRef ref;	
	
	public FunctionCall(int l, int c, String fid, List<ExprToken<? extends Type>> args) {
		this(l, c, new PathID(fid), args);
	}	
	
	public FunctionCall(int l, int c, String fid, ExprToken<? extends Type>... args) {
		this(l, c, new PathID(fid), args);
	}
	
	public FunctionCall(int l, int c, PathID fid, List<ExprToken<? extends Type>> args) {
		super(l, c, Type.ANY);
		
		this.fid = fid;		
		this.args = new ArrayList<ExprToken<? extends Type>>();
		this.args.addAll(args);
		
		this.ref = null;
	}
	
	public FunctionCall(int l, int c, PathID fid, ExprToken<? extends Type>... args) {
		this(l, c, fid, new Vector<ExprToken<? extends Type>>(args.length));
				
		for(ExprToken<? extends Type> e : args)	{ 
			this.args.add(e);
		}		
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
	public ArrayList<ExprToken<? extends Type>> getArgs() {
		return args;
	}
	
	public void bindRef(CallableRef d) {
		this.ref = d;
		this.valueType = d.getType().getReturnType();
	}
	
	public CallableRef getRef() {
		return this.ref;
	}	
	
}
