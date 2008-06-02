package org.lqc.jxc.javavm;

import java.io.PrintStream;

import org.lqc.jxc.CompilerException;
import org.lqc.jxc.il.Assignment;
import org.lqc.jxc.il.Block;
import org.lqc.jxc.il.Branch;
import org.lqc.jxc.il.Builtin;
import org.lqc.jxc.il.Call;
import org.lqc.jxc.il.Callable;
import org.lqc.jxc.il.Closure;
import org.lqc.jxc.il.Constant;
import org.lqc.jxc.il.Expression;
import org.lqc.jxc.il.Function;
import org.lqc.jxc.il.Label;
import org.lqc.jxc.il.Loop;
import org.lqc.jxc.il.Klass;
import org.lqc.jxc.il.Nop;
import org.lqc.jxc.il.Operation;
import org.lqc.jxc.il.Return;
import org.lqc.jxc.il.ReturnVoid;
import org.lqc.jxc.il.TypeConversion;
import org.lqc.jxc.il.VariableValue;
import org.lqc.jxc.types.Type;

/**
 * TODO: optimize increment
 * 
 */
public class IL2Jasmin {
	
	private PrintStream out;
	private int lastLnum;
	
	public IL2Jasmin(PrintStream out) {
		/* do some init */	
		this.out = out;
		lastLnum = -1000;
	}	 
	
	public void emmit(Klass m) {
		out.print("; Class generated by jxCompiler\n\n");
				
		if(!m.isInterface())
		{				
			/* preface block */
			out.printf(".class public %s\n", m.getModuleName());
			out.printf(".super lang/jx/Module\n\n");	

			/* main stub */
			out.print("; Main stub\n");
			out.print(".method public static main([Ljava/lang/String;)V\n");
			out.print(".limit locals 1\n");
			out.print(".limit stack 1\n");
			out.printf("invokestatic %s/main()I\n", m.getModuleName());
			out.print("pop\n");
			out.print("return\n");
			out.print(".end method\n\n");

			out.printf("; Module content\n\n");	
		}
		else {
			out.printf(".interface public %s\n", m.getModuleName());
			out.printf(".super java/lang/Object\n\n");	
		}
			
		
		for(Callable f : m.allCallables()) {
			this.emmit(f);
		}				
	}
	
	public void emmit(Callable c) {
		if(c instanceof Function) {
			emmit( (Function)c);			
		}
		else if (c instanceof Builtin) {
			/* don't print */
		}
		else 
			throw new CompilerException("Unknown type of callable - internal error !?");
	}
	
	public void emmit(Function f) {
		
		out.printf(".method %s public %s %s\n",
				(f.isAbstract() ? "abstract" : ""),
				(f.isStatic() ? "static" : ""),
				JType.methodSignature(f.declSignature()) );		
		
		if(!f.isAbstract())
		{
			/* locals can have diffrent size */				
			int k = f.newLVMap();		

			out.printf(".limit locals %d\n", k);
			out.printf(".limit stack %d\n", calculateMaxStack(f) );

			for(Operation op : f) 
				this.emmit(op, 0);
		}
		
		out.printf(".end method\n\n");		
	}
	
