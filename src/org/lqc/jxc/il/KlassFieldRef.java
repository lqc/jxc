package org.lqc.jxc.il;

import org.lqc.jxc.types.PrimitiveType;
import org.lqc.jxc.types.Type;

public class KlassFieldRef extends Variable<Klass> 
{	
	protected boolean dereferable;
	protected Constant<?> initVal;
	
	public KlassFieldRef(Klass k, Signature<? extends Type> sig,
			boolean isstatic) {
		super(k, -1, sig);
		
		this.dereferable = !isstatic;
	}
	
	public KlassFieldRef(Klass k, String name, Constant<?> initVal) {
		this(k, new Signature<PrimitiveType>(name, initVal.type), true);
		
		this.initVal = initVal;		
	}
	
	public String getAccessName() {
		return slink.getAbsoluteName() + "/" + signature.name;
	}
	
	@Override
	public int localID() {
		throw new UnsupportedOperationException(
			"This isn't a real var - can't be accessed as local variables"
		);		
	}
	
	public boolean isDereferable() {
		return dereferable;
	}
	
	public KlassField deref(Variable<?> instance) {
		return new KlassField(instance, this);				
	}
	
	public Constant<?> initialValue() {		
		return initVal;
	}

}
