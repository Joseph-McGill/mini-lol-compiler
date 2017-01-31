package parser;

import java.util.Vector;

/** Enum for the rules, terminals, and
 *  non terminals of the language */
public enum Symbol {

    /** stack symbol, id, and constants */
    STACK_SYMBOL(0, -1, -1, "Z0"),
    ID(1, -1, -1, "[id]"),
    CONST(2, -1, -1, "[const]"),

    /** keywords */
    K_AN(3, -1, -1, "AN"),
    K_BOTH_OF(4, -1, -1, "BOTH OF"),
    K_BOTH_SAEM(5, -1, -1, "BOTH SAEM"),
    K_DIFF_OF(6, -1, -1, "DIFF OF"),
    K_DIFFRINT(7, -1, -1, "DIFFRINT"),
    K_EITHER_OF(8, -1, -1, "EITHER OF"),
    K_FAIL(9, -1, -1, "FAIL"),
    K_FOUND_YR(10, -1, -1, "FOUND YR"),
    K_GIMMEH(11, -1, -1, "GIMMEH"),
    K_GTFO(12, -1, -1, "GTFO"),
    K_HAI(13, -1, -1, "HAI"),
    K_HOW_IZ_I(14, -1, -14, "HOW IZ I"),
    K_I_HAS_A(15, -1, -1, "I HAS A"),
    K_I_IZ(16, -1, -1, "I IZ"),
    K_IF_U_SAY_SO(17, -1, -1, "IF U SAY SO"),
    K_IM_IN_YR(18, -1, -1, "IM IN YR"),
    K_IM_OUTTA_YR(19, -1, -1, "IM OUTTA YR"),
    K_ITZ_A(20, -1, -1, "ITZ A"),
    K_KTHXBYE(21, -1, -1, "KTHXBYE"),
    K_MKAY(22, -1, -1, "MKAY"),
    K_NO_WAI(23, -1, -1, "NO WAI"),
    K_NOT(24, -1, -1, "NOT"),
    K_NUMBAR(25, -1, -1, "NUMBAR"),
    K_NUMBR(26, -1, -1, "NUMBR"),
    K_O_RLY(27, -1, -1, "O RLY?"),
    K_OIC(28, -1, -1, "OIC"),
    K_OMG(29, -1, -1, "OMG"),
    K_PRODUKT_OF(30, -1, -1, "PRODUKT OF"),
    K_QUOSHUNT_OF(31, -1, -1, "QUOSHUNT OF"),
    K_R(32, -1, -1, "R"),
    K_SUM_OF(33, -1, -1, "SUM OF"),
    K_TROOF(34, -1, -1, "TROOF"),
    K_VISIBLE(35, -1, -1, "VISIBLE"),
    K_WILE(36, -1, -1, "WILE"),
    K_WIN(37, -1, -1, "WIN"),
    K_WTF(38, -1, -1, "WTF?"),
    K_YA_RLY(39, -1, -1, "YA RLY"),
    K_YR(40, -1, -1, "YR"),

    /** semicolon symbol */
    SEMICOLON(41, -1, -1, ";"),

    /** production rules */
    R_LOL(42, 0, 0, "<lol>"),
    R_BODY(43, 1, 2, "<body>"),
    R_STMT(44, 3, 12, "<stmt>"),
    R_INPUT(45, 13, 13, "<input>"),
    R_OUTPUT(46, 14, 14, "<output>"),
    R_DECL(47, 15, 15, "<decl>"),
    R_TYPE(48, 16, 18, "<type>"),
    R_ASMT(49, 19, 19, "<asmt>"),
    R_LOOP(50, 20, 20, "<loop>"),
    R_IF(51, 21, 21, "<if>"),
    R_CASE(52, 22, 22, "<case>"),
    R_OMGS(53, 23, 24, "<omgs>"),
    R_OMG(54, 25, 25, "<omg>"),
    R_VALUE(55, 26, 28, "<value>"),
    R_RETURN(56, 29, 30, "<return>"),
    R_FUNCTION(57, 31, 31, "<function>"),
    R_ARGS(58, 32, 33, "<args>"),
    R_ARG(59, 34, 34, "<arg>"),
    R_CALL(60, 35, 35, "<call>"),
    R_EXPR(61, 36, 37, "<exp>"),
    R_ARITH(62, 38, 43, "<arith>"),
    R_BOOL(63, 44, 49, "<bool>"),
    R_COMP(64, 50, 51, "<comp>"),

