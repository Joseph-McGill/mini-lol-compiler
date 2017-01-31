package parser;

/* Class representing a valid token and its information */
public class Token {

    /* label of token */
    private String tokenName;

    /* symbol of the token */
   private Symbol symbol;

    /* type of token */
    private String type;

    /* line number token occurs on */
    private int lineNumber;

    /* error message of invalid token */
    private String errorMessage;

    /** Constructor */
    public Token(String tokenName, Symbol symbol, String type, int lineNumber) {
        this.tokenName = tokenName;
        this.symbol = symbol;
        this.type = type;
        this.lineNumber = lineNumber;
        this.errorMessage = "";
    }

    /** Default Constructor */
    public Token() {
        this.tokenName = "Illegal";
        this.type = "Illegal";
        this.lineNumber = 0;
        this.errorMessage = "Token is empty";
    }

    /** Returns token name */
    public String getTokenName() {return tokenName;}

    /** Returns the symbol for the token */
    public Symbol getSymbol() {return symbol;}

    /** Returns type of token */
    public String getType() {return type;}

    /** Returns line number of token */
    public int getLineNumber() {return lineNumber;}

    /** Returns the errorMessage of token */
    public String getErrorMessage() {return errorMessage;}

    /** Sets the error message of the token */
    public void setErrorMessage(String msg) {this.errorMessage = msg;}

    /** Method to print the token */
    public void print() {
        if (type.equals("Illegal")) {
            System.out.println("Token: " + tokenName + "\tType: " + type
                    + "\tLine: " + lineNumber + "\t\tError: " + errorMessage);
        } else {
            System.out.println("Token: " + tokenName + "\tType: " + type
                             + "\tLine: " + lineNumber);
        }
    }
}
