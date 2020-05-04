package me.uquark.barrymore.architecture;

import me.uquark.barrymore.db.DatabaseProvider;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Action {
    public final int ID;
    public final String pName;
    public final int kClass;

    public Class klass;

    public Action(int ID) throws SQLException {
        this.ID = ID;
        PreparedStatement statement = DatabaseProvider.CONNECTION.prepareStatement("SELECT * FROM V_ACTION WHERE ID = ?");
        statement.setInt(1, ID);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        pName = resultSet.getString(2);
        kClass = resultSet.getInt(3);

        resultSet.close();
        statement.close();
    }

    public void loadReferences() throws SQLException {
        klass = new Class(kClass);
    }
}
