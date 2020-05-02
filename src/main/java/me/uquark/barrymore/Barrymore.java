package me.uquark.barrymore;

import me.uquark.barrymore.api.BarrymoreBrain;
import me.uquark.barrymore.brain.Brain;
import me.uquark.barrymore.db.DatabaseProvider;
import me.uquark.barrymore.lexer.Lexer;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;

public class Barrymore {
    public static void main(String[] args) throws SQLException {
        try {
            DatabaseProvider.CONNECTION = DatabaseProvider.connect();
            Lexer.loadAliases();
            Brain brain = new Brain();

            BarrymoreBrain stub = (BarrymoreBrain) UnicastRemoteObject.exportObject(brain, 0);
            Registry registry = LocateRegistry.createRegistry(8766);
            registry.bind("BarrymoreBrain", stub);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseProvider.CONNECTION.close();
        }
    }
}
