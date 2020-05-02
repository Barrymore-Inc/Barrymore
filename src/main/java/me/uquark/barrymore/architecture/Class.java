package me.uquark.barrymore.architecture;

import me.uquark.barrymore.db.DatabaseProvider;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Class {
    public final int ID;
    public final String pName;

    public Class(int ID) throws SQLException {
        this.ID = ID;
        PreparedStatement statement = DatabaseProvider.CONNECTION.prepareStatement("SELECT * FROM V_CLASS WHERE ID = ?");
        statement.setInt(1, ID);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        pName = resultSet.getString(2);

        resultSet.close();
        statement.close();
    }
}
