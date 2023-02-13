package org.qamation.scanner;

public interface TokenListener {
    void gotStringValue(String val);
    void gotNumberValue(double d);
    void eolReached();
    void eofReached();
}