	public void emmit(Operation op, int depth) {
		
		if(lastLnum != op.line) {
			out.printf(".line %d\n", op.line);
			lastLnum = op.line;
		}
		
		/* Expressions */	
		if(op instanceof Constant) {
			Constant<?> c = (Constant<?>)op;
			
			JType jt = JType.fromILType(c.getType());
			out.printf("%s\n", jt.loadConstant(c.value()) );
			return;
		}
		
		if(op instanceof VariableValue) {
			VariableValue v = (VariableValue)op;
			JType jt = JType.fromILType(v.getType());
			
			if(v.slink() instanceof Function) {
				Function f = (Function)v.slink();
				int i = f.getLVMap()[v.reference().localID()];
				out.printf("%s\n", jt.loadVar(i));
			}
			else {
				throw new CompilerException("Non-local variables not supported yet.");
			}
			return;
		}
		
		if(op instanceof Assignment) {
			Assignment a = (Assignment)op;
			
			this.emmit(a.getArgument(), depth + 1);
			JType jt = JType.fromILType(a.getType());
			
			if(a.slink() instanceof Function) {
				Function f = (Function)a.slink();
				int i = f.getLVMap()[a.getTarget().localID()];
				if(depth > 0) out.print("dup\n");
				out.printf("%s\n", jt.storeVar(i)); 
			}
			else {
				throw new CompilerException("Non-local variables not supported yet.");
			}
					
			return;			
		}
		
		if(op instanceof Call) {
			Call c = (Call)op;
						
			for(Expression<? extends Type> e : c.args()) {
				this.emmit(e, depth + 1);
			}
						
			if(c.target() instanceof Builtin) {					
				out.printf("%s\n", ((Builtin)c.target()).getContents(c.slink()));
			}
			else {
				
				
				if(c.target().container().isInterface())
				{
					out.printf("invokeinterface %s/%s %d\n",						
						c.target().container().absolutePath(),
						JType.methodSignature(c.target().declSignature()),
						c.target().callSignature().type.getArity() );
				} else {
					out.printf("%s %s/%s\n",
						(c.target().isStatic() ? "invokestatic" : "invokeinterface"),
						c.target().container().absolutePath(),
						JType.methodSignature(c.target().declSignature()) );
				}
			}	
			return;
		}
		
		if(op instanceof Closure)
		{
			/* TODO: This is a little tricky, first make sure
			 * signatures are correctly generated */
			
			return; 
		}
		
		if(op instanceof TypeConversion) {
			TypeConversion cast = (TypeConversion)op;
			JType st = JType.fromILType(cast.srcType());
			JType dt = JType.fromILType(cast.dstType());
			
			this.emmit( cast.getInnerExpr(), depth + 1 );
			
			if(st.equals(JType.INTEGER) && dt.equals(JType.DOUBLE)) 
				out.print("i2d\n");				
			else if(st.equals(JType.DOUBLE) && dt.equals(JType.INTEGER)) 
				out.print("d2i\n");
			else {
				/* TODO: JVM CAST HERE */
			}
			return;
		}		
				
		if(op instanceof ReturnVoid) {
			out.print("return\n");
			return;						
		}
		
		if(op instanceof Return) {
			Return r = (Return)op;
			/* calculate expression */
			Expression<Type> e = r.returnValue;
			this.emmit(r.returnValue, depth + 1);
			
			/* return */
			JType jt = JType.fromILType(e.getType());
			out.printf("%s\n", jt.returnOp());
			return;						
		}	
		
		if(op instanceof Nop) {
			out.print("nop\n");
			return;
		}
		
		if(op instanceof Branch) {
			Branch branch = (Branch)op;				
			
			Expression<? extends Type> cond = branch.getCondition();			
			if( branch.getOperationB().isNop() ) 
			{					
				Label trueLabel = branch.slink().getUniqueLabel();
				Label falseLabel = branch.slink().getUniqueLabel();
				
				this.emmitCondition(cond, trueLabel, falseLabel);
				out.printf(trueLabel.emmit());
				this.emmit(branch.getOperationA(), 0);
				out.printf(falseLabel.emmit());	
				out.printf("nop\n");
			}
			else {
				Label trueLabel = branch.slink().getUniqueLabel();
				Label falseLabel = branch.slink().getUniqueLabel();
				Label endLabel = branch.slink().getUniqueLabel();
				
				this.emmitCondition(cond, trueLabel, falseLabel);
				trueLabel.emmit();				
				this.emmit(branch.getOperationA(), 0);
				out.printf("goto %s\n", endLabel.getName());
				out.printf(falseLabel.emmit());
				this.emmit(branch.getOperationB(), 0);
				out.printf(endLabel.emmit());	
				out.printf("nop\n");
			}
			return;			
		}
		
		if(op instanceof Loop) {
			Loop loop = (Loop)op;
			Label trueLabel = loop.slink().getUniqueLabel();
			Label falseLabel = loop.slink().getUniqueLabel();
			Label checkLabel = loop.slink().getUniqueLabel();
			
			/* mark as used */
			checkLabel.getName();
			
			/* emmit */			
			out.printf(checkLabel.emmit());
			this.emmitCondition(loop.getCondition(), 
					trueLabel, falseLabel);
			out.printf(trueLabel.emmit());			
			this.emmit(loop.getBodyBlock(), 0);			
			out.printf("goto %s\n", checkLabel.getName());
			out.printf(falseLabel.emmit());		
			out.printf("nop\n");
			return;
		}
				
		if(op instanceof Block) {
			for(Operation opx : (Block)op) 
				this.emmit(opx, depth);
			return;
		}		
		
		throw new CompilerException("Unknown operation to emmit: " + op);
	}
	
