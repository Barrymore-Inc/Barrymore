import me.uquark.barrymore.db.DatabaseProvider;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseTest {
    private Connection connection;

    private void connect() throws SQLException {
        connection = DatabaseProvider.connect();
    }

    private void disconnect() throws SQLException {
        connection.close();
    }

    @Test
    public void testConnection() throws SQLException {
        connect();
        disconnect();
    }

    @Test
    public void testQuery() throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM TEST");
        rs.next();
        Assert.assertEquals(rs.getString(1), "foo");
        rs.close();
        statement.close();
        disconnect();
    }
}
