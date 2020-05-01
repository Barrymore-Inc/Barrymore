package me.uquark.barrymore.architecture;

import java.sql.Connection;
import java.sql.SQLException;

public class Class extends Entity {
    public Class(int ID) throws SQLException {
        super(ID);
    }

    @Override
    protected EntityType getType() {
        return EntityType.Class;
    }

    @Override
    public void loadReferences() throws SQLException {}
}
