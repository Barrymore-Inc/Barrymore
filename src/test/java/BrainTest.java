import me.uquark.barrymore.api.BarrymoreBinding;
import me.uquark.barrymore.api.Coords;
import me.uquark.barrymore.api.Order;
import me.uquark.barrymore.brain.Brain;
import me.uquark.barrymore.db.DatabaseProvider;
import me.uquark.barrymore.lexer.Lexer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.rmi.RemoteException;
import java.sql.SQLException;

public class BrainTest implements BarrymoreBinding {
    private Order order;
    private Brain brain;
    private Coords coords;

    @Before
    public void init() throws SQLException {
        DatabaseProvider.CONNECTION = DatabaseProvider.connect();
        Lexer.loadAliases();
        brain = new Brain();
    }

    @Test
    public void testBrainExplicit() throws RemoteException {
        order = null;
        coords = new Coords(0,0,0);
        brain.processUserMessage(this, 0, "Выключи testdev в testroom");
        Assert.assertEquals(order.action.name, "turnOff");
        Assert.assertEquals(order.subjects[0].klass, "Light");
        Assert.assertEquals(order.subjects[0].address, "0:0:0");
    }

    @Test
    public void testBrainImplicitLocation() throws RemoteException {
        order = null;
        coords = new Coords(-65535, -65535, -65535);
        brain.processUserMessage(this, 0, "Выключи testdev");
        Assert.assertEquals(order.action.name, "turnOff");
        Assert.assertEquals(order.subjects[0].klass, "Light");
        Assert.assertEquals(order.subjects[0].address, "0:0:0");
    }

    @Test
    public void testBrainImplicit() throws RemoteException {
        order = null;
        coords = new Coords(0, 0, 0);
        brain.processUserMessage(this, 0, "Выключи testdev");
        Assert.assertEquals(order.action.name, "turnOff");
        Assert.assertEquals(order.subjects[0].klass, "Light");
        Assert.assertEquals(order.subjects[0].address, "0:0:0");
    }

    @Override
    public void processOrder(Order order) throws RemoteException {
        this.order = order;
    }

    @Override
    public String getName() throws RemoteException {
        return "TestBinding";
    }

    @Override
    public Coords getUserLocation(int i) throws RemoteException {
        return coords;
    }
}
