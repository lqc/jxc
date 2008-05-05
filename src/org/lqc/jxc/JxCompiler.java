package org.lqc.jxc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import java_cup.runtime.Symbol;

import org.lqc.jxc.tokens.CompileUnit;
import org.lqc.jxc.transform.ControlFlowAnalyzer;
import org.lqc.jxc.transform.ScopeAnalyzer;


public class JxCompiler {
	
	public static final int MAJOR_VERSION = 0;
	public static final int MINOR_VERSION = 1;
	
	/* TODO refactor error messages 
	 *  1. add line and column information
	 */

	public static void main(String args[])		
	{		
		File in;
		File out;
		int retcode = 0;
		
		System.out.printf("jxCompiler version %d.%d\n",
				MAJOR_VERSION, MINOR_VERSION);
		
		if(args.length >= 1) {
			in = new File(args[0].trim());
		}
		else {
			System.out.println("[Error] Too few arguments");
			System.exit(1);
			return;			
		}
		
		if(args.length <= 2) {
			if(args.length == 2)
				out = new File(args[1].trim());
			else
				out = new File("output.j");
		}
		else {
			System.out.println("[Error] Too many arguments");
			System.exit(1);
			return;
		}		
		
		try {
			/* files opended, go to parse phase */
			
			InputStream is = new FileInputStream(in);
			Lexer scanner = new Lexer(is);
			Parser parser = new Parser(scanner);
		
			Symbol root = parser.parse();
			
			/* Extract the program node */
			CompileUnit p = (CompileUnit)root.value;
			
			/* Visibility and type checking */
			p.visitNode(new ScopeAnalyzer());
			
			/* flow analysis */
			//ControlFlowAnalyzer cfa = new ControlFlowAnalyzer();
			//p.visitNode(cfa);
						 
			//for(CompilerWarning w : cfa.getWarnings()) {
			//	System.out.println(w.getMessage());
			//}
			
			/* TODO smash the tree to binary IL */
			
			/* TODO generate Jasmin from IL Tree */
			
		} catch(FileNotFoundException e) {
			System.out.printf("[Error] Input file '%s' not found.", in.getPath());
			retcode = 1;
		} catch(CompilerException e) {
			System.out.println(e.getMessage());
			retcode = 1;						
		} catch(Exception e) {
			e.printStackTrace();
			retcode = 1;
			
		}	
		
		System.exit(retcode);		
	}
}
