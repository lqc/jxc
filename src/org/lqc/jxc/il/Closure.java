package org.lqc.jxc.il;

import org.lqc.jxc.transform.ILVisitor;
import org.lqc.jxc.types.FunctionType;
import org.lqc.jxc.types.KlassType;
import org.lqc.jxc.types.Type;

public class Closure extends Klass {
	
	/**
	 * TODO:
	 *  odwrócić kolejność przy set field
	 *  dodać 'dup' przy konstruktorach
	 */ 
	
	public static final String PREFIX = "Closure";
	
	public Closure(Function p) {
		super(p.getUniqueLambdaName(), false);		
		this.external = true;		
		this.parent = p;
		this.slink = p;
		
		
	}
	
	private Function parent;
	private Function def_constr;
	
	public void produceLambda(Function f) {
		addImplements(PREFIX + f.signature.type.getShorthand() );
				 
		/* put the call */
		this.fmap.put(f.signature, f);
		
		/* we also need a constructor to wrap the frame */
		Function.Frame frame = parent.frame;
		Signature<FunctionType> sig;
		
		/* we need a field to hold the frame */
		Signature<KlassType> fieldsig = 
			new Signature<KlassType>(
				parent.callFrameVar.signature.name, frame.getType());
								
		sig = new Signature<FunctionType>("<init>",
			new FunctionType(Type.VOID, frame.getType()) );
		
		/* The default constructor */
		Function constr = this.newFunc(f.line, sig, false);
		
		def_constr = constr;
		
		Variable<?> _self = constr.newArg( 
				new Signature<KlassType>("_self", this.getType()) );
		Variable<?> frame_arg = constr.newArg(
				new Signature<KlassType>("frame", frame.getType()) );
		
		Variable<Klass> frame_field = 
			this.newInstanceVar(_self, fieldsig, false);
		
		Callable superconstr = this.getBaseKlass().get(
			new Signature<FunctionType>("<init>",new FunctionType(Type.VOID))
		); 
			
		Call scall = new Call(constr, f.line, superconstr, Call.Proto.NONVIRTUAL);
		scall.addArgument( new VariableValue(constr, f.line, _self) );
		constr.addOp( scall );
		
		/* store the frame reference */
		Assignment i = new Assignment(constr, f.line, frame_field,
				new VariableValue(constr, f.line, frame_arg)
		); 
		constr.addOp(i);
		constr.addOp(new ReturnVoid(constr, f.line) );		
	}
	
	public Function getDefaultConstructor() {
		return def_constr;
	}

	public <T> void visit(ILVisitor<T> v) {
		v.process(this);
	}

}
