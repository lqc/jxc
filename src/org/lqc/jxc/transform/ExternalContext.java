package org.lqc.jxc.transform;

import java.util.HashSet;
import java.util.Set;

import org.lqc.jxc.CompilerException;
import org.lqc.jxc.il.Callable;
import org.lqc.jxc.il.Module;
import org.lqc.jxc.tokens.ExternalFuncDecl;
import org.lqc.jxc.tokens.FunctionDecl;
import org.lqc.jxc.types.Type;
import org.lqc.util.NonUniqueElementException;
import org.lqc.util.POSet;
import org.lqc.util.Tuple;

public class ExternalContext extends Context {
	
	public ExternalContext(Context parent, Module m) 
		
	{
		super(parent, m.getModuleName());
		
		try {
			for(Callable c : m.allFunctions())
				this.put( new ExternalFuncDecl(c) );
		} catch (NonUniqueElementException e) {
			throw new CompilerException("Malformed external module: " 
					+ m.absolutePath() );
		}
	}	
	
	public Set<ExternalFuncDecl> getAllFunctionDecl() {
		Set<ExternalFuncDecl> v = new HashSet<ExternalFuncDecl>();
		
		for(POSet<Tuple<Type>, FunctionDecl> poset : fmap.values()) {
			for(FunctionDecl d : poset.values()) {
				v.add( (ExternalFuncDecl)d);			
			}
		}				
		return v;
	}

}
