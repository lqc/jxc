package org.lqc.jxc.types;

import org.lqc.jxc.il.Klass;
import org.lqc.util.Relation;

public class KlassType extends Type {
		
	private Klass klass;
	
	public KlassType(Klass klass) {
		this.klass = klass;
	}

	@Override
	public String getShorthand() {
		return "K" + klass.getModuleName() + "e";	
	}

	public Relation compareTo(Type object) {
		if(object instanceof AnyType)
			return Relation.LESSER;
		
		if(!(object instanceof KlassType))
			return Relation.NONCOMPARABLE;
		
		if( ((KlassType)object).getKlass().equals(klass) )
			return Relation.EQUAL;
		else
			return Relation.NONCOMPARABLE;
	}

	/**
	 * @return the klass
	 */
	public Klass getKlass() {
		return klass;
	}

	/**
	 * @param klass the klass to set
	 */
	public void setKlass(Klass klass) {
		this.klass = klass;
	}
	

}
