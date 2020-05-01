package me.uquark.barrymore.architecture;

import me.uquark.barrymore.db.DatabaseProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Object extends Entity {
    public final int kLocation;
    public final int kClass;

    public Location location;
    public Class klass;

    public Object(int ID) throws SQLException {
        super(ID);
        // load from db
        Connection connection = DatabaseProvider.connect();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM OBJECT WHERE ID = ?");
        statement.setInt(1, ID);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        kClass = resultSet.getInt(2);
        kLocation = resultSet.getInt(3);
        resultSet.close();
        statement.close();
        connection.close();
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

    public static List<Object> loadByClassAndLocation(int kLocation, int kClass) throws SQLException {
        Connection connection = DatabaseProvider.connect();
        PreparedStatement statement = connection.prepareStatement("SELECT ID FROM OBJECT WHERE (kLocation = ?) and (kClass = ?)");
        statement.setInt(1, kLocation);
        statement.setInt(2, kClass);
        ResultSet resultSet = statement.executeQuery();
        List<Object> objects = new ArrayList<>();
        while (resultSet.next())
            objects.add(new Object(resultSet.getInt(1)));
        resultSet.close();
        statement.close();
        connection.close();
        return objects;
    }

    public static List<Object> loadByObjectAndLocation(int kLocation, int kClass) throws SQLException {
        Connection connection = DatabaseProvider.connect();
        PreparedStatement statement = connection.prepareStatement("SELECT ID FROM OBJECT WHERE (kLocation = ?) and (ID = ?)");
        statement.setInt(1, kLocation);
        statement.setInt(2, kClass);
        ResultSet resultSet = statement.executeQuery();
        List<Object> objects = new ArrayList<>();
        while (resultSet.next())
            objects.add(new Object(resultSet.getInt(1)));
        resultSet.close();
        statement.close();
        connection.close();
        return objects;
    }
}
