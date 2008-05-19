package org.lqc.jxc.tokens;

public interface TreeVisitor{	
	public void visit(CompileUnit prog);
		
	public void visit(ArgumentDecl decl);
	public void visit(FunctionDecl decl);	
	public void visit(VarDecl decl);		
	
	public void visit(FunctionCall call);
	public void visit(ConstantExpr c);
	public void visit(VarExpr var);
	
	public void visit(ComplexInstr instr);
	public void visit(AssignmentInstr instr);
	public void visit(LoopInstr loop);
	public void visit(ReturnInstr ret);
	public void visit(CondInstr cond);	
	
	public void visit(NullExpression v);
	public void visit(EmptyInstruction v);

	public void visit(IncrementInstr incrementInstr);
}
