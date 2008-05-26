package org.lqc.jxc.transform;


import static org.lqc.util.TriStateLogic.FALSE;
import static org.lqc.util.TriStateLogic.TRUE;
import static org.lqc.util.TriStateLogic.UNKNOWN;

import java.util.Comparator;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

import org.lqc.jxc.CompilerException;
import org.lqc.jxc.CompilerWarning;
import org.lqc.jxc.SyntaxErrorException;
import org.lqc.jxc.tokens.ArgumentDecl;
import org.lqc.jxc.tokens.AssignmentInstr;
import org.lqc.jxc.tokens.IncrementInstr;
import org.lqc.jxc.tokens.InstrBlock;
import org.lqc.jxc.tokens.CondInstr;
import org.lqc.jxc.tokens.ConstantExpr;
import org.lqc.jxc.tokens.Declaration;
import org.lqc.jxc.tokens.EmptyInstruction;
import org.lqc.jxc.tokens.Expression;
import org.lqc.jxc.tokens.FunctionCall;
import org.lqc.jxc.tokens.FunctionDecl;
import org.lqc.jxc.tokens.InstrList;
import org.lqc.jxc.tokens.Instruction;
import org.lqc.jxc.tokens.LoopInstr;
import org.lqc.jxc.tokens.NullExpression;
import org.lqc.jxc.tokens.CompileUnit;
import org.lqc.jxc.tokens.ReturnInstr;
import org.lqc.jxc.tokens.TreeVisitor;
import org.lqc.jxc.tokens.TypeCast;
import org.lqc.jxc.tokens.VarDecl;
import org.lqc.jxc.tokens.VarExpr;
import org.lqc.jxc.types.Type;
import org.lqc.util.ElementNotFoundException;

public class ControlFlowAnalyzer implements TreeVisitor {
	
	private HashMap<Declaration, VarInfo> map;
	private SortedSet<CompilerWarning> warnings; 
	
	public ControlFlowAnalyzer() {
		this.map = new HashMap<Declaration, VarInfo>();
		this.warnings = new TreeSet<CompilerWarning>(
				new Comparator<CompilerWarning>() {
					public int compare(CompilerWarning a,
							CompilerWarning b) {
						return a.line - b.line;						
					}					
				}
			);
	}

	public void visit(CompileUnit prog) 
	{
		Context ctx = prog.getStaticContext();
				
		for(Declaration d : ctx.getAllDeclarations())		
			map.put(d, new VarInfo());
			
		for(FunctionDecl f : prog.getFunctions())
		{
			f.setReachable(TRUE);
			f.visitNode(this);	
		}
		
		for(Declaration d : map.keySet()) {
			VarInfo info = map.get(d);			
			if(info.read.equals(FALSE)) { 
				warnings.add(new CompilerWarning(d, 
						"Unused declaration: " + d ) );
			}		
		}
	}

	public void visit(ArgumentDecl decl) {
		//
	}

	public void visit(FunctionDecl decl) {		
		map.get(decl).write = TRUE;
		
		Context inner_ctx = decl.innerContext();
		
		VarDecl retVar;
		VarInfo info;
		
		try {
			retVar = inner_ctx.getVariable(Context.RETURN_ID);			
			map.put(retVar,new VarInfo());
		} catch (ElementNotFoundException e) {
			throw new CompilerException("[INTERNAL] No return in fdecl?");
		}
		
		/* Add local definitions to map */
		for(Declaration d : inner_ctx.getAllDeclarations())
		{
			 map.put(d, info = new VarInfo());
		
			 if(d instanceof ArgumentDecl) 
				 info.write = TRUE;
		}		
		
		/* Traverse instruction */
		for(Instruction i : decl.getBody()) 
		{
			/* There was a return. */  
			if(map.get(retVar).write.equals(TRUE)) {
				i.setReachable(FALSE);
				i.visitNode(this);
				continue;				
			}
			
			i.setReachable(TRUE);
			i.visitNode(this);			
		}
		
		if(!retVar.getType().equals(Type.VOID) 
			&& map.get(retVar).write.equals(FALSE)) {
			/* TODO: add mandatory return from voids */
			throw new SyntaxErrorException(decl, 
					String.format("Function '%s' does not return", decl.getLocalID()) );			
		}
		
		map.remove(retVar);
	}

