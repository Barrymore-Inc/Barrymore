package me.uquark.barrymore.brain;

import me.uquark.barrymore.api.BarrymoreBinding;
import me.uquark.barrymore.api.BarrymoreBrain;
import me.uquark.barrymore.api.Order;
import me.uquark.barrymore.utils.Logger;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class Brain implements BarrymoreBrain {
    private final Set<BarrymoreBinding> bindings = new HashSet<>();
    private final Interpreter interpreter = new Interpreter();

    private static final Logger LOGGER = new Logger("brain");

    public Brain() throws SQLException {}

    @Override
    public void registerBinding(BarrymoreBinding barrymoreBinding) throws RemoteException {
        LOGGER.info(String.format("Binding registered: %s", barrymoreBinding.getName()));
        bindings.add(barrymoreBinding);
    }

    @Override
    public void unregisterBinding(BarrymoreBinding barrymoreBinding) throws RemoteException {
        LOGGER.info(String.format("Binding unregistered: %s", barrymoreBinding.getName()));
        bindings.remove(barrymoreBinding);
    }

    @Override
    public void processUserMessage(String callerName, String message) throws RemoteException {
        LOGGER.info(String.format("%s : %s", callerName, message));
        Order result = new Order();
        Interpreter.InterpretationStatus status = interpreter.interprete(message, result);
        if (status != Interpreter.InterpretationStatus.OK)
            return;
        sendResult(result);
    }

    private void sendResult(Order result) {
        for (BarrymoreBinding binding : bindings) {
            try {
                binding.processOrder(result);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
