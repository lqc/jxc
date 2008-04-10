package org.lqc.jxc.tests;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.lqc.jxc.types.AnyType;
import org.lqc.jxc.types.FunctionType;
import org.lqc.jxc.types.PrimitiveType;
import org.lqc.jxc.types.Type;
import org.lqc.jxc.types.VoidType;

public class TypeTest extends TestCase {
	
	public void testVoidType() {
		Type v = new VoidType();
		
		Assert.assertTrue(v.isSupertypeOf(Type.VOID));
		Assert.assertFalse(v.isSupertypeOf(Type.ANY));
		Assert.assertFalse(v.isSupertypeOf(PrimitiveType.INT));
		Assert.assertFalse(v.isSupertypeOf(PrimitiveType.REAL));
		Assert.assertFalse(v.isSupertypeOf(PrimitiveType.STRING));
		Assert.assertFalse(v.isSupertypeOf(PrimitiveType.BOOLEAN));
	}
	
	public void testAnyType() {
		Type t = new AnyType();
		
		Assert.assertTrue(t.isSupertypeOf(Type.VOID));
		Assert.assertTrue(t.isSupertypeOf(Type.ANY));		
		Assert.assertTrue(t.isSupertypeOf(PrimitiveType.INT));
		Assert.assertTrue(t.isSupertypeOf(PrimitiveType.REAL));
		Assert.assertTrue(t.isSupertypeOf(PrimitiveType.STRING));
		Assert.assertTrue(t.isSupertypeOf(PrimitiveType.BOOLEAN));
	}
	
	public void testSimpleFuncType() {
		FunctionType t = new FunctionType(Type.VOID, PrimitiveType.INT);
		FunctionType any = new FunctionType(Type.VOID, PrimitiveType.ANY);
				
		Assert.assertTrue(t.isSupertypeOf(t));
		
		Assert.assertFalse(t.isSupertypeOf(Type.VOID));
		Assert.assertFalse(Type.VOID.isSupertypeOf(t));
		
		Assert.assertTrue( Type.ANY.isSupertypeOf(t) );		
		Assert.assertFalse( t.isSupertypeOf(Type.ANY) );
		
		Assert.assertFalse(t.isSupertypeOf(PrimitiveType.INT));
		Assert.assertFalse(PrimitiveType.INT.isSupertypeOf(t));
		
		Assert.assertFalse(t.isSupertypeOf(PrimitiveType.REAL));
		Assert.assertFalse(PrimitiveType.REAL.isSupertypeOf(t));
		
		Assert.assertFalse(t.isSupertypeOf(PrimitiveType.STRING));
		Assert.assertFalse(PrimitiveType.STRING.isSupertypeOf(t));
		
		Assert.assertFalse(t.isSupertypeOf(PrimitiveType.BOOLEAN));
		Assert.assertFalse(PrimitiveType.BOOLEAN.isSupertypeOf(t));		
		
		Assert.assertTrue(t.isSupertypeOf(any));
		Assert.assertFalse(any.isSupertypeOf(t));		
	}
	
	public void testFuncReturnType() {
		FunctionType t = new FunctionType(PrimitiveType.INT, PrimitiveType.INT, PrimitiveType.STRING);
		FunctionType v = new FunctionType(Type.VOID, PrimitiveType.INT, PrimitiveType.STRING);
		FunctionType a = new FunctionType(Type.ANY, PrimitiveType.INT, PrimitiveType.STRING);
		
		Assert.assertTrue( a.isSupertypeOf(t) );
		Assert.assertFalse( t.isSupertypeOf(a) );
		
		Assert.assertTrue( a.isSupertypeOf(v) );
		Assert.assertFalse( v.isSupertypeOf(a) );
		
		Assert.assertFalse( t.isSupertypeOf(v) );
		Assert.assertFalse( v.isSupertypeOf(t) );		
	}

}
