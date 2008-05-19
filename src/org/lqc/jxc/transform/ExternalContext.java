package org.lqc.jxc.transform;

import org.lqc.jxc.il.Callable;
import org.lqc.jxc.il.Module;
import org.lqc.jxc.tokens.ExternalFuncDecl;
import org.lqc.util.NonUniqueElementException;

public class ExternalContext extends Context {
	
	public ExternalContext(Context parent, Module m) 
		throws NonUniqueElementException	
	{
		super(parent, m.getModuleName());
		
		for(Callable c : m.allFunctions())
			this.put( new ExternalFuncDecl(c) );
	}	

}