	private void emmitCondition(Expression<? extends Type> cond, Label t, Label f) {
		/* we know cond is of type boolean */
		if(cond instanceof Constant) {
			Boolean b = ((Constant<Boolean>)cond).value();
			if(!b) {
				out.printf("goto %s\n", f.getName());								
			}			
			return;
		}
		
		if(cond instanceof VariableValue) {
			this.emmit(cond, 1);
			out.printf("ifeq %s\n", f.getName());
			return;
		}
		
		if(cond instanceof Call) {
			Call c = (Call)cond;
			
			if(! (c.target() instanceof Builtin))
			{
				this.emmit(cond, 1);
				out.printf("ifeq %s\n", f.getName());
			}
			else {
				/* builtin */
				for(Expression<? extends Type> e : c.args()) {
					this.emmit(e, 1);
				}
				
				out.printf(((Builtin)c.target()).getBranchTemplate()
						+ "\n", f.getName() );								
			}			
			return;
		}		
		
		if(cond instanceof Assignment) {
			Assignment a = (Assignment)cond;
			this.emmit(a, 1);
			out.printf("ifeq %s\n", f.getName());				
			
			return;
		}
		
		throw new CompilerException("Condition not implemented yet: " + cond);
	}
	
	private static int calculateMaxStack(Function f) 
	{
		int maxStack;
		
		/* default is size of return type 
		 * assert(JType(void).opsize == 0) */		
		Type rt = f.declSignature().type.getReturnType();
		maxStack = JType.sizeof(rt);
		
		/* every instruction is finite and complete 
		 * i.e. stack height before and after processing 
		 * an operation is the same */ 
		 
		 /* maxStack(f) = max({maxStack(op) : op \in f}) */
		for(Operation op : f) {
			maxStack = Math.max(maxStack, 
					calculateMaxStack(op) );
		}		
		
		return maxStack;		
	}
	
	private static int calculateMaxStack(Operation op) {
		/* Simple ops */
		if(op instanceof Return) {
			return calculateMaxStack(((Return)op).returnValue);
		}		 
		
		if(op instanceof ReturnVoid) {
			return 0;
		}
		
		if(op instanceof Nop) {
			return 0;
		}
		
		/* Expressions */
		if(op instanceof Constant) {
			return JType.sizeof( ((Constant)op).getType() );
		}		
		
		if(op instanceof VariableValue) {
			return JType.sizeof( ((VariableValue)op).getType() );			
		}
		
		if(op instanceof TypeConversion) {
			TypeConversion cast = (TypeConversion)op;
			
			return Math.max(
					JType.sizeof(cast.srcType()), 
					JType.sizeof(cast.dstType())
			);
		}
		
		if(op instanceof Closure) {
			return JType.REFERENCE_SIZE;
		}
		
		if(op instanceof Call) {
			Call c = (Call)op;
			
			/* return type size */
			int m = JType.sizeof( c.getType() );
			
			int k = 0;
			int s = 0;
			int n = 0;
			
			for(Expression<? extends Type> e : c.args()) 
			{	
				n = calculateMaxStack(e);				
				k = JType.sizeof( e.getType() );
								
				/* 
				 * n = space needed to calculate this argument  
				 * k = space to hold values of arguments
				 */ 
				m = Math.max(m, s+n);
				s += k;
			}	
			
			return m+1; /* XXX: fix this ugly hack */ 
		}	
		
		if(op instanceof Assignment) {
			Assignment assign = (Assignment)op;
			int n = calculateMaxStack(assign.getArgument());
			int k = JType.sizeof(assign.getType());
			
			/* we also might need an address */
			return n + k + 1;			
		}
		
		/* complex operations */
		if(op instanceof Branch) {
			Branch branch = (Branch)op;
			
			int cn = calculateMaxStack(branch.getCondition());
			int an = calculateMaxStack(branch.getOperationA());
			int bn = calculateMaxStack(branch.getOperationB());
			
			return Math.max(cn, Math.max(an, bn));
		}
		
		if(op instanceof Loop) {
			Loop loop = (Loop)op;
			
			return Math.max(
					calculateMaxStack(loop.getCondition()),
					calculateMaxStack(loop.getBodyBlock()) );					
		}
		
		/* compund operation */
		if(op instanceof Block) {
			int n = 0;
			
			for(Operation ox : (Block)op) 
				n = Math.max(n, calculateMaxStack(ox));
			
			return n;			
		}
		
		/* in case we missed something */
		throw new CompilerException(
			"Unmatched Operation in calcMaxStack");		
	}
}
