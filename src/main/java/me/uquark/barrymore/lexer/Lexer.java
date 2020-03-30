package me.uquark.barrymore.lexer;

import me.uquark.barrymore.db.DatabaseProvider;
import me.uquark.barrymore.utils.LongestCommonSubstring;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Lexer {
    private static ArrayList<Alias> aliases = new ArrayList<Alias>();

    public static void loadAliases() throws SQLException {
        aliases.clear();

        Connection connection = null;
        ResultSet resultSet = null;
        Statement statement = null;

        try {
            connection = DatabaseProvider.connect();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM ALIAS");
            while (resultSet.next()) {
                int ID = resultSet.getInt("ID");
                int kEntity = resultSet.getInt("kEntity");
                String pWord = resultSet.getString("pWord");
                aliases.add(new Alias(ID, kEntity, pWord));
            }
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        }
    }

    private static Alias findAlias(String word, double threshold) {
        double maxRating = -1;
        int position = -1;
        for (int i=0; i < aliases.size(); i++) {
            double rating = aliases.get(i).check(word);
            if (rating > maxRating) {
                maxRating = rating;
                position = i;
            }
        }

        if (maxRating >= threshold)
            return aliases.get(position);
        else
            return null;
    }

    public static ArrayList<Alias> tokenize(String sentence) {
        sentence = sentence.toLowerCase();
        sentence = sentence.replaceAll("[.,!?;:]", "");
        String[] words = sentence.split(" ");

        ArrayList<Alias> result = new ArrayList<Alias>();

        for (String word : words) {
            Alias alias = findAlias(word, 0.7);
            if (alias != null)
                result.add(alias);
        }

        return result;
    }
}
