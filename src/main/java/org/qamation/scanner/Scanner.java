package org.qamation.scanner;

public class Scanner implements TokenListener {

    private LineTokenizer tokenizer;
    private Tokens tokens;


    public Scanner() {
        this.tokenizer = new LineTokenizer();
        this.tokenizer.addTokenListener(this);
        this.tokens = new Tokens();

    }

    public Tokens getTokens(String xpathDescription) {
        tokenizer.tokenizeString(xpathDescription);
        return tokens;
    }

    @Override
    public void gotStringValue(String val) {
        tokens.add(val);
    };

    @Override
    public void gotNumberValue(double d) {
        tokens.add(String.valueOf(d));
    }

    @Override
    public void eolReached() {
        tokens.addEOL();

    }

    @Override
    public void eofReached() {
        tokens.addEOF();
    }


}
