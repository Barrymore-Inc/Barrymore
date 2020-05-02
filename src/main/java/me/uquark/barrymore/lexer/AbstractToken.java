package me.uquark.barrymore.lexer;

public abstract class AbstractToken {
    public final String pWord;
    public AbstractToken(String pWord) {
        this.pWord = pWord;
    }
}
