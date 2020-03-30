package me.uquark.barrymore.lexer;

import me.uquark.barrymore.utils.LongestCommonSubstring;

public class Alias {
    public int ID, kEntity;
    public String pWord;

    public double check(String w) {
        return LongestCommonSubstring.lcsRating(w, pWord);
    }

    public Alias(int ID, int kEntity, String pWord) {
        this.ID = ID;
        this.kEntity = kEntity;
        this.pWord = pWord;
    }
}
