package org.lqc.jxc.tests;

import static org.lqc.jxc.types.PrimitiveType.INT;
import static org.lqc.jxc.types.PrimitiveType.REAL;
import static org.lqc.jxc.types.PrimitiveType.STRING;
import static org.lqc.jxc.types.Type.VOID;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.lqc.jxc.tokens.FunctionDecl;
import org.lqc.jxc.transform.Context;
import org.lqc.jxc.types.FunctionType;
import org.lqc.jxc.types.Type;
import org.lqc.util.ElementNotFoundException;
import org.lqc.util.MultiplyMatchException;
import org.lqc.util.NonUniqueElementException;
import org.lqc.util.PathID;
import org.lqc.util.Relation;


public class TypeTest {
	
	Context ctx;
	
	@Before 
	public void setUp() {
		ctx = new Context(null, "<test>");		
	}
	
	@After 
	public void tearDown() {
		ctx = null;
	}
	
	private FunctionDecl makeDecl(String id, Type rt, Type... args)
	{
		FunctionType ft = new FunctionType(rt, args);
		return new FunctionDecl(-1, -1, id, ft);
	}
	
	@Test
	public void realBeforeInt()
		throws NonUniqueElementException, 
				ElementNotFoundException,
				MultiplyMatchException
	{
		FunctionDecl d1, d2, ret;
		
		ctx.put(d1 = makeDecl("f", VOID, REAL, REAL));
		ctx.put(d2 = makeDecl("f", VOID, INT, INT));		
		
		ret = ctx.getFunction("f", new FunctionType(VOID, REAL, REAL));
		Assert.assertEquals(d1, ret);
		
		ret = ctx.getFunction("f", new FunctionType(VOID, INT, INT));
		Assert.assertEquals(d2, ret);
		
		ret = ctx.getFunction("f", new FunctionType(VOID, REAL, INT));
		Assert.assertEquals(d1, ret);
		
		ret = ctx.getFunction("f", new FunctionType(VOID, INT, REAL));
		Assert.assertEquals(d1, ret);		
	}
	
	@Test
	public void intBeforeReal()
		throws NonUniqueElementException, 
				ElementNotFoundException,
				MultiplyMatchException
	{
		FunctionDecl d1, d2, ret;
		
		ctx.put(d2 = makeDecl("f", VOID, INT, INT));
		ctx.put(d1 = makeDecl("f", VOID, REAL, REAL));				
		
		ret = ctx.getFunction("f", new FunctionType(VOID, REAL, REAL));
		Assert.assertEquals(d1, ret);
		
		ret = ctx.getFunction("f", new FunctionType(VOID, INT, INT));
		Assert.assertEquals(d2, ret);
		
		ret = ctx.getFunction("f", new FunctionType(VOID, REAL, INT));
		Assert.assertEquals(d1, ret);
		
		ret = ctx.getFunction("f", new FunctionType(VOID, INT, REAL));
		Assert.assertEquals(d1, ret);						
	}
	
	@Test(expected = MultiplyMatchException.class)	
	public void multiplyMatch()
		throws NonUniqueElementException, 
				ElementNotFoundException,
				MultiplyMatchException
	{
		FunctionDecl d1, d2, d3, ret;
		
		ctx.put(d1 = makeDecl("f", VOID, REAL, REAL));	
		ctx.put(d2 = makeDecl("f", VOID, INT, REAL));	
		ctx.put(d3 = makeDecl("f", VOID, REAL, INT));
		
		ret = ctx.getFunction("f", new FunctionType(VOID, INT, INT));		
	}
	
	@Test
	public void simple() 
	{
		Assert.assertTrue("REAL = REAL", 
				REAL.compareTo(REAL).equals(Relation.EQUAL));
		Assert.assertTrue("REAL > INT", 
				REAL.compareTo(INT).equals(Relation.GREATER));
		Assert.assertTrue("INT < REAL", 
				INT.compareTo(REAL).equals(Relation.LESSER));		
		Assert.assertTrue("REAL !~ STRING", 
				REAL.compareTo(STRING).equals(Relation.NONCOMPARABLE));
		Assert.assertTrue("STRING !~ REAL",
				STRING.compareTo(REAL).equals(Relation.NONCOMPARABLE));
	}

}
