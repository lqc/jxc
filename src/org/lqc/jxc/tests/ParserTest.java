package org.lqc.jxc.tests;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;

import java_cup.runtime.Symbol;
import junit.framework.Assert;
import junit.framework.TestCase;

import org.lqc.jxc.Lexer;
import org.lqc.jxc.Parser;
import org.lqc.jxc.tokens.Program;
import org.lqc.jxc.transform.ScopeAnalyzer;

public class ParserTest extends TestCase {
	
	private File f;
	private boolean positive;

	public ParserTest(String name, File f, boolean positive) {
		super(name);
		this.f = f;
		this.positive = positive;		
	}
	
	public void runTest() throws Exception {		
		ByteArrayOutputStream bs;
		PrintStream log = new PrintStream( bs = new ByteArrayOutputStream());
		
		System.out.printf("*** START OF TEST: %s ***\n", this.getName());
		
		try {
			InputStream is = new FileInputStream(f);
			
			Lexer scanner = new Lexer(is);
			Parser parser = new Parser(scanner);
			
			Symbol root = parser.parse();
			Program p = (Program)root.value;
			// p.visitNode(new PrintingVisitor(log));
			p.visitNode(new ScopeAnalyzer());
			
			p = p;
			
		} catch (FileNotFoundException e) {			
			Assert.fail("Test file: '" + f.getName() + "' is missing.");
		} catch (Exception e) {
			if(positive)
				throw e;			
			
			log.printf("Exception: " + e.getMessage());			
			System.out.println(bs.toString());		
			System.out.printf("*** END OF TEST: %s ***\n", this.getName());
			return;
		}
		
		if(!positive) {
			System.out.println(bs.toString());
			System.out.printf("*** TEST: %s FAILED ***\n", this.getName());
			Assert.fail("Malformed input accepted.");
		}
		
		System.out.println(bs.toString());		
		System.out.printf("*** END OF TEST: %s ***\n", this.getName());
	}
	
	

}
