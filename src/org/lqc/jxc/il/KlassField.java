package org.lqc.jxc.il;

import org.lqc.jxc.types.Type;

public class KlassField extends Variable<Klass> {
	
	protected Variable<?> source;
	protected KlassFieldRef template;
	
	public KlassField(Variable<?> instance, KlassFieldRef ref)
	{	
		super(ref.slink, -1, ref.signature);
		
		source = instance;
		template = ref;
	}
	
	@Override
	public int localID() {
		throw new UnsupportedOperationException(
			"Class fields can't be accessed as local variables"
		);		
	}
	
	public String getAccessName() {
		return slink.getAbsoluteName() + "/" + signature.name;
	}

	public Variable<?> getSource() {
		return source;
	}
	
	public void setSource(Variable<?> source) {
		this.source = source;
	}
	
	public KlassFieldRef template() {
		return template;
	}
}
