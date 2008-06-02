package org.lqc.jxc.transform;

import static org.lqc.util.TriStateLogic.FALSE;

import java.util.HashMap;

import org.lqc.jxc.CompilerException;
import org.lqc.jxc.il.Assignment;
import org.lqc.jxc.il.Block;
import org.lqc.jxc.il.Branch;
import org.lqc.jxc.il.Call;
import org.lqc.jxc.il.Callable;
import org.lqc.jxc.il.Closure;
import org.lqc.jxc.il.Constant;
import org.lqc.jxc.il.Expression;
import org.lqc.jxc.il.Function;
import org.lqc.jxc.il.Loop;
import org.lqc.jxc.il.Klass;
import org.lqc.jxc.il.Operation;
import org.lqc.jxc.il.Return;
import org.lqc.jxc.il.ReturnVoid;
import org.lqc.jxc.il.TypeConversion;
import org.lqc.jxc.il.Variable;
import org.lqc.jxc.il.VariableValue;
import org.lqc.jxc.types.PrimitiveType;
import org.lqc.jxc.types.Type;
import org.lqc.util.TriStateLogic;


public class ILFlowAnalyzer extends AbstractILVisitor<ILFlowAnalyzer.FlowContext> 
{
	private class Info {
		/** Has this entity been read. */
		public TriStateLogic read;
		
		/** Has this entity been written to. */
		public TriStateLogic write;
				
		public Info() {
			read = FALSE;
			write = FALSE;		
		}
		
		public String toString() {
			return String.format("{R=%s, W=%s}", read, write);
		}
	}
	
	private class FlowContext 
	{		
		public FlowContext() 
		{	
			if(current() != null)
				accessState = current().accessState;
			else
				accessState = TriStateLogic.TRUE;
			
			returnState = TriStateLogic.FALSE;
		}		
				
		public TriStateLogic accessState;
		public TriStateLogic returnState;
		
		public Info getVarInfo(Variable v) {
			return vmap.get(v);
		}
		
		public void putVarInfo(Variable v, Info i) {
			vmap.put(v, i);
		}
	}
	
	private HashMap<Variable, Info> vmap;
	// private HashMap<Function, Info> fmap;
	
	public ILFlowAnalyzer() {
		super();
		
		vmap = new HashMap<Variable, Info>();		
		// fmap = new HashMap<Function, Info>();
	}
	
	public void analyze(Klass m) 
	{
		if(m.isInterface()) return;
		
		for(Callable c : m.allCallables())
		{
			if(c.isAbstract()) continue;
			
			if(c instanceof Function) {
				Function f = (Function)c;
				this.begin(f);
				
				for(Operation op : f) {
					op.visit(this);
				}
				
				this.end(f);
			}
		}	
	}
	
	private <A extends Type> TriStateLogic 
		evaluateBoolExpr(Expression<A> e) {
		if(e instanceof Constant) {
			Constant<Boolean> c = (Constant<Boolean>)
						(Expression<PrimitiveType>)e;
			if(c.value().booleanValue())
				return TriStateLogic.TRUE;
			else
				return TriStateLogic.FALSE;				
		}
		
		return TriStateLogic.UNKNOWN;
	}
	
	@Override
	protected FlowContext createContext() {
		return new FlowContext();		
	}
	
	@Override
	protected void finalizeContext(FlowContext ctx) {
		if(current() == null) return;
		
		current().returnState = current().returnState.or(ctx.returnState);
		current().accessState = current().accessState.and(ctx.returnState.neg());
	}

	@Override
	protected void enter(Assignment op) 
	{
		op.setReachable(current().accessState);
		Info info = current().getVarInfo(op.getTarget());
		info.write = current().accessState.or(info.write);
	}
	
	@Override
	protected void leave(Assignment op) {
		// nothing 	
	}

	@Override
	protected void enter(Branch op) {
		op.setReachable(current().accessState);
	}
	
	@Override
	protected void beforeBranchA(Branch op) {
		// determine access		
		TriStateLogic x = evaluateBoolExpr(op.getCondition());		
		current().accessState = x;		
	}
	
	@Override
	protected void afterBranchA(Branch op) {
				
	}

	@Override
	protected void beforeBranchB(Branch op) {
		// determine access
		TriStateLogic x = evaluateBoolExpr(op.getCondition());
		current().accessState = x.neg();		
	}
	
	@Override
	protected void afterBranchB(Branch op) {
		
	}
	
	@Override
	protected void leave(Branch op, FlowContext a, FlowContext b) {
		
		
		if( op.getOperationA().isReachable().equals(TriStateLogic.UNKNOWN)
		 && op.getOperationB().isReachable().equals(TriStateLogic.UNKNOWN) )
		{
			// we don't know which branch will be executed 
			// if both can return, then we also can
			// if both are unsure, we are unsure
			// if any is able not to, we are unsure
			// if both aren't able to return we can't also						
			current().returnState = a.returnState.or(b.returnState);			
		}
		else if( op.getOperationA().isReachable().equals(TriStateLogic.TRUE) ) {
			current().returnState = a.returnState;
		}
		else if( op.getOperationB().isReachable().equals(TriStateLogic.TRUE) ) {
			current().returnState = b.returnState;
		}
		else {
			/* there are no more cases 'cause if a = ~b */
			throw new CompilerException("[Flow] Error while leaving branch");
		}			 			
	}

	@Override
	protected void enter(Call op) {
		op.setReachable(current().accessState);
	}
	
	@Override
	protected void leave(Call op) {
		// nothing		
	}

	@Override
	protected void enter(Loop op) {
		op.setReachable(current().accessState);
	}
	
	@Override
	protected void leave(Loop op) {
		// TODO Auto-generated method stub		
	}

	@Override
	protected void enter(Return op) {
		op.setReachable(current().accessState);
		current().returnState = TriStateLogic.TRUE;	
	}	

	@Override
	protected void leave(Return op) {		
		current().accessState = TriStateLogic.FALSE;
	}
	
	@Override
	public <S> void process(Constant<S> op) {
		op.setReachable(current().accessState);
	}

	@Override
	public void process(ReturnVoid op) {
		op.setReachable(current().accessState);
		current().returnState = TriStateLogic.TRUE;
		current().accessState = TriStateLogic.FALSE;
	}

	@Override
	public void process(TypeConversion op) {
		op.setReachable(current().accessState);		
	}

	@Override
	public void process(VariableValue op) {
		op.setReachable(current().accessState);
		Info info = current().getVarInfo(op.reference());
		info.read = current().accessState.or(info.read);
		
	}

	@Override
	protected void enter(Block op) {
		op.setReachable(current().accessState);
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void leave(Block op) {
		// TODO Auto-generated method stub		
	}

	

	@Override
	protected void enter(Function f) {
		for(Variable v : f.allVariables())
			current().putVarInfo(v, new Info());		
	}

	@Override
	protected void leave(Function f) {
		// TODO report unused variables
		
		switch(current().returnState) {
			case TRUE:
				/* everything ok */
				break;			
			case UNKNOWN:	
			case FALSE:
				if(f.declSignature().type.getReturnType().equals(Type.VOID))
						f.addOp( new ReturnVoid(f, f.lastLineNumber()) );
				else
					throw new CompilerException(
					String.format("Function '%s' needs to return.", f.toString())
				);				
		}
	}

	public void process(Closure closure) {
		closure.setReachable(current().accessState);		
	}

}
