package me.uquark.barrymore.architecture;

import me.uquark.barrymore.db.DatabaseProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Action extends Entity {
    public final int kClass;
    public Class klass;

    public Action(int ID) throws SQLException {
        super(ID);
        // load from db
        Connection connection = DatabaseProvider.connect();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM ACTION WHERE ID = ?");
        statement.setInt(1, ID);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        this.kClass = resultSet.getInt(2);
        resultSet.close();
        statement.close();
        connection.close();
    }

    @Override
    protected EntityType getType() {
        return EntityType.Action;
    }

    @Override
    public void loadReferences() throws SQLException {
        klass = new Class(kClass);
    }
}
