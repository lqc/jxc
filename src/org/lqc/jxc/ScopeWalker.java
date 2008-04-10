package org.lqc.jxc;

import java.util.Stack;

import org.lqc.jxc.tokens.AssignmentInstr;
import org.lqc.jxc.tokens.ComplexInstr;
import org.lqc.jxc.tokens.CondInstr;
import org.lqc.jxc.tokens.ConstantExpr;
import org.lqc.jxc.tokens.Declaration;
import org.lqc.jxc.tokens.EmptyInstruction;
import org.lqc.jxc.tokens.FunctionCall;
import org.lqc.jxc.tokens.FunctionDecl;
import org.lqc.jxc.tokens.LoopInstr;
import org.lqc.jxc.tokens.NullExpression;
import org.lqc.jxc.tokens.Program;
import org.lqc.jxc.tokens.ReturnInstr;
import org.lqc.jxc.tokens.TreeVisitor;
import org.lqc.jxc.tokens.VarDecl;
import org.lqc.jxc.tokens.VarExpr;

public class ScopeWalker implements TreeVisitor {
	
	private Stack<Environment> envStack;
	
	public ScopeWalker() {
		envStack = new Stack<Environment>();
		envStack.push(Environment.getBuiltins());
	}

	public void visit(Program prog) {
		Environment top = new Environment(envStack.peek());
		
		for(FunctionDecl f : prog.getFunctions()) {
			top.putMapping(f);			
		}
		
		envStack.push(top);
		
		for(FunctionDecl f : prog.getFunctions()) {
			f.visitNode(this);			
		}
	}

	public void visit(Declaration decl) {
		// This isn't good XD //
	}

	public void visit(FunctionDecl decl) {
		
	}

	public void visit(VarDecl decl) {
		/* declarations are not recursive */
		decl.getInitialValue().visitNode(this);
		if( envStack.peek().hasImmediateMapping(decl) )
			// TODO
		

	}

	public void visit(FunctionCall call) {
		// TODO Auto-generated method stub

	}

	public void visit(ConstantExpr c) {
		// TODO Auto-generated method stub

	}

	public void visit(VarExpr var) {
		// TODO Auto-generated method stub

	}

	public void visit(ComplexInstr instr) {
		// TODO Auto-generated method stub

	}

	public void visit(AssignmentInstr instr) {
		// TODO Auto-generated method stub

	}

	public void visit(LoopInstr loop) {
		// TODO Auto-generated method stub

	}

	public void visit(ReturnInstr ret) {
		// TODO Auto-generated method stub

	}

	public void visit(CondInstr cond) {
		// TODO Auto-generated method stub

	}

	public void visit(NullExpression v) {
		// TODO Auto-generated method stub

	}

	public void visit(EmptyInstruction v) {
		// TODO Auto-generated method stub

	}

}
