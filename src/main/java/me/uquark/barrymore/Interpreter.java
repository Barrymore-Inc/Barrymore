package me.uquark.barrymore;

import me.uquark.barrymore.architecture.Class;
import me.uquark.barrymore.architecture.Object;
import me.uquark.barrymore.architecture.*;
import me.uquark.barrymore.db.DatabaseProvider;
import me.uquark.barrymore.lexer.Alias;
import me.uquark.barrymore.lexer.Lexer;

import java.security.InvalidParameterException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Interpreter {
    public Interpreter() throws SQLException {}

    public enum InterpretationResult {
        OK,
        SQLException,
        InvalidParameterException,
        NoActionSpecified,
        NoLocationSpecified,
        NoObjectsSpecified,
        NothingSpecified
    }

    private String getNames(List<? extends Entity> entities, String separator) {
        StringBuilder names = new StringBuilder();
        for (int i=0; i < entities.size(); i++) {
            names.append(entities.get(i).pName);
            if (i < entities.size() - 1)
                names.append(separator);
        }
        return names.toString();
    }

    private String[] getIds(List<Alias> aliases) {
        String[] result = new String[aliases.size()];
        for (int i=0; i < aliases.size(); i++)
            result[i] = String.valueOf(aliases.get(i).kEntity);
        return result;
    }

    public InterpretationResult interprete(String command) {
        try {
            Connection connection = DatabaseProvider.connect();
            String ids = String.join(",", getIds(Lexer.tokenize(command)));

            if (ids.isEmpty())
                return InterpretationResult.NothingSpecified;

            String query = String.format("select ID from V_Location where ID in (%s)", ids);
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next())
                return InterpretationResult.NoLocationSpecified;
            int kLocation = resultSet.getInt(1);
            resultSet.close();
            statement.close();

            query = String.format("select ID, kClass from V_Object where kClass in (%s) and kLocation = ?", ids);
            statement = connection.prepareStatement(query);
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

            query = String.format("select ID, kClass from V_Object where ID in (%s) and kLocation = ?", ids);
            statement = connection.prepareStatement(query);
            statement.setInt(1, kLocation);
            resultSet = statement.executeQuery();
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

            if (kObjects.isEmpty())
                return InterpretationResult.NoObjectsSpecified;

            String kClassesStr = String.join(",", kClasses);
            query = String.format("select ID from V_Action where ID in (%s) and kClass in (%s)", ids, kClassesStr);
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            if (!resultSet.next())
                return InterpretationResult.NoActionSpecified;
            int kAction = resultSet.getInt(1);
            resultSet.close();
            statement.close();

            Action action = new Action(kAction);
            List<Object> objects = new ArrayList<>();
            for (int key : kObjects)
                objects.add(new Object(key));

            for (Object object : objects)
                System.out.printf("Execute %s on object %s\n", action.pName, object.pName);
        } catch (SQLException e) {
            e.printStackTrace();
            return InterpretationResult.SQLException;
        } catch (InvalidParameterException e) {
            e.printStackTrace();
            return InterpretationResult.InvalidParameterException;
        }
        return InterpretationResult.OK;

//        Location location = null;
//        Class klass = null;
//        Action action = null;
//        List<Object> objects = new ArrayList<>();
//
//        for (Alias alias : aliases) {
//            Entity current = null;
//            try {
//                current = Entity.loadEntity(alias.kEntity);
//            } catch (SQLException e) {
//                e.printStackTrace();
//                return InterpretationResult.SQLException;
//            } catch (InvalidParameterException e) {
//                e.printStackTrace();
//                return InterpretationResult.InvalidParameterException;
//            }
//            if (current == null)
//                continue;
//            switch (current.type) {
//                case Location:
//                    location = (Location) current;
//                    break;
//                case Class:
//                    klass = (Class) current;
//                    break;
//                case Action:
//                    action = (Action) current;
//                    break;
//                case Object:
//                    objects.add((Object) current);
//                    break;
//            }
//        }
//
//        if (action == null)
//            return InterpretationResult.NoActionSpecified;
//
//        try {
//            if (klass != null && location != null)
//                objects.addAll(Object.loadByClassAndLocation(location.ID, klass.ID));
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        if (location != null)
//            System.out.printf("%s objects [%s] at location %s\n", action.pName, getNames(objects, ", "), location.pName);
//        else
//            System.out.printf("%s objects [%s]\n", action.pName, getNames(objects, ", "));
//
//        return InterpretationResult.OK;
    }
}
