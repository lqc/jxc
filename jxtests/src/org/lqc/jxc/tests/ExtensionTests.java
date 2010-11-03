package org.lqc.jxc.tests;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ExtensionTests extends TestSuite {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.lqc.jxc.JxCompiler");
		//$JUnit-BEGIN$
		File dir;
		File[] tests;		
		
		FileFilter filter = new FileFilter() {
			public boolean accept(File pathname) {
				if(!pathname.isFile()) return false;
				
				return Pattern.matches("[a-zA-Z0-9\\-_]+\\.jl", 
						pathname.getName());				
			}
			
		};
		
		System.setErr(System.out);
		
		dir  = new File("examples/extensions/assignexp");		
		tests = dir.listFiles(filter);		
		for(File file : tests) {
			suite.addTest( new ParserTest("xtest-"+file.getName(), file, true));
		} 
		
		dir  = new File("examples/extensions/typecast");		
		tests = dir.listFiles(filter);		
		for(File file : tests) {
			suite.addTest( new ParserTest("xtest-"+file.getName(), file, true));
		} 
	
		dir  = new File("examples/extensions/overloading");		
		tests = dir.listFiles(filter);		
		for(File file : tests) {
			suite.addTest( new ParserTest("xtest-"+file.getName(), file, true));
		} 
		
		dir  = new File("examples/extensions/for");		
		tests = dir.listFiles(filter);		
		for(File file : tests) {
			suite.addTest( new ParserTest("xtest-"+file.getName(), file, true));
		} 

		//$JUnit-END$
		return suite;
	}
	
}
