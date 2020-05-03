package me.uquark.barrymore;

import me.uquark.barrymore.api.BarrymoreBrain;
import me.uquark.barrymore.api.BarrymoreConfig;
import me.uquark.barrymore.brain.Brain;
import me.uquark.barrymore.db.DatabaseProvider;
import me.uquark.barrymore.lexer.Lexer;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;

public class Barrymore {
    public static void main(String[] args) throws SQLException {
        System.out.println("BOOT");
        try {
            DatabaseProvider.CONNECTION = DatabaseProvider.connect();
            System.out.println("DB CONNECTED");
            Lexer.loadAliases();
            System.out.println("ALIASES LOADED");
            Brain brain = new Brain();
            System.out.println("BRAIN INITIALIZED");

            BarrymoreBrain stub = (BarrymoreBrain) UnicastRemoteObject.exportObject(brain, 0);
            System.out.println("RMI INTERFACE SET UP");
            Registry registry = LocateRegistry.createRegistry(BarrymoreConfig.BARRYMORE_RMI_PORT);
            System.out.println("RMI REGISTRY SET UP");
            registry.bind("BarrymoreBrain", stub);
            System.out.println("RMI INTERFACE ADDED TO RMI REGISTRY");
            System.out.println("WAITING FOR RMI");
            while (true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseProvider.CONNECTION.close();
        }
    }
}
