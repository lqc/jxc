package org.lqc.jxc.transform;

import java.util.HashMap;
import java.util.Set;
import java.util.Stack;

import org.lqc.jxc.tokens.ArgumentDecl;
import org.lqc.jxc.tokens.AssignmentInstr;
import org.lqc.jxc.tokens.ComplexInstr;
import org.lqc.jxc.tokens.CondInstr;
import org.lqc.jxc.tokens.ConstantExpr;
import org.lqc.jxc.tokens.Declaration;
import org.lqc.jxc.tokens.EmptyInstruction;
import org.lqc.jxc.tokens.FunctionCall;
import org.lqc.jxc.tokens.FunctionDecl;
import org.lqc.jxc.tokens.Instruction;
import org.lqc.jxc.tokens.LoopInstr;
import org.lqc.jxc.tokens.NullExpression;
import org.lqc.jxc.tokens.Program;
import org.lqc.jxc.tokens.ReturnInstr;
import org.lqc.jxc.tokens.TreeVisitor;
import org.lqc.jxc.tokens.VarDecl;
import org.lqc.jxc.tokens.VarExpr;

public class LivenessAnalyzer implements TreeVisitor {
	
	/** Liveness Analysis information. */
	public class Info {
		/** Has this entity been read. */
		public boolean read;
		
		/** Has this entity been written to. */
		public boolean write;
		
		/** Has this entity been used in non local scope. */
		public boolean nonlocal;
		
		public Info() {
			read = false;
			write = false;
			nonlocal = false;
		}
	}
	
	private HashMap<Declaration, Info> map;
	private Stack<Boolean> returnStates;
	
	public LivenessAnalyzer() {
		this.map = new HashMap<Declaration, Info>();
		this.returnStates = new Stack<Boolean>();
	}

	public void visit(Program prog) 
	{
		Context ctx = prog.getStaticContext();		
		Set<Declaration> set = ctx.getAllDeclarations();
		
		/* Add all context elements */
		for(Declaration d : set)		
			map.put(d, new Info());
		
		for(Declaration d : set) 
			d.visitNode(this);
	}

	public void visit(ArgumentDecl decl) {
		// TODO Auto-generated method stub

	}

	public void visit(FunctionDecl decl) {
		map.get(decl).write = true;
		
		/* Add local definitions to map */
		for(Declaration d : decl.getStaticContext().getAllDeclarations())		
			map.put(d, new Info());		
		
		/* Arguments are by default initialized */
		for(ArgumentDecl d : decl.getArgs())
			map.get(d).write = true;
		
		

		/* Traverse instruction */
		for(Instruction i : decl.getBody().getInstructions()) 
		{
			
			
			
		}
	}

	public void visit(VarDecl decl) {
		// TODO Auto-generated method stub

	}

	public void visit(FunctionCall call) {
		map.get(call.getRef()).read = true;
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
