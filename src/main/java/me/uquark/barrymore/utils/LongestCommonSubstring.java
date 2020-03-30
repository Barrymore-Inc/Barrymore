package me.uquark.barrymore.utils;

public class LongestCommonSubstring {
    public static String lcs(String a, String b) {
        if (a.length() > b.length())
            return lcs(b, a);

        String res = "";
        for (int ai = 0; ai < a.length(); ai++) {
            for (int len = a.length() - ai; len > 0; len--) {

                for (int bi = 0; bi < b.length() - len; bi++) {

                    if (a.regionMatches(ai, b, bi, len) && len > res.length()) {
                        res = a.substring(ai, ai + len);
                    }
                }
            }
        }
        return res;
    }

    public static double lcsRating(String a, String b) {
        return lcs(a, b).length() * 2.0 / (a.length() + b.length());
    }
}