    /** error symbol */
    ERROR(65, -1, -1, "ERROR");

    /** Data for each enum */
    private final int code;
    private final int lower;
    private final int upper;
    private final String sname;

    /* vector of vectors containing the production rules for the grammar */
    public static Vector<Vector<Integer>> productions = new Vector<Vector<Integer>>() {{

        /* <lol> */
        add(createVector(K_HAI, R_BODY, K_KTHXBYE));

        /* <body> */
        add(createVector(R_STMT, SEMICOLON, R_BODY));
        add(createVector());

        /* <stmt> */
        add(createVector(R_INPUT));
        add(createVector(R_OUTPUT));
        add(createVector(R_DECL));
        add(createVector(R_ASMT));
        add(createVector(R_LOOP));
        add(createVector(R_IF));
        add(createVector(R_CASE));
        add(createVector(R_RETURN));
        add(createVector(R_FUNCTION));
        add(createVector(R_CALL));

        /* <input> */
        add(createVector(K_GIMMEH, ID));

        /* <output> */
        add(createVector(K_VISIBLE, R_EXPR));

        /* <decl> */
        add(createVector(K_I_HAS_A, ID, K_ITZ_A, R_TYPE));

        /* <type> */
        add(createVector(K_NUMBR));
        add(createVector(K_NUMBAR));
        add(createVector(K_TROOF));

        /* <asmt> */
        add(createVector(ID, K_R, R_EXPR));

        /* <loop> */
        add(createVector(K_IM_IN_YR, ID, K_WILE, R_BOOL, R_BODY, K_IM_OUTTA_YR, ID));

        /* <if> */
        add(createVector(K_O_RLY, R_BOOL, K_YA_RLY, R_BODY, K_NO_WAI, R_BODY, K_OIC));

        /* <case> */
        add(createVector(K_WTF, R_EXPR, R_OMGS, K_OIC));

        /* <omgs> */
        add(createVector(R_OMG, R_OMGS));
        add(createVector());

        /* <omg> */
        add(createVector(K_OMG, R_VALUE, R_BODY));

        /* <value> */
        add(createVector(CONST));
        add(createVector(K_WIN));
        add(createVector(K_FAIL));

        /* <return> */
        add(createVector(K_FOUND_YR, R_EXPR));
        add(createVector(K_GTFO));

        /* <function> */
        add(createVector(K_HOW_IZ_I, ID, R_ARGS, R_BODY, K_IF_U_SAY_SO));

        /* <args> */
        add(createVector(R_ARG, R_ARGS));
        add(createVector());

        /* <arg> */
        add(createVector(K_YR, ID));

        /* <call> */
        add(createVector(K_I_IZ, ID, R_ARGS, K_MKAY));

        /* <expr> */
        add(createVector(R_ARITH));
        add(createVector(R_BOOL));

        /* <arith> */
        add(createVector(K_SUM_OF, R_ARITH, K_AN, R_ARITH));
        add(createVector(K_DIFF_OF, R_ARITH, K_AN, R_ARITH));
        add(createVector(K_PRODUKT_OF, R_ARITH, K_AN, R_ARITH));
        add(createVector(K_QUOSHUNT_OF, R_ARITH, K_AN, R_ARITH));
        add(createVector(ID));
        add(createVector(CONST));

        /* <bool> */
        add(createVector(K_BOTH_OF, R_BOOL, K_AN, R_BOOL));
        add(createVector(K_EITHER_OF, R_BOOL, K_AN, R_BOOL));
        add(createVector(K_NOT, R_BOOL));
        add(createVector(R_COMP));
        add(createVector(K_WIN));
        add(createVector(K_FAIL));

        /* <comp> */
        add(createVector(K_BOTH_SAEM, R_EXPR, K_AN, R_EXPR));
        add(createVector(K_DIFFRINT, R_EXPR, K_AN, R_EXPR));
    }};

