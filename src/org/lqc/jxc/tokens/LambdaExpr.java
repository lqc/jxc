package org.lqc.jxc.tokens;

import java.util.List;

import org.lqc.jxc.transform.Context;
import org.lqc.jxc.transform.TreeVisitor;
import org.lqc.jxc.types.FunctionType;
import org.lqc.jxc.types.Type;

public class LambdaExpr extends ExprToken<FunctionType> {
	
	protected static Type[] extractTypes(List<ArgumentDecl> args) {
		Type[] types = new Type[args.size()];
		int i = 0;
		for(ArgumentDecl d : args) {
		       types[i++] = d.getType();			       
		}
		return types;		
	}
	
	private InstrBlock body;
	private ExprToken<? extends Type> yield;
	protected List<ArgumentDecl> args;
	
	protected Context innerContext;

	public LambdaExpr(int l, int c, List<Instruction> ops, 
			ExprToken<? extends Type> yield, List<ArgumentDecl> args_decl) {
		super(l, c, new FunctionType(yield.getType(), extractTypes(args_decl)) );
		
		body = new InstrBlock(l, c, ops);	
		
		this.yield = yield;
		this.args = args_decl;
	}

	@Override
	public void visitNode(TreeVisitor v) {
		v.visit(this);
	}
	
	public void initInnerContext(Context parent) {
		this.innerContext = new Context(parent, "<lambda>");				
	}
	
	public Context innerContext() {
		return innerContext;
	}	
		
	public InstrBlock getBody() {
		return body;
	}
	
	public List<ArgumentDecl> getArgs() {
		return args;
	}
	
	public ExprToken<? extends Type> getYield() {
		return yield;
	}

}
