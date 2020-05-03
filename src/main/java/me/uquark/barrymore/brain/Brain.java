package me.uquark.barrymore.brain;

import me.uquark.barrymore.api.BarrymoreBinding;
import me.uquark.barrymore.api.BarrymoreBrain;
import me.uquark.barrymore.api.Order;
import me.uquark.barrymore.architecture.Location;
import me.uquark.barrymore.utils.Logger;

import java.rmi.RemoteException;
import java.sql.SQLException;

public class Brain implements BarrymoreBrain {
    private final Interpreter interpreter = new Interpreter();

    private static final Logger LOGGER = new Logger("brain");

    public Brain() throws SQLException {}

    @Override
    public void processUserMessage(BarrymoreBinding caller, int userHashCode, String message) throws RemoteException {
        LOGGER.info(String.format("%s : %s", caller.getName(), message));

        Order result = new Order();
        Context context = new Context();

        try {
            context.location = new Location(caller.getUserLocation(userHashCode));
        } catch (SQLException e) {
            LOGGER.warning("No location found for user");
        }
        context.checkSingleObject = true;

        Interpreter.InterpretationStatus status = interpreter.interprete(message, result, context);
        if (status != Interpreter.InterpretationStatus.OK)
            return;
        result.userHashCode = userHashCode;
        sendResult(caller, result);
    }

    private void sendResult(BarrymoreBinding binding, Order result) throws RemoteException {
        binding.processOrder(result);
    }
}
