package org.lqc.jxc.tokens;

import java.util.ArrayList;
import java.util.List;

import org.lqc.jxc.transform.Context;
import org.lqc.jxc.types.Type;

public class BuiltinDecl extends FunctionDecl {
	
	private static List<ArgumentDecl> _argsFromTypes(Type... types) {
		List<ArgumentDecl> decls = new ArrayList<ArgumentDecl>(types.length);
		int i=0;
		
		for(Type t : types) 
			decls.add( new ArgumentDecl(-1, -1, t, "_arg"+i) );
		
		return decls;		
	}

	public BuiltinDecl(String fid, Type rt, Type... argTypes) {
		super(-1, -1, fid, rt, _argsFromTypes(argTypes), 
				new EmptyInstruction() );		
	}
	
	@Override
	public void visitNode(TreeVisitor v) {
		v.visit(this);		
	}
	
	/**
	 * Does nothing.
	 */
	@Override
	public void initInnerContext(Context parent) {
		return;
	}
	
	/**
	 * Builtins don't have any inner context.
	 */
	@Override
	public Context innerContext() {
		return null;
	}

}
