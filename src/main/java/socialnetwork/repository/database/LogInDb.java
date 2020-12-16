package socialnetwork.repository.database;

import socialnetwork.domain.LogIn;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.Pageable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class LogInDb extends AbstractDbRepository<Tuple<String,String>, LogIn> {

    public LogInDb(String url, String username, String password, Validator<LogIn> validator) {
        super(url, username, password, validator);
    }

    @Override
    protected String findOneQuery(Tuple<String, String> tuplu) {
        return "SELECT * from login WHERE username = '"+ tuplu.getLeft()+"' and passw = '" + tuplu.getRight()+"'";

    }

    @Override
    protected LogIn createEntity(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("idF");
        String username = resultSet.getString("username");
        String passw = resultSet.getString("passw");

        LogIn log = new LogIn(id);
        log.setId(new Tuple<>(username,passw));
        return log;
    }

    @Override
    protected String getTableName() {
        return "login";
    }

    @Override
    protected PreparedStatement addQuery(LogIn entity, Connection connection) throws SQLException {
        return connection.prepareStatement("insert into  login(username,passw) values  ('" + entity.getId().getLeft() + "' ,'"+ entity.getId().getRight() + "' )");
    }

    @Override
    protected PreparedStatement delQuery(Tuple<String, String> entity, Connection connection) throws SQLException {
        return connection.prepareStatement("delete from login where username = '" + entity.getLeft() + "' and passw = '"+ entity.getRight() + "'");

    }

    @Override
    protected PreparedStatement updateQuery(LogIn entity, Connection connection) throws SQLException {
        return null;
    }


}
