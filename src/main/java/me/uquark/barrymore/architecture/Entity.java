package me.uquark.barrymore.architecture;

import me.uquark.barrymore.db.DatabaseProvider;

import java.security.InvalidParameterException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class Entity {

    public enum EntityType {
        Object,
        Action,
        Location,
        Class,
        Unknown;

        public static EntityType getType(char pType) {
            switch (pType) {
                case 'O':
                    return Object;
                case 'A':
                    return Action;
                case 'L':
                    return Location;
                case 'C':
                    return Class;
                default:
                    return Unknown;
            }
        }
    }

    public final int ID;
    public final String pName;
    public final char pType;
    public final EntityType type;

    public Entity(int ID, String pName, char pType) {
        this.ID = ID;
        this.pName = pName;
        this.pType = pType;
        this.type = EntityType.getType(pType);
        if (this.type != getType())
            throw new InvalidParameterException(String.format("Types mismatch. %s expected, but %s found", this.type.toString(), getType().toString()));
    }

    public Entity(int ID) throws SQLException {
        // load from DB
        PreparedStatement statement = DatabaseProvider.CONNECTION.prepareStatement("SELECT * FROM ENTITY WHERE ID = ?");
        statement.setInt(1, ID);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        this.ID = ID;
        pName = resultSet.getString(2);
        pType =  resultSet.getString(3).charAt(0);
        type = EntityType.getType(this.pType);
        resultSet.close();
        statement.close();
        if (this.type != getType())
            throw new InvalidParameterException(String.format("Types mismatch. %s expected, but %s found", this.type.toString(), getType().toString()));
    }

    protected abstract EntityType getType();

    public abstract void loadReferences() throws SQLException;

    public static Entity loadEntity(int ID) throws SQLException {
        PreparedStatement statement = DatabaseProvider.CONNECTION.prepareStatement("SELECT PTYPE FROM ENTITY WHERE ID = ?");
        statement.setInt(1, ID);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        char pType = resultSet.getString(1).charAt(0);
        resultSet.close();
        statement.close();
        switch (EntityType.getType(pType)) {
            case Class:
                return new Class(ID);
            case Action:
                return new Action(ID);
            case Object:
                return new Object(ID);
            case Location:
                return new Location(ID);
            default:
                return null;
        }
    }
}