	public void visit(VarDecl decl) {
		if(!decl.getInitialValue().equals(Expression.NULL)) {
			final VarInfo info = map.get(decl);
			info.write = info.write.or(decl.isReachable());
			
			decl.getInitialValue().setReachable(decl.isReachable());
			decl.getInitialValue().visitNode(this);
		}		
	}

	public void visit(FunctionCall call) {
		for(Expression e : call.getArgs()) {
			e.setReachable(call.isReachable());
			e.visitNode(this);
		}
		
		final VarInfo info = map.get(call.getRef());
		if(info != null)
			info.read = info.read.or(call.isReachable());		
	}

	public void visit(ConstantExpr c) {
		// nothing
	}

	public void visit(VarExpr var) {
		final VarInfo info = map.get(var.getRef());
		info.read = info.read.or(var.isReachable());

	}

	public void visit(InstrBlock instr) {
		Context ctx = instr.getStaticContext();				
		VarInfo rinfo;
				
		try {
			rinfo = map.get(ctx.getVariable(Context.RETURN_ID));			
		} catch (ElementNotFoundException e) {
			throw new CompilerException("[INTERNAL] No return in " +
					"block ?");
		}		
		
		/* Traverse instruction */
		for(Instruction i : instr) 
		{
			if(i instanceof Declaration) 
				map.put((Declaration)i, new VarInfo());				
			
			/* There was a return. */  
			if(rinfo.write.equals(TRUE)) {
				i.setReachable(FALSE);
				i.visitNode(this);
				continue;				
			}
			
			i.setReachable(instr.isReachable());
			i.visitNode(this);			
		}
	}

	public void visit(AssignmentInstr instr) {
		final VarInfo info = map.get(instr.getRef());
		info.write = info.write.or(instr.isReachable());		
		
		instr.getValue().setReachable(instr.isReachable());
		instr.getValue().visitNode(this);		
	}

	public void visit(LoopInstr loop) {
		Expression cond = loop.getCondition();
		cond.setReachable(loop.isReachable());
		cond.visitNode(this);
		
		Instruction i;
		
		i = loop.getInitInstr();
		i.setReachable(loop.isReachable());
		i.visitNode(this);
		
		i = loop.getBody();
		i.setReachable(UNKNOWN);
		i.visitNode(this);
		
		i = loop.getPostInstr();
		i.setReachable(UNKNOWN);
		i.visitNode(this);
	}

	public void visit(ReturnInstr ret) {
		Context ctx = ret.getStaticContext();
		try {
			final VarInfo rinfo = map.get(ctx.getVariable(Context.RETURN_ID));
			rinfo.write = rinfo.write.or(ret.isReachable());
			
			ret.getValue().setReachable(ret.isReachable());
			ret.getValue().visitNode(this);
			
		} catch (ElementNotFoundException e) {
			throw new CompilerException("[INTERNAL] Return not in function - this should already been checked.");
		}
	}

	public void visit(CondInstr cond) {
		Expression c = cond.getCondition();
		c.setReachable(cond.isReachable());
		c.visitNode(this);
		
		Instruction i;
		
		i = cond.getBranch(true);
		i.setReachable(UNKNOWN);
		i.visitNode(this);
		
		i = cond.getBranch(false);
		i.setReachable(UNKNOWN);
		i.visitNode(this);
	}

	public void visit(NullExpression v) {
		// nothing
	}

	public void visit(EmptyInstruction v) {
		// nothing
	}

	/**
	 * @return the warnings
	 */
	public SortedSet<CompilerWarning> getWarnings() {
		return warnings;
	}

	public void visit(IncrementInstr incrementInstr) {
		// TODO Auto-generated method stub
		
	}

	public void visit(TypeCast cast) {
		// TODO Auto-generated method stub
		
	}

	public void visit(InstrList instrList) {
		// TODO Auto-generated method stub
		
	}

}
