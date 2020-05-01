package me.uquark.barrymore.architecture;

import java.sql.SQLException;

public class Location extends Entity {
    public Location(int ID) throws SQLException {
        super(ID);
    }

    @Override
    protected EntityType getType() {
        return EntityType.Location;
    }

    @Override
    public void loadReferences() throws SQLException {}
}
