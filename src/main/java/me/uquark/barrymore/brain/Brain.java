package me.uquark.barrymore.brain;

import me.uquark.barrymore.api.BarrymoreBinding;
import me.uquark.barrymore.api.BarrymoreBrain;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Brain implements BarrymoreBrain {
    private final Set<BarrymoreBinding> bindings = new HashSet<>();
    private final Interpreter interpreter = new Interpreter();

    public Brain() throws SQLException {}

    @Override
    public void registerBinding(BarrymoreBinding barrymoreBinding) throws RemoteException {
        System.out.println("BINDING REGISTERED");
        bindings.add(barrymoreBinding);
    }

    @Override
    public void unregisterBinding(BarrymoreBinding barrymoreBinding) throws RemoteException {
        System.out.println("BINDING UNREGISTERED");
        bindings.remove(barrymoreBinding);
    }

    @Override
    public void processUserMessage(String s) throws RemoteException {
        System.out.println(s);
        Interpreter.InterpretationResult result = new Interpreter.InterpretationResult();
        Interpreter.InterpretationStatus status = interpreter.interprete(s, result);
        if (status != Interpreter.InterpretationStatus.OK)
            return;
        sendResult(result);
    }

    private void sendResult(Interpreter.InterpretationResult result) {
        for (BarrymoreBinding binding : bindings) {
            try {
                binding.processAction(result.object, result.subjects, result.parameters);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
