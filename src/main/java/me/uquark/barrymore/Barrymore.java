package me.uquark.barrymore;

import me.uquark.barrymore.api.BarrymoreBrain;
import me.uquark.barrymore.api.BarrymoreConfig;
import me.uquark.barrymore.brain.Brain;
import me.uquark.barrymore.db.DatabaseProvider;
import me.uquark.barrymore.lexer.Lexer;
import me.uquark.barrymore.utils.Logger;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.Scanner;

public class Barrymore {
    private static final Logger LOGGER = new Logger("main");

    public static void main(String[] args) throws SQLException {
        LOGGER.info("Startup");
        try {
            DatabaseProvider.CONNECTION = DatabaseProvider.connect();
            LOGGER.info("Connected to database");
            LOGGER.info(String.format("Loaded %d aliases", Lexer.loadAliases()));
            Brain brain = new Brain();
            LOGGER.info("Brain initialized");

            BarrymoreBrain stub = (BarrymoreBrain) UnicastRemoteObject.exportObject(brain, 0);
            LOGGER.info("RMI object exported");
            Registry registry = LocateRegistry.createRegistry(BarrymoreConfig.BARRYMORE_RMI_REGISTRY_PORT);
            LOGGER.info("RMI registry set up");
            registry.bind("BarrymoreBrain", stub);
            LOGGER.info("RMI object registered");
            LOGGER.info("Waiting for RMI");
            new Scanner(System.in).nextLine();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseProvider.CONNECTION.close();
        }
        LOGGER.info("Shutdown");
    }
}
