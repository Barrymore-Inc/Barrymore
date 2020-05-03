import me.uquark.barrymore.api.ActionObject;
import me.uquark.barrymore.api.ActionSubject;
import me.uquark.barrymore.api.BarrymoreBinding;
import me.uquark.barrymore.brain.Brain;
import me.uquark.barrymore.db.DatabaseProvider;
import me.uquark.barrymore.lexer.Lexer;
import org.junit.Assert;
import org.junit.Test;

import java.rmi.RemoteException;
import java.sql.SQLException;

public class BrainTest implements BarrymoreBinding {
    private ActionObject actionObject;
    private ActionSubject[] actionSubjects;
    private String[] parameters;
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
        brain.processUserMessage("Выключи свет и телевизор в спальне");
        Assert.assertEquals(actionObject.name, "turnOff");
        Assert.assertEquals(actionSubjects[0].klass, "Light");
        Assert.assertEquals(actionSubjects[1].klass, "TV");
    }

    @Override
    public void processAction(ActionObject actionObject, ActionSubject[] actionSubjects, String[] parameters) {
        this.actionObject = actionObject;
        this.actionSubjects = actionSubjects;
        this.parameters = parameters;
    }
}
