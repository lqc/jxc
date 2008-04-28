package org.lqc.jxc.transform;

import static org.lqc.jxc.types.PrimitiveType.BOOLEAN;
import static org.lqc.jxc.types.PrimitiveType.INT;
import static org.lqc.jxc.types.PrimitiveType.REAL;
import static org.lqc.jxc.types.PrimitiveType.STRING;
import static org.lqc.jxc.types.Type.VOID;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.lqc.jxc.CompilerException;
import org.lqc.jxc.tokens.Declaration;
import org.lqc.jxc.tokens.FunctionDecl;
import org.lqc.jxc.tokens.VarDecl;
import org.lqc.jxc.types.FunctionType;
import org.lqc.jxc.types.Type;
import org.lqc.util.DAGraph;
import org.lqc.util.ElementNotFoundException;
import org.lqc.util.NonUniqueElementException;
import org.lqc.util.POSet;
import org.lqc.util.Tuple;

public class Context {

	private HashMap<String, POSet<Tuple<Type>, FunctionDecl>> fmap;
	private HashMap<String, VarDecl> vmap;
	private Context parent;
	private String name;

	protected Context() {
		this(null, "<builtin>");
	}

	public static Context getBuiltins() {
		/* Define builtin functions */
		Context ctx = new Context();

		FunctionDecl func;

		try {
			/* Integer numeric operations */
			func = new FunctionDecl("_ADD", INT, INT, INT);			
			ctx.put(func);
			func = new FunctionDecl("_SUB", INT, INT, INT);					
			ctx.put(func);
			func = new FunctionDecl("_TIMES", INT, INT, INT);
			ctx.put(func);
			func = new FunctionDecl("_DIVIDE", INT, INT, INT);
			ctx.put(func);
			func = new FunctionDecl("_NNEG", INT, INT);
			ctx.put(func);
			func = new FunctionDecl("_DEC", INT, INT);
			ctx.put(func);
			func = new FunctionDecl("_INC", INT, INT);
			ctx.put(func);

			/* Integer Boolean operators */
			func = new FunctionDecl("_EQEQ", BOOLEAN, INT, INT);
			ctx.put(func);
			func = new FunctionDecl("_NEQEQ", BOOLEAN, INT, INT);
			ctx.put(func);
			func = new FunctionDecl("_GT", BOOLEAN, INT, INT);
			ctx.put(func);
			func = new FunctionDecl("_LT", BOOLEAN, INT, INT);
			ctx.put(func);
			func = new FunctionDecl("_GTEQ", BOOLEAN, INT, INT);
			ctx.put(func);
			func = new FunctionDecl("_LTEQ", BOOLEAN, INT, INT);
			ctx.put(func);

			/* Real numeric operations */
			func = new FunctionDecl("_ADD", REAL, REAL, REAL);
			ctx.put(func);
			func = new FunctionDecl("_SUB", REAL, REAL, REAL);
			ctx.put(func);
			func = new FunctionDecl("_TIMES", REAL, REAL, REAL);
			ctx.put(func);
			func = new FunctionDecl("_DIVIDE", REAL, REAL, REAL);
			ctx.put(func);
			func = new FunctionDecl("_NNEG", REAL, REAL);
			ctx.put(func);

			/* Real Boolean operators */
			func = new FunctionDecl("_EQEQ", BOOLEAN, REAL, REAL);
			ctx.put(func);
			func = new FunctionDecl("_NEQEQ", BOOLEAN, REAL, REAL);
			ctx.put(func);
			func = new FunctionDecl("_GT", BOOLEAN, REAL, REAL);
			ctx.put(func);
			func = new FunctionDecl("_LT", BOOLEAN, REAL, REAL);
			ctx.put(func);
			func = new FunctionDecl("_GTEQ", BOOLEAN, REAL, REAL);
			ctx.put(func);
			func = new FunctionDecl("_LTEQ", BOOLEAN, REAL, REAL);
			ctx.put(func);

			/* Boolean boolean operators */
			func = new FunctionDecl("_EQEQ", BOOLEAN, BOOLEAN, BOOLEAN);
			ctx.put(func);
			func = new FunctionDecl("_LAND", BOOLEAN, BOOLEAN, BOOLEAN);
			ctx.put(func);
			func = new FunctionDecl("_LOR", BOOLEAN, BOOLEAN, BOOLEAN);
			ctx.put(func);
			func = new FunctionDecl("_LNEG",BOOLEAN, BOOLEAN);
			ctx.put(func);

			/* I/O Operations */
			func = new FunctionDecl("readInt", INT);
			ctx.put(func);
			func = new FunctionDecl("readDouble", REAL);
			ctx.put(func);
			func = new FunctionDecl("printInt", VOID, INT);
			ctx.put(func);
			func = new FunctionDecl("printDouble", VOID, REAL);
			ctx.put(func);
			func = new FunctionDecl("printString", VOID, STRING);
			ctx.put(func);

		} catch (NonUniqueElementException e) {
			throw new CompilerException(
				"[INTERNAL] Overlaping built-in definitions.", e);
		}

		return ctx;
	}

	public Context(Context parent, String name) {
		fmap = new HashMap<String, POSet<Tuple<Type>, FunctionDecl>>();
		vmap = new HashMap<String, VarDecl>();
		this.parent = parent;
		this.name = name;
	}

	public FunctionDecl getFunction(String id, FunctionType t)
			throws ElementNotFoundException 
	{
		POSet<Tuple<Type>, FunctionDecl> set = fmap.get(id);
		try {
			return set.find(t.getArgumentTypes());
		} catch (ElementNotFoundException e) {
			if (parent != null)
				return parent.getFunction(id, t);
			throw e;
		} catch (NullPointerException e) {
			if (parent != null)
				return parent.getFunction(id, t);
			throw new ElementNotFoundException();
		}
	}

	public VarDecl getVariable(String id) throws ElementNotFoundException {
		VarDecl var = vmap.get(id);

		if (var != null)
			return var;

		if (parent != null)
			return parent.getVariable(id);

		throw new ElementNotFoundException();
	}

	public void put(FunctionDecl d) throws NonUniqueElementException {
		POSet<Tuple<Type>, FunctionDecl> set = fmap.get(d.getID());

		if (set == null) {
			set = new DAGraph<Tuple<Type>, FunctionDecl>();
			fmap.put(d.getID(), set);
		}

		set.insert(d.getType().getArgumentTypes(), d);
	}

	public void put(VarDecl d) throws NonUniqueElementException {
		if (vmap.containsKey(d.getID()))
			throw new NonUniqueElementException();

		vmap.put(d.getID(), d);
	}

	public String toString() {
		if (parent != null)
			return parent.toString() + " :: " + this.name;

		return this.name;
	}
	
	public Set<Declaration> getAllDeclarations() {
		Set<Declaration> v = new HashSet<Declaration>();
		
		for(POSet<Tuple<Type>, FunctionDecl> poset : fmap.values()) {
			v.addAll( poset.values() );			
		}
		
		v.addAll( vmap.values() );		
		return v;
	}
}
