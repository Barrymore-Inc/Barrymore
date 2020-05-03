package me.uquark.barrymore.architecture;

import me.uquark.barrymore.api.Coords;
import me.uquark.barrymore.db.DatabaseProvider;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Location {
    public final int ID;
    public final String pName;

    public Location(int ID) throws SQLException {
        this.ID = ID;
        PreparedStatement statement = DatabaseProvider.CONNECTION.prepareStatement("SELECT pName FROM V_LOCATION WHERE ID = ?");
        statement.setInt(1, ID);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        pName = resultSet.getString(1);

        resultSet.close();
        statement.close();
    }

    public Location(Coords coords) throws SQLException {
        PreparedStatement statement = DatabaseProvider.CONNECTION.prepareStatement(
        "SELECT ID, pName FROM V_LOCATION WHERE (SX <= ?) and (SY <= ?) and (SZ <= ?) and (EX >= ?) and (EY >= ?) and (EZ >= ?)");
        statement.setFloat(1, coords.x);
        statement.setFloat(2, coords.y);
        statement.setFloat(3, coords.z);
        statement.setFloat(4, coords.x);
        statement.setFloat(5, coords.y);
        statement.setFloat(6, coords.z);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();

        ID = resultSet.getInt(1);
        pName = resultSet.getString(2);
    }
}
