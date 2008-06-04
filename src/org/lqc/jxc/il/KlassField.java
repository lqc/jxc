package org.lqc.jxc.il;

import org.lqc.jxc.types.Type;

public class KlassField extends Variable<Klass> {
	
	protected Variable<?> source; 
	
	public KlassField(Klass k, Variable<?> instance, 
			Signature<? extends Type> sig) {
		super(k, -1, sig);
		
		source = instance;
	}
	
	@Override
	public int localID() {
		throw new UnsupportedOperationException(
			"Class fields can't be accessed as local variables"
		);		
	}
	
	public String getAccessName() {
		return slink.getKlassName() + "/" + signature.name;
	}

	public Variable<?> getSource() {
		return source;
	}
	
	public void setSource(Variable<?> source) {
		this.source = source;
	}
	
}
