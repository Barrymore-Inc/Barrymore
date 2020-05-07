package me.uquark.barrymore.brain;

import me.uquark.barrymore.api.BarrymoreBrain;
import me.uquark.barrymore.api.Coords;
import me.uquark.barrymore.api.Order;
import me.uquark.barrymore.architecture.Location;
import me.uquark.barrymore.lexer.Lexer;
import me.uquark.barrymore.utils.Logger;

import java.sql.SQLException;
import java.util.List;

public class Brain implements BarrymoreBrain {
    private final Interpreter interpreter = new Interpreter();

    private static final Logger LOGGER = new Logger("brain");

    public Brain() throws SQLException {}

    @Override
    public Order processUserMessage(Coords userCoords, String message) {
        LOGGER.info(String.format("<[%.0f,%.0f,%.0f]> %s", userCoords.x, userCoords.y, userCoords.z, message));

        Order result = new Order();
        Context context = new Context();

        try {
            context.location = new Location(userCoords);
        } catch (SQLException e) {
            LOGGER.warning("No location found for user");
        }
        context.checkSingleObject = true;

        Interpreter.InterpretationStatus status = interpreter.interprete(message, result, context);
        result.response = status.toString();
        return result;
    }

    @Override
    public List<String> getPhrases() {
        return Lexer.getAliasesAsStrings();
    }
}
