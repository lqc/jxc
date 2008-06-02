package org.lqc.jxc.transform;

import org.lqc.jxc.tokens.ArgumentDecl;
import org.lqc.jxc.tokens.AssignmentInstr;
import org.lqc.jxc.tokens.CompileUnit;
import org.lqc.jxc.tokens.CondInstr;
import org.lqc.jxc.tokens.ConstantExpr;
import org.lqc.jxc.tokens.EmptyInstruction;
import org.lqc.jxc.tokens.FunctionCall;
import org.lqc.jxc.tokens.FunctionDecl;
import org.lqc.jxc.tokens.ImportStmt;
import org.lqc.jxc.tokens.IncrementInstr;
import org.lqc.jxc.tokens.InstrBlock;
import org.lqc.jxc.tokens.InstrList;
import org.lqc.jxc.tokens.LambdaExpr;
import org.lqc.jxc.tokens.LoopInstr;
import org.lqc.jxc.tokens.NullExpression;
import org.lqc.jxc.tokens.ReturnInstr;
import org.lqc.jxc.tokens.TypeCast;
import org.lqc.jxc.tokens.VarDecl;
import org.lqc.jxc.tokens.VarExpr;

public interface TreeVisitor{	
	public void visit(CompileUnit prog);
		
	public void visit(ArgumentDecl decl);
	public void visit(FunctionDecl decl);	
	public void visit(VarDecl decl);		
	
	public void visit(FunctionCall call);
	public void visit(ConstantExpr c);
	public void visit(VarExpr var);
		
	public void visit(AssignmentInstr instr);
	public void visit(LoopInstr loop);
	public void visit(ReturnInstr ret);
	public void visit(CondInstr cond);	
	
	public void visit(NullExpression v);
	public void visit(EmptyInstruction v);

	public void visit(IncrementInstr incrementInstr);
	
	public void visit(TypeCast cast);

	public void visit(InstrBlock instr);
	public void visit(InstrList instrList);

	public void visit(ImportStmt importStmt);

	public void visit(LambdaExpr lambdaExpr);
}
