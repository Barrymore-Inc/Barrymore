package me.uquark.barrymore.lexer;

import me.uquark.barrymore.utils.LongestCommonSubstring;

public class Alias extends AbstractToken {
    public final int ID, kEntity;

    public double check(String w) {
        return LongestCommonSubstring.lcsRating(w, pWord);
    }

    public Alias(int ID, int kEntity, String pWord) {
        super(pWord);
        this.ID = ID;
        this.kEntity = kEntity;
    }
}
