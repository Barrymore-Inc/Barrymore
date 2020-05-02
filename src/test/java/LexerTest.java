import me.uquark.barrymore.db.DatabaseProvider;
import me.uquark.barrymore.lexer.AbstractToken;
import me.uquark.barrymore.lexer.Lexer;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

public class LexerTest {
    private void init() throws SQLException {
        DatabaseProvider.CONNECTION = DatabaseProvider.connect();
        Lexer.loadAliases();
    }

    @Test
    public void testTokenization() throws SQLException {
        init();
        List<AbstractToken> result = Lexer.tokenize("Выключи свет на кухне, пожалуйста");
        Assert.assertEquals(result.get(0).pWord, "выключить");
        Assert.assertEquals(result.get(1).pWord, "выключи");
        Assert.assertEquals(result.get(2).pWord, "свет");
        Assert.assertEquals(result.get(3).pWord, "на");
        Assert.assertEquals(result.get(4).pWord, "кухня");
        Assert.assertEquals(result.get(5).pWord, "пожалуйста");
    }
}
