package org.lqc.jxc.javavm;

/**
 * 
 * BasicInstr - basic Jasmine instructions.
 *
 */
public enum BasicInstr implements JInstr {
	
	/* local variable instructions */
	
	/** Return TO the address held in variable [arg0]. */
	RET(1,0,0),
	/** Push local variable [arg0] to the stack. */
	ALOAD(1,0,1),
	/** Store top of the stack to local variable [arg0]. */
	ASTORE(1,1,0),
	/** Push local int variable [arg0] to the stack. */
	ILOAD(1,0,1),
	/** Store top of the stack to local int variable [arg0]. */
	ISTORE(1,1,0),
	/** Push local long variable [arg0] to the stack. */
	LLOAD(1,0,1),
	/** Store top of the stack to local long variable [arg0]. */
	LSTORE(1,1,0),
	/** Push local float variable [arg0] to the stack. */
	FLOAD(1,0,1),
	/** Store top of the stack to local float variable [arg0]. */
	FSTORE(1,1,0),
	/** Push local double variable [arg0] to the stack. */
	DLOAD(1,0,1),
	/** Store top of the stack to local doble variable [arg0]. */
	DSTORE(1,1,0),
	
	/* immediate push */
	/** Push immediate value on to the stack. */
	BIPUSH(1,0,1),
	/** Push immediate value on to the stack. */
	SIPUSH(1,0,1),
	/** Increase variable [arg0] by [arg1]. */
	IINC(2,0,0),
	
	/* branch instructions - argument is label */
	GOTO(1,0,0),
    GOTO_W(1,0,0),
    IF_ACMPEQ(1,0,0),
    IF_ACMPNE(1,0,0),
    IF_ICMPEQ(1,0,0),
    IF_ICMPGE(1,0,0),
    IF_ICMPGT(1,0,0),
    IF_ICMPLE(1,0,0),
    IF_ICMPLT(1,0,0),
    IF_ICMPNE(1,0,0),
    IFEQ(1,0,0),
    IFGE(1,0,0),
    IFGT(1,0,0),
    IFLE(1,0,0),
    IFLT(1,0,0),
    IFNE(1,0,0),
    IFNONNULL(1,0,0),
    IFNULL(1,0,0),
    JSR(1,0,0),
    JSR_W(2,0,0),
    
    /* Class operations - [arg0] is a class name */
    ANEWARRAY(1,0,0),
    CHECKCAST(1,0,0),
    INSTANCEOF(1,0,0),
    NEW(1,0,0),
    
    /* Method invokations - non fixed number of arguments */
    /*
     * invokespecial
     * invokenonvirtual
     * invokevirtual
     */
    
    /* Field operations */
    GETFIELD(2, 1, 1),
    GETSTATIC(2, 0, 1),
    PUTFIELD(2, 2, 0),
    PUTSTATIC(2, 1, 0),
    
    /* Primitive array creation */
    NEWARRAY(1, 0, 1),    
    MULTIANEWARRAY(2, 0, 1),
    
    /* Load large constant */
    LDC(1, 0, 1),
	LDC_W(1, 0, 2),
	
	/* Operations without argument */
	/*
	AALOAD 
	AASTORE 
	ACONST_NULL 
	ALOAD_0 
	ALOAD_1 
	ALOAD_2 
	ALOAD_3 
	ARETURN 
	ARRAYLENGTH 
	ASTORE_0 
	ASTORE_1 
	ASTORE_2 
	ASTORE_3 
	ATHROW 
	BALOAD 
	BASTORE 
	BREAKPOINT 
	CALOAD 
	CASTORE 
	D2F 
	D2I 
	D2L 
	DADD 
	DALOAD 
	DASTORE 
	DCMPG 
	DCMPL 
	DCONST_0 
	DCONST_1 
	DDIV 
	DLOAD_0 
	DLOAD_1 
	DLOAD_2 
	DLOAD_3 
	DMUL 
	DNEG 
	DREM 
	DRETURN 
	DSTORE_0 
	DSTORE_1 
	DSTORE_2 
	DSTORE_3 
	DSUB 
	DUP 
	DUP2 
	DUP2_X1 
	DUP2_X2 
	DUP_X1 
	DUP_X2 
	F2D 
	F2I 
	F2L 
	FADD 
	FALOAD 
	FASTORE 
	FCMPG 
	FCMPL
	FCONST_0 
	FCONST_1 
	FCONST_2 
	FDIV 
	FLOAD_0 
	FLOAD_1 
	FLOAD_2 
	FLOAD_3 
	FMUL 
	FNEG 
	FREM 
	FRETURN 
	FSTORE_0 
	FSTORE_1 
	FSTORE_2 
	FSTORE_3 
	FSUB 
	I2D
	I2F
	I2L */
	
	IADD(2,1),
	IALOAD(0,0),
	IAND(2,1),
	IASTORE(0,0),
	ICONST_0(0,1),
	ICONST_1(0,1),
	ICONST_2(0,1),
	ICONST_3(0,1),
	ICONST_4(0,1),
	ICONST_5(0,1),
	/* ICONST_M1(0,0),*/
	IDIV(2,1),
	ILOAD_0(0,1),
	ILOAD_1(0,1),
	ILOAD_2(0,1),
	ILOAD_3(0,1),
	IMUL(2,1),
	INEG (1,1),
	/*INT2BYTE(0,0),
	INT2CHAR(0,0),
	INT2SHORT(0,0), */
	IOR(2,1),
	IREM (2,1),
	IRETURN(1,0),
	ISHL(2,1),
	ISHR(2,1),
	ISTORE_0 (0,0),
	ISTORE_1 (0,0),
	ISTORE_2 (0,0),
	ISTORE_3 (0,0),
	ISUB (2,1),
	/* IUSHR (0,0), */
	IXOR (2,1),
	
	/*
	L2D 
	L2F 
	L2I 
	LADD 
	LALOAD 
	LAND 
	LASTORE 
	LCMP 
	LCONST_0 
	LCONST_1 
	LDIV 
	LLOAD_0 
	LLOAD_1 
	LLOAD_2 
	LLOAD_3 
	LMUL 
	LNEG 
	LOR 
	LREM 
	LRETURN 
	LSHL 
	LSHR 
	LSTORE_0 
	LSTORE_1 
	LSTORE_2 
	LSTORE_3 
	LSUB 
	LUSHR 
	LXOR 
	MONITORENTER 
	MONITOREXIT */ 
	NOP(0,0), 
	POP(1,0), 
	POP2(2,0), 
	RETURN(0,0), 
	/*SALOAD 
	SASTORE */ 
	SWAP(2,2)	
	;
	
	private BasicInstr(int n, int ss, int sa) {
		this.nimarg = n;
		this.narg = ss;
		this.nres = sa;
		this.opcode = this.name();		
	}
	
	private BasicInstr(int ss, int sa) {
		this.nimarg = 0;
		this.narg = ss;
		this.nres = sa;
		this.opcode = this.name();
	}
	
	/** Associated name of operation. */
	private String opcode;
	
	/** Number of immediate arguments. */
	private int nimarg;
	
	/** Number of stack arguments. */
	private int narg;
	
	/** Operands added to the stack as a result. */
	private int nres;

	public int immediateArgs() {
		return nimarg;
	}

	public int maxStackSize() {
		return Math.max(narg, nres);
	}

	public int stackChange() {
		return nres-narg;
	}

}
