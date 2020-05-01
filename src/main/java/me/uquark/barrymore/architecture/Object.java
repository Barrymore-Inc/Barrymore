package me.uquark.barrymore.architecture;

import me.uquark.barrymore.db.DatabaseProvider;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Object extends Entity {
    public final int kLocation;
    public final int kClass;

    public Location location;
    public Class klass;

    public Object(int ID) throws SQLException {
        super(ID);
        PreparedStatement statement = DatabaseProvider.CONNECTION.prepareStatement("SELECT * FROM OBJECT WHERE ID = ?");
        statement.setInt(1, ID);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        kClass = resultSet.getInt(2);
        kLocation = resultSet.getInt(3);
        resultSet.close();
        statement.close();
    }

    @Override
    protected EntityType getType() {
        return EntityType.Object;
    }

    @Override
    public void loadReferences() throws SQLException {
        location = new Location(kLocation);
        klass = new Class(kClass);
    }
}
