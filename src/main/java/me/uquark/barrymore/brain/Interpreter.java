package me.uquark.barrymore.brain;

import me.uquark.barrymore.api.Order;
import me.uquark.barrymore.api.Subject;
import me.uquark.barrymore.architecture.Action;
import me.uquark.barrymore.architecture.Object;
import me.uquark.barrymore.db.DatabaseProvider;
import me.uquark.barrymore.lexer.AbstractToken;
import me.uquark.barrymore.lexer.Alias;
import me.uquark.barrymore.lexer.Lexer;
import me.uquark.barrymore.lexer.Parameter;

import java.security.InvalidParameterException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Interpreter {
    public Interpreter() throws SQLException {}

    public enum InterpretationStatus {
        OK,
        SQLException,
        InvalidParameterException,
        NoActionSpecified,
        NoLocationSpecified,
        NoObjectsSpecified,
        NothingSpecified
    }

    private String[] getIds(List<Alias> aliases) {
        String[] result = new String[aliases.size()];
        for (int i=0; i < aliases.size(); i++)
            result[i] = String.valueOf(aliases.get(i).kEntity);
        return result;
    }

    private String[] getWords(List<? extends AbstractToken> tokens) {
        String[] result = new String[tokens.size()];
        for (int i=0; i < tokens.size(); i++)
            result[i] = tokens.get(i).pWord;
        return result;
    }

    private void separateTokens(List<AbstractToken> tokens, List<Alias> aliases, List<Parameter> parameters) {
        for (AbstractToken token : tokens)
            if (token instanceof Parameter)
                parameters.add((Parameter) token);
            else if (token instanceof Alias)
                aliases.add((Alias) token);
    }

    public InterpretationStatus interprete(String command, Order result, Context context) {
        try {
            List<Alias> aliases = new ArrayList<>();
            List<Parameter> parameters = new ArrayList<>();

            separateTokens(Lexer.tokenize(command), aliases, parameters);

            String ids = String.join(",", getIds(aliases));

            if (ids.isEmpty())
                return InterpretationStatus.NothingSpecified;

            String query = String.format("select ID from V_Location where ID in (%s)", ids);
            PreparedStatement statement = DatabaseProvider.CONNECTION.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            int kLocation = 0;
            if (resultSet.next()) {
                kLocation = resultSet.getInt(1);
            } else if (context.location != null) {
                kLocation = context.location.ID;
            } else if (!context.checkSingleObject) {
                return InterpretationStatus.NoLocationSpecified;
            }
            resultSet.close();
            statement.close();

            query = String.format("select ID, kClass from V_Object where ((kClass in (%s)) or (ID in (%s))) and kLocation = ?", ids, ids);
            statement = DatabaseProvider.CONNECTION.prepareStatement(query);
            statement.setInt(1, kLocation);
            resultSet = statement.executeQuery();
            List<Integer> kObjects = new ArrayList<>();
            List<String> kClasses = new ArrayList<>();
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                kObjects.add(id);
                String kClass = String.valueOf(resultSet.getInt(2));
                if (!kClass.equals("0"))
                    kClasses.add(kClass);
                else
                    kClasses.add(String.valueOf(id));
            }
            resultSet.close();
            statement.close();

//            query = String.format("select ID, kClass from V_Object where ID in (%s) and kLocation = ?", ids);
//            statement = DatabaseProvider.CONNECTION.prepareStatement(query);
//            statement.setInt(1, kLocation);
//            resultSet = statement.executeQuery();
//            while (resultSet.next()) {
//                int id = resultSet.getInt(1);
//                kObjects.add(id);
//                String kClass = String.valueOf(resultSet.getInt(2));
//                if (!kClass.equals("0"))
//                    kClasses.add(kClass);
//                else
//                    kClasses.add(String.valueOf(id));
//            }
//            resultSet.close();
//            statement.close();

            if (kObjects.isEmpty() && context.checkSingleObject) {
                query = String.format("select ID, kClass from V_Object where (kClass in (%s)) or (ID in (%s))", ids, ids);
                statement = DatabaseProvider.CONNECTION.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                resultSet = statement.executeQuery();
                if (!resultSet.last())
                    return InterpretationStatus.NoObjectsSpecified;
                if (resultSet.getRow() > 1)
                    return InterpretationStatus.NoObjectsSpecified;
                else {
                    resultSet.first();
                    int id = resultSet.getInt(1);
                    kObjects.add(id);
                    String kClass = String.valueOf(resultSet.getInt(2));
                    if (!kClass.equals("0"))
                        kClasses.add(kClass);
                    else
                        kClasses.add(String.valueOf(id));
                }
            }
            resultSet.close();
            statement.close();

            String kClassesStr = String.join(",", kClasses);
            query = String.format("select ID from V_Action where ID in (%s) and kClass in (%s)", ids, kClassesStr);
            statement = DatabaseProvider.CONNECTION.prepareStatement(query);
            resultSet = statement.executeQuery();
            if (!resultSet.next())
                return InterpretationStatus.NoActionSpecified;
            int kAction = resultSet.getInt(1);
            resultSet.close();
            statement.close();

            result.action = new me.uquark.barrymore.api.Action(new Action(kAction).pName);
            result.subjects = new Subject[kObjects.size()];
            for (int i=0; i < kObjects.size(); i++) {
                Object object = new Object(kObjects.get(i));
                object.loadReferences();
                result.subjects[i] = new Subject(object.klass.pName, object.address);
            }
            result.parameters = getWords(parameters);
        } catch (SQLException e) {
            e.printStackTrace();
            return InterpretationStatus.SQLException;
        } catch (InvalidParameterException e) {
            e.printStackTrace();
            return InterpretationStatus.InvalidParameterException;
        }
        return InterpretationStatus.OK;
    }
}