    /* vector of vectors containing the lookahead sets for each production */
    public static Vector<Vector<Integer>> lookaheads = new Vector<Vector<Integer>>() {{
        /* <lol> */
        add(createVector(K_HAI));

        /* <body> */
        add(createVector(K_GIMMEH, K_VISIBLE, K_I_HAS_A, ID, K_IM_IN_YR, K_O_RLY, K_WTF,
                K_FOUND_YR, K_GTFO, K_HOW_IZ_I, K_I_IZ));
        add(createVector(K_KTHXBYE, K_NO_WAI, K_IM_OUTTA_YR, K_OIC, K_OMG, K_IF_U_SAY_SO));

        /* <stmt> */
        add(createVector(K_GIMMEH));
        add(createVector(K_VISIBLE));
        add(createVector(K_I_HAS_A));
        add(createVector(ID));
        add(createVector(K_IM_IN_YR));
        add(createVector(K_O_RLY));
        add(createVector(K_WTF));
        add(createVector(K_FOUND_YR, K_GTFO));
        add(createVector(K_HOW_IZ_I));
        add(createVector(K_I_IZ));

        /* <input> */
        add(createVector(K_GIMMEH));

        /* <output> */
        add(createVector(K_VISIBLE));

        /* <decl> */
        add(createVector(K_I_HAS_A));

        /* <type> */
        add(createVector(K_NUMBR));
        add(createVector(K_NUMBAR));
        add(createVector(K_TROOF));


        /* <asmt> */
        add(createVector(ID));

        /* <loop> */
        add(createVector(K_IM_IN_YR));

        /* <if> */
        add(createVector(K_O_RLY));

        /* <case> */
        add(createVector(K_WTF));

        /* <omgs> */
        add(createVector(K_OMG));
        add(createVector(K_OIC));

        /* <omg> */
        add(createVector(K_OMG));

        /* <value> */
        add(createVector(CONST));
        add(createVector(K_WIN));
        add(createVector(K_FAIL));

        /* <return> */
        add(createVector(K_FOUND_YR));
        add(createVector(K_GTFO));

        /* <function> */
        add(createVector(K_HOW_IZ_I));

        /* <args> */
        add(createVector(K_YR));
        add(createVector(K_IF_U_SAY_SO, K_MKAY, K_GIMMEH, K_VISIBLE, K_I_HAS_A, ID, K_IM_IN_YR, K_O_RLY, K_WTF,
                K_FOUND_YR, K_GTFO, K_HOW_IZ_I, K_I_IZ ));

        /* <arg> */
        add(createVector(K_YR));

        /* <call> */
        add(createVector(K_I_IZ));

        /* <expr> */
        add(createVector(K_SUM_OF, K_DIFF_OF, K_PRODUKT_OF, K_QUOSHUNT_OF, ID, CONST));
        add(createVector(K_BOTH_OF, K_EITHER_OF, K_NOT, K_BOTH_SAEM, K_DIFFRINT, K_WIN, K_FAIL));

        /* <arith> */
        add(createVector(K_SUM_OF));
        add(createVector(K_DIFF_OF));
        add(createVector(K_PRODUKT_OF));
        add(createVector(K_QUOSHUNT_OF));
        add(createVector(ID));
        add(createVector(CONST));


        /* <bool> */
        add(createVector(K_BOTH_OF));
        add(createVector(K_EITHER_OF));
        add(createVector(K_NOT));
        add(createVector(K_BOTH_SAEM, K_DIFFRINT));
        add(createVector(K_WIN));
        add(createVector(K_FAIL));

        /* <comp> */
        add(createVector(K_BOTH_SAEM));
        add(createVector(K_DIFFRINT));

    }};

    /** Enum constructor */
    Symbol(int code, int lower, int upper, String sname) {
        this.code = code;
        this.lower = lower;
        this.upper = upper;
        this.sname = sname;
    }

    /** Return the int code */
    public int getCode() { return code; }

    /** Return the sname */
    public String getSname() { return sname; }

    /** Return the name of the enum given the int code */
    public static  Symbol get(int code) {
        for(Symbol s: Symbol.values()) {
            if (s.code == code) {
                return s;
            }
        }
        return null;
    }

    /** Creates a Vector of integers given a set of symbols */
    private static Vector<Integer> createVector(Symbol ...symbols) {
        Vector<Integer> v = new Vector<Integer>();
        for (Symbol s: symbols) {
            v.add(s.getCode());
        }
        return v;
    }

    /** Checks if the lookahead is in the given lookahead set */
    public static boolean isLookahead(Symbol lookahead, Vector<Integer> symbols) {
        if (symbols.contains(lookahead.code)) return true;
        else return false;
    }

    /** Returns the production rule associated with the stack top and lookahead */
    public static Vector<Integer> getRule(Symbol stackTop, Symbol lookahead) {
        for (int i = stackTop.lower; i <= stackTop.upper; ++i) {
            if (isLookahead(lookahead, lookaheads.elementAt(i))) {
                return productions.elementAt(i);
            }
        }

        /* return null if no rule is found */
        return null;
    }
}

