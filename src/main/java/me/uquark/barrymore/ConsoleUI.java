package me.uquark.barrymore;

import me.uquark.barrymore.db.DatabaseProvider;
import me.uquark.barrymore.lexer.Lexer;

import java.sql.SQLException;
import java.util.Scanner;

public class ConsoleUI {
    public static void main(String[] args) throws SQLException {
        try {
            DatabaseProvider.CONNECTION = DatabaseProvider.connect();

            Lexer.loadAliases();
            Interpreter interpreter = new Interpreter();
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String line = scanner.nextLine();
                if (line.equals("/exit"))
                    break;
                System.out.printf("Execution result: %s\n", interpreter.interprete(line).toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseProvider.CONNECTION.close();
        }
    }
}
