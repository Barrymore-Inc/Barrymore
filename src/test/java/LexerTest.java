import me.uquark.barrymore.lexer.Alias;
import me.uquark.barrymore.lexer.Lexer;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;

public class LexerTest {
    private void init() throws SQLException {
        Lexer.loadAliases();
    }

    @Test
    public void testTokenization() throws SQLException {
        init();
        ArrayList<Alias> result = Lexer.tokenize("Выключи свет на кухне, пожалуйста");
        Assert.assertEquals(result.get(0).pWord, "выключить");
        Assert.assertEquals(result.get(1).pWord, "свет");
        Assert.assertEquals(result.get(2).pWord, "кухня");
    }
}
