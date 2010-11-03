package lang.jx;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StreamTokenizer;

public class System extends Module {
	
	private static final StreamTokenizer in;
	private static final PrintStream out;
	
	static {
		in = new StreamTokenizer(
				new InputStreamReader(java.lang.System.in)
		);
		in.parseNumbers();
		
		out = java.lang.System.out;
	}
	
		
	@JVMPrimitive("iadd")
	public static int _ADD(int x, int y) {
		return x + y;
	}
	
	@JVMPrimitive("isub")
	public static int _SUB(int x, int y) {
		return x - y;
	}	
	
	@JVMPrimitive("imul")
	public static int _MUL(int x, int y) {
		return x * y;
	}
	
	@JVMPrimitive("idiv")
	public static int _DIV(int x, int y) {
		return x / y;
	}
	
	@JVMPrimitive("irem")
	public static int _REM(int x, int y) {
		return x % y;
	}
	
	@JVMPrimitive("ineg")
	public static int  _NNEG(int x) {
		return -x;
	}
	
	@JVMPrimitive("dadd")
	public static double _ADD(double x, double y) {
		return x + y;
	}
	
	@JVMPrimitive("dsub")
	public static double _SUB(double x, double y) {
		return x - y;
	}
	
	@JVMPrimitive("dmul")
	public static double _MUL(double x, double y) {
		return x * y;
	}
	
	@JVMPrimitive("ddiv")
	public static double _DIV(double x, double y) {
		return x / y;
	}
	
	@JVMPrimitive("drem")
	public static double _REM(double x, double y) {
		return x % y;
	}
	
	@JVMPrimitive("dneg")
	public static double _NNEG(double x) {
		return -x;
	}
	
	
	@FunctionAnnotation(
			name = "_EQEQ",
			type = "{ int, int -> boolean }"
	)
	@JVMBranch("if_icmpne %s")
	public static  boolean _EQEQ(int x, int y) {
		return (x == y);
	}
	
	@FunctionAnnotation(
			name = "_EQEQ",
			type = "{ double, double -> boolean }"
	)			
	@JVMBranch("dcmpl\nifne %s")
	public static  boolean _EQEQ(double x, double y) {
		return (x == y);
	}
	
	@FunctionAnnotation(
			name = "_NEQEQ",
			type = "{ int, int -> boolean }"
	)			
	@JVMBranch("if_icmpeq %s")
	public static  boolean _NEQEQ(int x, int y) {
		return (x != y);
	}
	
	@FunctionAnnotation(
			name = "_NEQEQ",
			type = "{ double, double -> boolean }"
	)			
	@JVMBranch("dcmpl\n ifeq %s")
	public static boolean _NEQ(double x, double y) {
		return (x != y);
	}
	
	@FunctionAnnotation(
			name = "_GT",
			type = "{ int, int -> boolean }"
	)			
	@JVMBranch("if_icmple %s")
	public static  boolean _GT(int x, int y) {
		return (x > y);
	}
	
	@FunctionAnnotation(
			name = "_GT",
			type = "{ double, double -> boolean }"
	)			
	@JVMBranch("dcmpl\nifle %s")
	public static boolean _GT(double x, double y) {
		return (x > y);
	}
	
	@FunctionAnnotation(
			name = "_GE",
			type = "{ int, int -> boolean }"
	)			
	@JVMBranch("if_icmplt %s")
	public static  boolean _GE(int x, int y) {
		return (x >= y);
	}
	
	@FunctionAnnotation(
			name = "_GE",
			type = "{ double, double -> boolean }"
	)			
	@JVMBranch("dcmpl\niflt %s")
	public static boolean _GE(double x, double y) {
		return (x >= y);
	}
	
	@FunctionAnnotation(
			name = "_LT",
			type = "{ int, int -> boolean }"
	)			
	@JVMBranch("if_icmpge %s")
	public static boolean _LT(int x, int y) {
		return (x < y);
	}
	
	@FunctionAnnotation(
			name = "_LT",
			type = "{ double, double -> boolean }"
	)			
	@JVMBranch("dcmpg\nifge %s")
	public static boolean _LT(double x, double y) {
		return (x < y);
	}
	
	@FunctionAnnotation(
			name = "_LE",
			type = "{ int, int -> boolean }"
	)
	@JVMBranch("if_icmpgt %s")
	public static boolean _LE(int x, int y) {
		return (x <= y);
	}	
	
	@FunctionAnnotation(
			name = "_LE",
			type = "{ double, double -> boolean }"
	)			
	@JVMBranch("dcmpg\nifgt %s")
	public static boolean _LE(double x, double y) {
		return (x <= y);
	}
	
	/* Boolean expressions */
	@JVMBranch("if_icmpne %s")
	public static boolean _EQEQ(boolean a, boolean b) {
		return a == b;
	}
	
	@JVMBranch("if_icmpeq %s")
	public static boolean _NEQEQ(boolean a, boolean b) {
		return a != b;
	}
	
	
	@JVMPrimitive("ior") 
	public static boolean _LOR(boolean a, boolean b) {
		return a || b;
	}	
	
	@JVMPrimitive("iand")	
	public static boolean _LAND(boolean a, boolean b) {
		return (a && b);
	}
		
	@JVMPrimitive("iconst_1\nixor")	
	public static boolean _LNEG(boolean a) {
		if (a) return false;
		else return true;
	}
		
	@FunctionAnnotation(
			name = "printInt",
			type = "{ int -> void }"
	)
	public static void printInt(int i) {
		out.println(i);
	}
		
	@FunctionAnnotation(
			name = "printDouble",
			type = "{ double -> void }"
	)	
	public static void printDouble(double d) {
		out.println(d);
	}
	
	public static void printBool(boolean b) {
		out.println(b);
	}
	
	@FunctionAnnotation(
			name = "printString",
			type = "{ string -> void }"
	)
	public static void printString(String str) {
		out.println(str);
	}
	
	public static void print(boolean b) {
		out.print(b);
	}
	
	public static void print(int x) {
		out.print(x);
	}
	
	public static void print(double x) {
		out.print(x);
	}
	
	public static void println() {
		out.println();
	}
	
	@FunctionAnnotation(
			name = "readInt",
			type = "{ -> int}"
	)
	public static int readInt()
		throws IOException
	{		 
		in.nextToken();
		switch(in.ttype) {
			case StreamTokenizer.TT_NUMBER:
				return (int)in.nval;		
			default:
				throw new RuntimeException("Expected number as input");
		}		
	}
	
	@FunctionAnnotation(
			name = "readDouble",
			type = "{ -> double}"
	)
	public static double readDouble()
		throws IOException
	{		 
		in.nextToken();
		switch(in.ttype) {
			case StreamTokenizer.TT_NUMBER:
				return (int)in.nval;		
			default:
				throw new RuntimeException("Expected number as input");
		}
	}
	
	@FunctionAnnotation(
			name = "readString",
			type = "{ -> string}"
	)
	public static String readString()
		throws IOException
	{		 
		in.nextToken();
		switch(in.ttype) {
			case StreamTokenizer.TT_WORD:
				return in.sval;	
			default:
				throw new RuntimeException("Expected string as input");
		}
	}

}
