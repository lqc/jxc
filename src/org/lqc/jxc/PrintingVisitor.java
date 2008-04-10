package org.lqc.jxc;

import java.io.PrintStream;

import org.lqc.jxc.tokens.AssignmentInstr;
import org.lqc.jxc.tokens.ComplexInstr;
import org.lqc.jxc.tokens.CondInstr;
import org.lqc.jxc.tokens.ConstantExpr;
import org.lqc.jxc.tokens.Declaration;
import org.lqc.jxc.tokens.EmptyInstruction;
import org.lqc.jxc.tokens.Expression;
import org.lqc.jxc.tokens.FunctionCall;
import org.lqc.jxc.tokens.FunctionDecl;
import org.lqc.jxc.tokens.Instruction;
import org.lqc.jxc.tokens.LoopInstr;
import org.lqc.jxc.tokens.NullExpression;
import org.lqc.jxc.tokens.Program;
import org.lqc.jxc.tokens.ReturnInstr;
import org.lqc.jxc.tokens.VarDecl;
import org.lqc.jxc.tokens.VarExpr;

public class PrintingVisitor implements org.lqc.jxc.tokens.TreeVisitor {
	
	private PrintStream output;
	
	public PrintingVisitor(PrintStream out) {
		this.output = out;		
	}

	public void visit(Program p) {
		for(FunctionDecl d : p.getFunctions()) {
			d.visitNode(this);
		};	
	}
	
	public void visit(FunctionDecl d) {
		this.output.printf("[FUNC-DECL] ID: '%s' TYPE: '%s'\n", d.getID(), d.getType());
		this.output.printf("[BODY-START]\n");
			d.getBody().visitNode(this);
		this.output.printf("[BODY-END]\n");		
	}
	
	public void visit(Declaration decl) {
		this.output.printf("[DECL] ID: %s TYPE: %s\n", decl.getID(), decl.getType());	
	}
	
	public void visit(VarDecl decl) {
		this.visit((Declaration)decl);		
	}
	
	
	public void visit(VarExpr e) {
		this.output.printf("var %s", e.getId() );		
	}

	public void visit(FunctionCall call) {
		this.output.printf("[CALL %s ", call.getFid());
		for(Expression e : call.getArgs()) {
			this.output.print("(");
			e.visitNode(this);
			this.output.print(") ");
		}
		this.output.println("]");		
	}

	public void visit(ComplexInstr instr) {
		this.output.println("[BLOCK-START]");
		for(Instruction i : instr.getInstructions()) {
			i.visitNode(this);			
		}		
		this.output.println("[BLOCK-END]");		
	}

	public void visit(AssignmentInstr instr) {
		this.output.printf("[INSTR] '%s' <- EXPR: ", instr.getId());
		instr.getValue().visitNode(this);
		this.output.println();
	}

	public void visit(ConstantExpr c) {
		this.output.printf("const %s:%s", c.getType(), c.getValue());		
	}

	public void visit(LoopInstr loop) {
		this.output.println("[INSTR-FOR]: ");
		this.output.print("[INIT]: "); 
		loop.getInitInstr().visitNode(this);
		this.output.print("[COND]: ");
		loop.getCondition().visitNode(this);
		this.output.print("[POST]: ");
		loop.getPostInstr().visitNode(this);
		this.output.print("[BODY]: ");
		loop.getBody().visitNode(this);	
	}

	public void visit(ReturnInstr ret) {
		this.output.print("[RET]: ");
		ret.getValue().visitNode(this);
		this.output.println();
	}

	public void visit(CondInstr cond) {
		this.output.println("[INSTR-COND]: ");
		this.output.print("[COND-EXPR: "); 
		cond.getCondition().visitNode(this);
		this.output.print("[BRANCH-A]: ");
		cond.getBranch(true).visitNode(this);
		this.output.print("[BRANCH-B]: ");
		cond.getBranch(false).visitNode(this);		
	}

	public void visit(NullExpression v) {
		this.output.print("<none>");		
	}
	
	public void visit(EmptyInstruction v) {
		this.output.print("<empty>");		
	}

}
