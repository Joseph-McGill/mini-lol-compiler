package parser;

import java.io.*;
import java.util.*;

/**
 * Joseph McGill
 * Spring 2016
 * CS 4324
 *
 * This program is a compiler for the language Mini-LOL. Mini-LOL is a
 * smaller derivative of the mock language LOL with simplified grammar rules.
 * Mini-LOL was created by Dr. Kim, a CS professor at OU for the course
 * CS 4324 Compiler Construction.
 **/
public class MiniLOLCompiler {

    /* array for holding user defined variables */
    public static Token[] SYMTAB;
    public static int SYMTABCount;

    /* vector for holding tokens */
    public static Vector<Token> TOKENS;

    /* file path of the input file */
    public static String FILEPATH = "";

    /** Main */
    public static void main(String args[]) {

        /* get the file path from the command line */
        if (args.length > 0) {
            FILEPATH = args[0];
        }

        /* initialize data structures */
        SYMTAB = new Token[100];
        TOKENS = new Vector<>();
        SYMTABCount = 0;

        /* print the source program */
        System.out.println("\nInput program");
        System.out.println("--------------------------------"
                         + "--------------------------------");

        /* open a new input stream */
        LineNumberReader input = null;
        try {
            input = new LineNumberReader(new FileReader(FILEPATH));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        /* print each line of the input program */
        try {

            String line = input.readLine();
            while (line != null) {
                System.out.println(line);
                line = input.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* close the input stream */
        try {
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* call PARSER to parse the input file */
        PARSER(FILEPATH);

        /* Print the content of SYMTAB */
        System.out.println("\nSYMTAB");
        System.out.println("--------------------------------"
                         + "--------------------------------");

        if (SYMTABCount <= 0) {
            System.out.println("None");
        } else {
            for (int i = 0; i < SYMTABCount; i++) {
                System.out.format("%-6s%-10s\n", SYMTAB[i].getTokenName(),
                                    SYMTAB[i].getType());
            }
        }
    }

    /** Function to parse an input file according to the rules
     *  of the grammar */
    public static boolean PARSER(String filePath) {

         /* Initialize LineNumberReader */
        LineNumberReader stream = null;
        try {
            stream = new LineNumberReader(new FileReader(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        stream.setLineNumber(1);

        /* First lookahead symbol */
        Token lookAHead = SCANNER(stream);

        /* Integer stack */
        Stack<Integer> stack = new Stack<>();

        /* push the stack symbol onto the stack */
        stack.push(Symbol.STACK_SYMBOL.getCode());

        /* counter to keep track of the steps */
        int step = 1;

        /* print the table header */
        System.out.println("\nParse output");
        System.out.format("%-6s%-20s%-20s%-20s\n", "Step", "Stack top", "Lookahead", "Action");
        System.out.println("----------------------------------------------------------------");

        /* print the first step */
        System.out.format("%-6d%-20s%-20s%-20s\n", step, (Symbol.get(stack.lastElement()).getSname() +
                " (" + stack.lastElement() + ")"), "  -", ("Push " + Symbol.R_LOL.getSname() + " (" +
                Symbol.R_LOL.getCode() + ")"));

        /* push <lol> onto the stack*/
        stack.push(Symbol.R_LOL.getCode());
        step++;

        /* parse the file until no more tokens are found */
        while (lookAHead != null) {

            /* if the top of the stack matches the lookahead, consume */
            if (lookAHead.getSymbol().getCode() == stack.lastElement()) {

                System.out.format("%-6d%-20s%-20s%-20s\n", step, (Symbol.get(stack.lastElement()).getSname() +
                        " (" + stack.lastElement() + ")"), (lookAHead.getSymbol().getSname() +
                        " (" + lookAHead.getSymbol().getCode() + ")"), ("Match " +
                        lookAHead.getSymbol().getSname() + " (" + lookAHead.getSymbol().getCode() + ")"));

                /* pop and get next lookahead */
                stack.pop();
                lookAHead = SCANNER(stream);

            } else if (stack.lastElement() >= 42 && stack.lastElement() <= 64 ) {

                /* get the next rule from the lookahead and stack top */
                Vector<Integer> nextRule = Symbol.getRule(Symbol.get(stack.lastElement()), lookAHead.getSymbol());

                System.out.format("%-6d%-20s%-20s%-20s\n", step, (Symbol.get(stack.lastElement()).getSname() +
                        " (" + stack.lastElement() + ")"), (lookAHead.getSymbol().getSname() +
                        " (" + lookAHead.getSymbol().getCode() + ")"), ("Use Rule " +
                        " (" + (Symbol.productions.indexOf(nextRule) + 1) + ")"));

                /* pop the stack top off */
                stack.pop();

                /* push the next rule onto the stack (in reverse)  */
                for (int i = nextRule.size() - 1; i >= 0; i--) {
                    stack.push(nextRule.get(i));
                }
            }

            step++;
        }

        /* return true if there are no more lookahead symbols and nothing on the stack */
        if (lookAHead == null && (stack.lastElement() == Symbol.STACK_SYMBOL.getCode())) {
            System.out.format("%-6d%-20s%-20s%-20s\n", step,(Symbol.get(stack.lastElement()).getSname() +
                    " (" + stack.lastElement() + ")"), "-None-", "ACCEPT" );
            return true;
        } else return false;
    }

    /** Function to scan a source program character by character
     *  and return a valid token */
    public static Token SCANNER(LineNumberReader stream) {

        /* char array to hold read characters */
        char[] token = new char[128];
        int size = 0;
        Token temp;

        /* read a character from the stream */
        token[size] = readChar(stream);

        /* skip leading newline characters and white space */
        while (token[size] == '\n' || token[size] == ' ' || token[size] == '\t' || token[size] == '\r') {
            token[size] = readChar(stream);
        }

        /* find a valid token */
        if (token[size] != '\0') {
            if (token[size] == 'A') {

                /* increment counter and read next character */
                size++;
                token[size] = readChar(stream);

                /* check if read character is next character in a legal token */
                if (token[size] == 'N') {

                    /* accepting state, any token separator causes token to be accepted, anything else fails */
                    size++;
                    token[size] = readChar(stream);
                    if (Character.isWhitespace(token[size]) || token[size] == ';'
                            || token[size] == '\0') {

                        /* accept token if token separator is found */
                        return ACCEPT(token, Symbol.K_AN, size, "Keyword", stream);
                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
            } else if (token[size] == 'B') {
                size++;
                token[size] = readChar(stream);
                if (token[size] == 'O') {
                    size++;
                    token[size] = readChar(stream);
                    if (token[size] == 'T') {
                        size++;
                        token[size] = readChar(stream);
                        if (token[size] == 'H') {
                            size++;
                            token[size] = readChar(stream);
                            if (token[size] == ' ') {
                                size++;
                                token[size] = readChar(stream);
                                if (token[size] == 'O') {
                                    //BOTH OF
                                    size++;
                                    token[size] = readChar(stream);
                                    if (token[size] == 'F') {
                                        size++;
                                        token[size] = readChar(stream);
                                        if (Character.isWhitespace(token[size]) || token[size] == ';'
                                                || token[size] == '\0') {
                                            return ACCEPT(token, Symbol.K_BOTH_OF, size, "Keyword", stream);
                                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                } else if (token[size] == 'S') {
                                    //BOTH SAEM
                                    size++;
                                    token[size] = readChar(stream);
                                    if (token[size] == 'A') {
                                        size++;
                                        token[size] = readChar(stream);
                                        if (token[size] == 'E') {
                                            size++;
                                            token[size] = readChar(stream);
                                            if (token[size] == 'M') {
                                                size++;
                                                token[size] = readChar(stream);
                                                if (Character.isWhitespace(token[size]) || token[size] == ';'
                                                        || token[size] == '\0') {
                                                    return ACCEPT(token, Symbol.K_BOTH_SAEM, size, "Keyword", stream);
                                                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                            } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                            } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                } else if (token[size] == 'T') {
                    //BTW
                    size++;
                    token[size] = readChar(stream);
                    if (token[size] == 'W') {
                        /* consume and ignore until \n, then call SCANNER again and return the token */
                        do {
                            size++;
                            token[size] = readChar(stream);
                        } while (token[size] != '\n' && token[size] != '\r');

                        /* return next token, ignoring comment */
                        return SCANNER(stream);
                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
            } else if (token[size] == 'D') {
                size++;
                token[size] = readChar(stream);
                if (token[size] == 'I') {
                    size++;
                    token[size] = readChar(stream);
                    if (token[size] == 'F') {
                        size++;
                        token[size] = readChar(stream);
                        if (token[size] == 'F') {
                            size++;
                            token[size] = readChar(stream);
                            if (token[size] == 'R') {
                                //DIFFRINT
                                size++;
                                token[size] = readChar(stream);
                                if (token[size] == 'I') {
                                    size++;
                                    token[size] = readChar(stream);
                                    if (token[size] == 'N') {
                                        size++;
                                        token[size] = readChar(stream);
                                        if (token[size] == 'T') {
                                            size++;
                                            token[size] = readChar(stream);
                                            if (Character.isWhitespace(token[size]) || token[size] == ';'
                                                    || token[size] == '\0') {
                                                return ACCEPT(token, Symbol.K_DIFFRINT, size, "Keyword", stream);
                                            } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                            } else if (token[size] == ' ') {
                                //DIFF OF
                                size++;
                                token[size] = readChar(stream);
                                if (token[size] == 'O') {
                                    size++;
                                    token[size] = readChar(stream);
                                    if (token[size] == 'F') {
                                        size++;
                                        token[size] = readChar(stream);
                                        if (Character.isWhitespace(token[size]) || token[size] == ';'
                                                || token[size] == '\0') {
                                            return ACCEPT(token,Symbol.K_DIFF_OF, size, "Keyword", stream);
                                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                            } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
            } else if (token[size] == 'E') {
                //EITHER OF
                size++;
                token[size] = readChar(stream);
                if (token[size] == 'I') {
                    size++;
                    token[size] = readChar(stream);
                    if (token[size] == 'T') {
                        size++;
                        token[size] = readChar(stream);
                        if (token[size] == 'H') {
                            size++;
                            token[size] = readChar(stream);
                            if (token[size] == 'E') {
                                size++;
                                token[size] = readChar(stream);
                                if (token[size] == 'R') {
                                    size++;
                                    token[size] = readChar(stream);
                                    if (token[size] == ' ') {
                                        size++;
                                        token[size] = readChar(stream);
                                        if (token[size] == 'O') {
                                            size++;
                                            token[size] = readChar(stream);
                                            if (token[size] == 'F') {
                                                size++;
                                                token[size] = readChar(stream);
                                                if (Character.isWhitespace(token[size]) || token[size] == ';'
                                                        || token[size] == '\0') {
                                                    return ACCEPT(token, Symbol.K_EITHER_OF, size, "Keyword", stream);
                                                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                            } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                            } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
            } else if (token[size] == 'F') {
                //FAIL
                size++;
                token[size] = readChar(stream);
                if (token[size] == 'A') {
                    size++;
                    token[size] = readChar(stream);
                    if (token[size] == 'I') {
                        size++;
                        token[size] = readChar(stream);
                        if (token[size] == 'L') {
                            size++;
                            token[size] = readChar(stream);
                            if (Character.isWhitespace(token[size]) || token[size] == ';'
                                    || token[size] == '\0') {
                                return ACCEPT(token, Symbol.K_FAIL,size, "Keyword", stream);
                            } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                } else if (token[size] == 'O') {
                    //FOUND YR
                    size++;
                    token[size] = readChar(stream);
                    if (token[size] == 'U') {
                        size++;
                        token[size] = readChar(stream);
                        if (token[size] == 'N') {
                            size++;
                            token[size] = readChar(stream);
                            if (token[size] == 'D') {
                                size++;
                                token[size] = readChar(stream);
                                if (token[size] == ' ') {
                                    size++;
                                    token[size] = readChar(stream);
                                    if (token[size] == 'Y') {
                                        size++;
                                        token[size] = readChar(stream);
                                        if (token[size] == 'R') {
                                            size++;
                                            token[size] = readChar(stream);
                                            if (Character.isWhitespace(token[size]) || token[size] == ';') {
                                                return ACCEPT(token, Symbol.K_FOUND_YR, size, "Keyword", stream);
                                            } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                            } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
            } else if (token[size] == 'G') {
                //GIMMEH
                //GTFO
                size++;
                token[size] = readChar(stream);
                if (token[size] == 'I') {
                    size++;
                    token[size] = readChar(stream);
                    if (token[size] == 'M') {
                        size++;
                        token[size] = readChar(stream);
                        if (token[size] == 'M') {
                            size++;
                            token[size] = readChar(stream);
                            if (token[size] == 'E') {
                                size++;
                                token[size] = readChar(stream);
                                if (token[size] == 'H') {
                                    size++;
                                    token[size] = readChar(stream);
                                    if (Character.isWhitespace(token[size]) || token[size] == ';') {
                                        return ACCEPT(token,Symbol.K_GIMMEH, size, "Keyword", stream);
                                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                            } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                } else if (token[size] == 'T') {
                    size++;
                    token[size] = readChar(stream);
                    if (token[size] == 'F') {
                        size++;
                        token[size] = readChar(stream);
                        if (token[size] == 'O') {
                            size++;
                            token[size] = readChar(stream);
                            if (Character.isWhitespace(token[size]) || token[size] == ';'
                                    || token[size] == '\0') {
                                return ACCEPT(token,Symbol.K_GTFO, size, "Keyword", stream);
                            } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
            } else if (token[size] == 'H') {
                size++;
                token[size] = readChar(stream);
                if (token[size] == 'A') {
                    //HAI
                    size++;
                    token[size] = readChar(stream);
                    if (token[size] == 'I') {
                        size++;
                        token[size] = readChar(stream);
                        if (Character.isWhitespace(token[size]) || token[size] == ';'
                                || token[size] == '\0') {
                            return ACCEPT(token,Symbol.K_HAI, size, "Keyword", stream);
                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                } else if (token[size] == 'O') {
                    //HOW IZ I
                    size++;
                    token[size] = readChar(stream);
                    if (token[size] == 'W') {
                        size++;
                        token[size] = readChar(stream);
                        if (token[size] == ' ') {
                            size++;
                            token[size] = readChar(stream);
                            if (token[size] == 'I') {
                                size++;
                                token[size] = readChar(stream);
                                if (token[size] == 'Z') {
                                    size++;
                                    token[size] = readChar(stream);
                                    if (token[size] == ' ') {
                                        size++;
                                        token[size] = readChar(stream);
                                        if (token[size] == 'I') {
                                            size++;
                                            token[size] = readChar(stream);
                                            if (Character.isWhitespace(token[size]) || token[size] == ';'
                                                    || token[size] == '\0') {
                                                return ACCEPT(token, Symbol.K_HOW_IZ_I, size, "Keyword", stream);
                                            } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                            } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
            } else if (token[size] == 'I') {
                size++;
                token[size] = readChar(stream);
                if (token[size] == ' ') {
                    size++;
                    token[size] = readChar(stream);
                    if (token[size] == 'H') {
                        //I HAS A
                        size++;
                        token[size] = readChar(stream);
                        if (token[size] == 'A') {
                            size++;
                            token[size] = readChar(stream);
                            if (token[size] == 'S') {
                                size++;
                                token[size] = readChar(stream);
                                if (token[size] == ' ') {
                                    size++;
                                    token[size] = readChar(stream);
                                    if (token[size] == 'A') {
                                        size++;
                                        token[size] = readChar(stream);
                                        if (Character.isWhitespace(token[size]) || token[size] == ';'
                                                || token[size] == '\0') {
                                            return ACCEPT(token,Symbol.K_I_HAS_A, size, "Keyword", stream);
                                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                            } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                    } else if (token[size] == 'I') {
                        //I IZ
                        size++;
                        token[size] = readChar(stream);
                        if (token[size] == 'Z') {
                            size++;
                            token[size] = readChar(stream);
                            if (Character.isWhitespace(token[size]) || token[size] == ';'
                                    || token[size] == '\0') {
                                return ACCEPT(token,Symbol.K_I_IZ, size, "Keyword", stream);
                            } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                } else if (token[size] == 'F') {
                    //IF U SAY SO
                    size++;
                    token[size] = readChar(stream);
                    if (token[size] == ' ') {
                        size++;
                        token[size] = readChar(stream);
                        if (token[size] == 'U') {
                            size++;
                            token[size] = readChar(stream);
                            if (token[size] == ' ') {
                                size++;
                                token[size] = readChar(stream);
                                if (token[size] == 'S') {
                                    size++;
                                    token[size] = readChar(stream);
                                    if (token[size] == 'A') {
                                        size++;
                                        token[size] = readChar(stream);
                                        if (token[size] == 'Y') {
                                            size++;
                                            token[size] = readChar(stream);
                                            if (token[size] == ' ') {
                                                size++;
                                                token[size] = readChar(stream);
                                                if (token[size] == 'S') {
                                                    size++;
                                                    token[size] = readChar(stream);
                                                    if (token[size] == 'O') {
                                                        size++;
                                                        token[size] = readChar(stream);
                                                        if (Character.isWhitespace(token[size]) || token[size] == ';'
                                                                || token[size] == '\0') {
                                                            return ACCEPT(token, Symbol.K_IF_U_SAY_SO, size, "Keyword", stream);
                                                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                            } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                            } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                } else if (token[size] == 'M') {
                    //IM IN YR
                    size++;
                    token[size] = readChar(stream);
                    if (token[size] == ' ') {
                        size++;
                        token[size] = readChar(stream);
                        if (token[size] == 'I') {
                            size++;
                            token[size] = readChar(stream);
                            if (token[size] == 'N') {
                                size++;
                                token[size] = readChar(stream);
                                if (token[size] == ' ') {
                                    size++;
                                    token[size] = readChar(stream);
                                    if (token[size] == 'Y') {
                                        size++;
                                        token[size] = readChar(stream);
                                        if (token[size] == 'R') {
                                            size++;
                                            token[size] = readChar(stream);
                                            if (Character.isWhitespace(token[size]) || token[size] == ';'
                                                    || token[size] == '\0') {
                                                return ACCEPT(token, Symbol.K_IM_IN_YR, size, "Keyword", stream);
                                            } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                            } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                        } else if (token[size] == 'O') {
                            //IM OUTTA YR
                            size++;
                            token[size] = readChar(stream);
                            if (token[size] == 'U') {
                                size++;
                                token[size] = readChar(stream);
                                if (token[size] == 'T') {
                                    size++;
                                    token[size] = readChar(stream);
                                    if (token[size] == 'T') {
                                        size++;
                                        token[size] = readChar(stream);
                                        if (token[size] == 'A') {
                                            size++;
                                            token[size] = readChar(stream);
                                            if (token[size] == ' ') {
                                                size++;
                                                token[size] = readChar(stream);
                                                if (token[size] == 'Y') {
                                                    size++;
                                                    token[size] = readChar(stream);
                                                    if (token[size] == 'R') {
                                                        size++;
                                                        token[size] = readChar(stream);
                                                        if (Character.isWhitespace(token[size]) || token[size] == ';'
                                                                || token[size] == '\0') {
                                                            return ACCEPT(token, Symbol.K_IM_OUTTA_YR, size, "Keyword", stream);
                                                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                            } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                            } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                } else if (token[size] == 'T') {
                    //ITZ A
                    size++;
                    token[size] = readChar(stream);
                    if (token[size] == 'Z') {
                        size++;
                        token[size] = readChar(stream);
                        if (token[size] == ' ') {
                            size++;
                            token[size] = readChar(stream);
                            if (token[size] == 'A') {
                                size++;
                                token[size] = readChar(stream);
                                if (Character.isWhitespace(token[size]) || token[size] == ';'
                                        || token[size] == '\0') {
                                    return ACCEPT(token, Symbol.K_ITZ_A, size, "Keyword", stream);
                                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                            } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
            } else if (token[size] == 'K') {
                //KTHXBYE
                size++;
                token[size] = readChar(stream);
                if (token[size] == 'T') {
                    size++;
                    token[size] = readChar(stream);
                    if (token[size] == 'H') {
                        size++;
                        token[size] = readChar(stream);
                        if (token[size] == 'X') {
                            size++;
                            token[size] = readChar(stream);
                            if (token[size] == 'B') {
                                size++;
                                token[size] = readChar(stream);
                                if (token[size] == 'Y') {
                                    size++;
                                    token[size] = readChar(stream);
                                    if (token[size] == 'E') {
                                        size++;
                                        token[size] = readChar(stream);
                                        if (Character.isWhitespace(token[size]) || token[size] == ';'
                                                || token[size] == '\0') {
                                            return ACCEPT(token, Symbol.K_KTHXBYE, size, "Keyword", stream);
                                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                            } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
            } else if (token[size] == 'M') {
                //MKAY
                size++;
                token[size] = readChar(stream);
                if (token[size] == 'K') {
                    size++;
                    token[size] = readChar(stream);
                    if (token[size] == 'A') {
                        size++;
                        token[size] = readChar(stream);
                        if (token[size] == 'Y') {
                            size++;
                            token[size] = readChar(stream);
                            if (Character.isWhitespace(token[size]) || token[size] == ';'
                                    || token[size] == '\0') {
                                return ACCEPT(token, Symbol.K_MKAY, size, "Keyword", stream);
                            } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
            } else if (token[size] == 'N') {
                size++;
                token[size] = readChar(stream);
                if (token[size] == 'O') {
                    size++;
                    token[size] = readChar(stream);
                    if (token[size] == 'T') {
                        //NOT
                        size++;
                        token[size] = readChar(stream);
                        if (Character.isWhitespace(token[size]) || token[size] == ';'
                                || token[size] == '\0') {
                            return ACCEPT(token, Symbol.K_NOT, size, "Keyword", stream);
                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                    } else if (token[size] == ' ') {
                        //NO WAI
                        size++;
                        token[size] = readChar(stream);
                        if (token[size] == 'W') {
                            size++;
                            token[size] = readChar(stream);
                            if (token[size] == 'A') {
                                size++;
                                token[size] = readChar(stream);
                                if (token[size] == 'I') {
                                    size++;
                                    token[size] = readChar(stream);
                                    if (Character.isWhitespace(token[size]) || token[size] == ';'
                                            || token[size] == '\0') {
                                        return ACCEPT(token,Symbol.K_NO_WAI, size, "Keyword", stream);
                                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                            } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                } else if (token[size] == 'U') {
                    size++;
                    token[size] = readChar(stream);
                    if (token[size] == 'M') {
                        size++;
                        token[size] = readChar(stream);
                        if (token[size] == 'B') {
                            size++;
                            token[size] = readChar(stream);
                            if (token[size] == 'A') {
                                size++;
                                token[size] = readChar(stream);
                                if (token[size] == 'R') {
                                    //NUMBAR
                                    size++;
                                    token[size] = readChar(stream);
                                    if (Character.isWhitespace(token[size]) || token[size] == ';'
                                            || token[size] == '\0') {
                                        return ACCEPT(token, Symbol.K_NUMBAR, size, "Keyword", stream);
                                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                            } else if (token[size] == 'R') {
                                //NUMBR
                                size++;
                                token[size] = readChar(stream);
                                if (Character.isWhitespace(token[size]) || token[size] == ';'
                                        || token[size] == '\0') {
                                    return ACCEPT(token, Symbol.K_NUMBR, size, "Keyword", stream);
                                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                            } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
            } else if (token[size] == 'O') {
                size++;
                token[size] = readChar(stream);
                if (token[size] == ' ') {
                    //O RLY?
                    size++;
                    token[size] = readChar(stream);
                    if (token[size] == 'R') {
                        size++;
                        token[size] = readChar(stream);
                        if (token[size] == 'L') {
                            size++;
                            token[size] = readChar(stream);
                            if (token[size] == 'Y') {
                                size++;
                                token[size] = readChar(stream);
                                if (token[size] == '?') {
                                    size++;
                                    token[size] = readChar(stream);
                                    if (Character.isWhitespace(token[size]) || token[size] == ';'
                                            || token[size] == '\0') {
                                        return ACCEPT(token, Symbol.K_O_RLY, size, "Keyword", stream);
                                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                            } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                } else  if (token[size] == 'I') {
                    //OIC
                    size++;
                    token[size] = readChar(stream);
                    if (token[size] == 'C') {
                        size++;
                        token[size] = readChar(stream);
                        if (Character.isWhitespace(token[size]) || token[size] == ';'
                                || token[size] == '\0') {
                            return ACCEPT(token, Symbol.K_OIC, size, "Keyword", stream);
                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                } else if (token[size] == 'M') {
                    //OMG
                    size++;
                    token[size] = readChar(stream);
                    if (token[size] == 'G') {
                        size++;
                        token[size] = readChar(stream);
                        if (Character.isWhitespace(token[size]) || token[size] == ';'
                                || token[size] == '\0') {
                            return ACCEPT(token, Symbol.K_OMG, size, "Keyword", stream);
                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
            } else if (token[size] == 'P') {
                //PRODUKT OF
                size++;
                token[size] = readChar(stream);
                if (token[size] == 'R') {
                    size++;
                    token[size] = readChar(stream);
                    if (token[size] == 'O') {
                        size++;
                        token[size] = readChar(stream);
                        if (token[size] == 'D') {
                            size++;
                            token[size] = readChar(stream);
                            if (token[size] == 'U') {
                                size++;
                                token[size] = readChar(stream);
                                if (token[size] == 'K') {
                                    size++;
                                    token[size] = readChar(stream);
                                    if (token[size] == 'T') {
                                        size++;
                                        token[size] = readChar(stream);
                                        if (token[size] == ' ') {
                                            size++;
                                            token[size] = readChar(stream);
                                            if (token[size] == 'O') {
                                                size++;
                                                token[size] = readChar(stream);
                                                if (token[size] == 'F') {
                                                    size++;
                                                    token[size] = readChar(stream);
                                                    if (Character.isWhitespace(token[size])
                                                            || token[size] == ';' || token[size] == '\0') {
                                                        return ACCEPT(token, Symbol.K_PRODUKT_OF, size, "Keyword", stream);
                                                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                            } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                            } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
            } else if (token[size] == 'Q') {
                //QUOSHUNT OF
                size++;
                token[size] = readChar(stream);
                if (token[size] == 'U') {
                    size++;
                    token[size] = readChar(stream);
                    if (token[size] == 'O') {
                        size++;
                        token[size] = readChar(stream);
                        if (token[size] == 'S') {
                            size++;
                            token[size] = readChar(stream);
                            if (token[size] == 'H') {
                                size++;
                                token[size] = readChar(stream);
                                if (token[size] == 'U') {
                                    size++;
                                    token[size] = readChar(stream);
                                    if (token[size] == 'N') {
                                        size++;
                                        token[size] = readChar(stream);
                                        if (token[size] == 'T') {
                                            size++;
                                            token[size] = readChar(stream);
                                            if (token[size] == ' ') {
                                                size++;
                                                token[size] = readChar(stream);
                                                if (token[size] == 'O') {
                                                    size++;
                                                    token[size] = readChar(stream);
                                                    if (token[size] == 'F') {
                                                        size++;
                                                        token[size] = readChar(stream);
                                                        if (Character.isWhitespace(token[size]) || token[size] == ';'
                                                                || token[size] == '\0') {
                                                            return ACCEPT(token, Symbol.K_QUOSHUNT_OF, size, "Keyword", stream);
                                                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                            } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                            } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
            } else if (token[size] == 'R') {
                //R
                size++;
                token[size] = readChar(stream);
                if (Character.isWhitespace(token[size]) || token[size] == ';'
                        || token[size] == '\0') {
                    return ACCEPT(token, Symbol.K_R, size, "Keyword", stream);
                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
            } else if (token[size] == 'S') {
                //SUM OF
                size++;
                token[size] = readChar(stream);
                if (token[size] == 'U') {
                    size++;
                    token[size] = readChar(stream);
                    if (token[size] == 'M') {
                        size++;
                        token[size] = readChar(stream);
                        if (token[size] == ' ') {
                            size++;
                            token[size] = readChar(stream);
                            if (token[size] == 'O') {
                                size++;
                                token[size] = readChar(stream);
                                if (token[size] == 'F') {
                                    size++;
                                    token[size] = readChar(stream);
                                    if (Character.isWhitespace(token[size]) || token[size] == ';'
                                            || token[size] == '\0') {
                                        return ACCEPT(token, Symbol.K_SUM_OF, size, "Keyword", stream);
                                    } else {
                                        return ERRORHANDLER(REJECT(token, size, stream), 0);
                                    }
                                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                            } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
            } else if (token[size] == 'T') {
                //TROOF
                size++;
                token[size] = readChar(stream);
                if (token[size] == 'R') {
                    size++;
                    token[size] = readChar(stream);
                    if (token[size] == 'O') {
                        size++;
                        token[size] = readChar(stream);
                        if (token[size] == 'O') {
                            size++;
                            token[size] = readChar(stream);
                            if (token[size] == 'F') {
                                size++;
                                token[size] = readChar(stream);
                                if (Character.isWhitespace(token[size]) || token[size] == ';'
                                        || token[size] == '\0') {
                                    return ACCEPT(token, Symbol.K_TROOF, size, "Keyword", stream);
                                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                            } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
            } else if (token[size] == 'V') {
                //VISIBLE
                size++;
                token[size] = readChar(stream);
                if (token[size] == 'I') {
                    size++;
                    token[size] = readChar(stream);
                    if (token[size] == 'S') {
                        size++;
                        token[size] = readChar(stream);
                        if (token[size] == 'I') {
                            size++;
                            token[size] = readChar(stream);
                            if (token[size] == 'B') {
                                size++;
                                token[size] = readChar(stream);
                                if (token[size] == 'L') {
                                    size++;
                                    token[size] = readChar(stream);
                                    if (token[size] == 'E') {
                                        size++;
                                        token[size] = readChar(stream);
                                        if (Character.isWhitespace(token[size]) || token[size] == ';'
                                                || token[size] == '\0') {
                                            return ACCEPT(token, Symbol.K_VISIBLE, size, "Keyword", stream);
                                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                            } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
            } else if (token[size] == 'W') {
                size++;
                token[size] = readChar(stream);
                if (token[size] == 'I') {
                    size++;
                    token[size] = readChar(stream);
                    if (token[size] == 'L') {
                        //WILE
                        size++;
                        token[size] = readChar(stream);
                        if (token[size] == 'E') {
                            size++;
                            token[size] = readChar(stream);
                            if (Character.isWhitespace(token[size]) || token[size] == ';'
                                    || token[size] == '\0') {
                                return ACCEPT(token, Symbol.K_WILE, size, "Keyword", stream);
                            } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                    } else if (token[size] == 'N') {
                        //WIN
                        size++;
                        token[size] = readChar(stream);
                        if (Character.isWhitespace(token[size]) || token[size] == ';'
                                || token[size] == '\0') {
                            return ACCEPT(token, Symbol.K_WIN, size, "Keyword", stream);
                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                } else if (token[size] == 'T') {
                    //WTF?
                    size++;
                    token[size] = readChar(stream);
                    if (token[size] == 'F') {
                        size++;
                        token[size] = readChar(stream);
                        if (token[size] == '?') {
                            size++;
                            token[size] = readChar(stream);
                            if (Character.isWhitespace(token[size]) || token[size] == ';'
                                    || token[size] == '\0') {
                                return ACCEPT(token, Symbol.K_WTF, size, "Keyword", stream);
                            } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
            } else if (token[size] == 'Y') {
                //YA RLY
                size++;
                token[size] = readChar(stream);
                if (token[size] ==  'A') {
                    size++;
                    token[size] = readChar(stream);
                    if (token[size] == ' ') {
                        size++;
                        token[size] = readChar(stream);
                        if(token[size] == 'R') {
                            size++;
                            token[size] = readChar(stream);
                            if(token[size] == 'L') {
                                size++;
                                token[size] = readChar(stream);
                                if (token[size] == 'Y') {
                                    //YA RLY
                                    size++;
                                    token[size] = readChar(stream);
                                    if (Character.isWhitespace(token[size]) || token[size] == ';'
                                            || token[size] == '\0') {
                                        return ACCEPT(token, Symbol.K_YA_RLY, size, "Keyword", stream);
                                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                            } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                        } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                } else if (token[size] == 'R') {
                    //YR
                    size++;
                    token[size] = readChar(stream);
                    if (Character.isWhitespace(token[size]) || token[size] == ';'
                            || token[size] == '\0') {
                        return ACCEPT(token, Symbol.K_YR, size, "Keyword", stream);
                    } else return ERRORHANDLER(REJECT(token, size, stream), 0);
                } else return ERRORHANDLER(REJECT(token, size, stream), 0);
            } else if (token[size] == ';') {
                return new Token(";", Symbol.SEMICOLON, "Special Symbol", stream.getLineNumber());
            } else if (Character.isLowerCase(token[size])) {
                boolean reject = false;

                /* scan to the end of the token checking if the token is legal */
                while (!Character.isWhitespace(token[size]) && token[size] != ';' && token[size] != '\0') {
                    size++;
                    token[size] = readChar(stream);
                    if (!Character.isWhitespace(token[size]) && token[size] != ';' && token[size] != '\0') {
                        if (!Character.isLowerCase(token[size]) && !Character.isDigit(token[size])
                                && token[size] != '_') {
                            reject = true;
                        }
                    }
                }

                /* accept if token is legal, reject if illegal */
                if (!reject) {
                    temp = ACCEPT(token, Symbol.ID, size, "Identifier", stream);
                    BOOKKEEPER(temp); //add token to the bookkeeper
                    return temp;
                } else {
                    return ERRORHANDLER(REJECT(token, size, stream), 3);
                }
            } else if (Character.isDigit(token[size]) || token[size] == '.') {
                int decimal = 0;
                boolean letters = false;
                if (token[size] == '.') {
                    decimal++;
                }

                 /* scan to the end of the token checking if the token is legal */
                while (!Character.isWhitespace(token[size]) && token[size] != ';' && token[size] != '\0') {
                    size++;
                    token[size] = readChar(stream);
                    if (!Character.isWhitespace(token[size]) && token[size] != ';' && token[size] != '\0') {
                        if (token[size] == '.') {
                            decimal++; //keep track of number of decimal points
                        } else if (!Character.isDigit(token[size])) {
                            letters = true; //if the constant contains non digits
                        }
                    }
                }

                 /* accept if token is legal, reject if illegal */
                if (decimal <= 1) {
                    if (!letters) {
                        temp = ACCEPT(token, Symbol.CONST, size, "Constant", stream);
                        BOOKKEEPER(temp); //add token to the bookkeeper
                        return temp;
                    } else {
                        return ERRORHANDLER(REJECT(token, size, stream), 1);
                    }
                } else {
                    return ERRORHANDLER(REJECT(token, size, stream), 2);
                }
            } else {
                /* reject if the beginning of a keyword,  identifier
                constant, or special character is not found*/
                return ERRORHANDLER(REJECT(token, size, stream), 4);
            }
        }

        /* return null if no character is read (EOF) */
        return null;
    }

    /** Method to keep track of user defined variables */
    public static boolean BOOKKEEPER(Token t) {

        /* return false if token isn't a constant or identifier */
        if (!t.getType().equals("Constant") && !t.getType().equals("Identifier")) return false;

        /* check if token is already in SYMTAB */
        for (int i = 0; i < SYMTABCount; i++) {
            if(SYMTAB[i].getTokenName().equals(t.getTokenName())) return true;
        }

        /* add token to SYMTAB */
        SYMTAB[SYMTABCount] = t;
        SYMTABCount++;
        return true;
    }

    /** Method to handle errors thrown by scanner */
    public static Token ERRORHANDLER(Token t, int code) {

        /* set the appropriate error message for the code recieved */
        if (code == 0) {
            t.setErrorMessage("Not a valid keyword");
        } else if (code == 1) {
            t.setErrorMessage("Constants may not contain letters or special characters");
        } else if (code == 2)  {
            t.setErrorMessage("Constants may not contain multiple decimal points");
        } else if (code == 3) {
            t.setErrorMessage("Identifiers may not contain anything other" +
                    " than lowercase letters, numbers, or underscores");
        } else if (code == 4) {
            t.setErrorMessage("Not a valid keyword, constant, or identifier");
        } else {
            t.setErrorMessage("Illegal token");
        }

        /* output error message */
        System.out.println("Error at line " + t.getLineNumber() + ": \""
                + t.getTokenName() + "\": ERROR " + t.getErrorMessage());
        return t;
    }

    /** Method to read 1 character from an input stream */
    public static char readChar(LineNumberReader stream) {

        try {
            stream.mark(10);
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* char array of size 1 */
        int c = 0;

        /* read one character from the stream */
        try {
            c = stream.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* return \0 if no character is read */
        if (c == -1) return '\0';

        /* return the character */
        return (char) c;
    }

    /** Method to reset the stream to the marked position */
    public static void reset(LineNumberReader stream) {
        /* short method to save space */
        try {
            stream.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Method to accept a token */
    public static Token ACCEPT(char[] token, Symbol symbol, int size, String type, LineNumberReader stream) {

        /* check for token separator and return appropriate token */
        if (token[size] == '\n' || token[size] == '\r') {
            return new Token(String.valueOf(token).trim(), symbol, type, stream.getLineNumber() - 1);
        } else if (token[size] == ';') {
            reset(stream);
            token[size] = '\0';
            return new Token(String.valueOf(token).trim(), symbol, type, stream.getLineNumber());
        } else return new Token(String.valueOf(token).trim(), symbol, type, stream.getLineNumber());
    }

    /** Method to reject a token */
    public static Token REJECT(char[] token, int size, LineNumberReader stream) {

        /* Consume until a token separator or EOF is found */
        while (!Character.isWhitespace(token[size]) && token[size] != ';' && token[size] != '\0') {
            size++;
            token[size] = readChar(stream);
        }

        /* return if a token separator is found */
        if (token[size] == '\n' || token[size] == '\r') {
            return new Token(String.valueOf(token).trim(), Symbol.ERROR, "Illegal", stream.getLineNumber() - 1);
        } else if (token[size] == ';') {
            reset(stream);
            token[size] = '\0';
            return new Token(String.valueOf(token).trim(), Symbol.ERROR, "Illegal", stream.getLineNumber());
        } else return new Token(String.valueOf(token).trim(), Symbol.ERROR, "Illegal", stream.getLineNumber());
    }
}
