package me.uquark.barrymore.lexer;

import me.uquark.barrymore.db.DatabaseProvider;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private static ArrayList<Alias> aliases = new ArrayList<Alias>();

    public static int loadAliases() throws SQLException {
        aliases.clear();

        ResultSet resultSet = null;
        Statement statement = null;

        try {
            statement = DatabaseProvider.CONNECTION.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM ALIAS");
            while (resultSet.next()) {
                int ID = resultSet.getInt("ID");
                int kEntity = resultSet.getInt("kEntity");
                String pWord = resultSet.getString("pWord");
                aliases.add(new Alias(ID, kEntity, pWord));
            }

            return aliases.size();
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
        }
    }

    private static List<AbstractToken> findTokens(String word, double threshold) {
        List<AbstractToken> found = new ArrayList<>();
        for (Alias alias : aliases)
            if (alias.check(word) >= threshold)
                found.add(alias);
        if (found.isEmpty())
            found.add(new Parameter(word));
        return found;
    }

    public static List<AbstractToken> tokenize(String sentence) {
        sentence = sentence.toLowerCase();
        sentence = sentence.replaceAll("[.,!?;:]", "");
        String[] words = sentence.split(" ");

        List<AbstractToken> result = new ArrayList<>();

        for (String word : words)
            result.addAll(findTokens(word, 0.7));

        return result;
    }
}
