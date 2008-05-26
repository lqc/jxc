package org.lqc.jxc.il;

import org.lqc.jxc.types.FunctionType;

public class BranchBuiltin extends Builtin {
		
	public BranchBuiltin(Signature<FunctionType> sig,
			String branchTempl) {
		super(sig, "", branchTempl);		
	}
	
	@Override
	public String getContents(StaticContainer dl) {		
		Label fL = dl.getUniqueLabel();
		Label eL = dl.getUniqueLabel();
		
		StringBuffer buf = new StringBuffer();
		
		buf.append(String.format(branchTempl, fL.getName()));
		buf.append('\n');
		buf.append("iconst_1\n");
		buf.append("goto ");
		buf.append(eL.getName());
		buf.append('\n');
		buf.append(fL.emmit());		
		buf.append("iconst_0\n");
		buf.append(eL.emmit());
		buf.append("nop");
		
		return buf.toString();
	}

}
