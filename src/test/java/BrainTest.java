import me.uquark.barrymore.api.BarrymoreBinding;
import me.uquark.barrymore.api.Order;
import me.uquark.barrymore.brain.Brain;
import me.uquark.barrymore.db.DatabaseProvider;
import me.uquark.barrymore.lexer.Lexer;
import org.junit.Assert;
import org.junit.Test;

import java.rmi.RemoteException;
import java.sql.SQLException;

public class BrainTest implements BarrymoreBinding {
    private Order order;
    private Brain brain;

    private void init() throws SQLException, RemoteException {
        DatabaseProvider.CONNECTION = DatabaseProvider.connect();
        Lexer.loadAliases();
        brain = new Brain();
        brain.registerBinding(this);
    }

    @Test
    public void testBrain() throws SQLException, RemoteException {
        init();
        brain.processUserMessage("TestBinding", "Выключи свет и телевизор в спальне");
        Assert.assertEquals(order.action.name, "turnOff");
        Assert.assertEquals(order.subjects[0].klass, "Light");
        Assert.assertEquals(order.subjects[1].klass, "TV");
    }

    @Override
    public void processOrder(Order order) throws RemoteException {
        this.order = order;
    }

    @Override
    public String getName() throws RemoteException {
        return "TestBinding";
    }
}
