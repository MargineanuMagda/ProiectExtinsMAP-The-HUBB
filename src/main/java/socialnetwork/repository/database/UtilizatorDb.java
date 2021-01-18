package socialnetwork.repository.database;

import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.Pageable;

import java.sql.*;
import java.util.Arrays;
import java.util.List;

public class UtilizatorDb extends AbstractDbRepository<Long, Utilizator> {
    public UtilizatorDb(String url, String username, String password, Validator<Utilizator> validator) {
        super(url, username, password, validator);
    }

    @Override
    protected String findOneQuery(Long id) {
        return "SELECT * from users WHERE idU = " + id.toString();
    }



    @Override
    protected Utilizator createEntity(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("idU");
        String firstName = resultSet.getString("firstname");
        String lastName = resultSet.getString("lastname");
        String bio = resultSet.getString("bio");
        List<String> list = Arrays.asList(bio.split(";").clone());

        Utilizator utilizator = new Utilizator(firstName, lastName);
        utilizator.setId(id);
        utilizator.setBio(list);
        return utilizator;
    }

    @Override
    protected String getTableName() {
        return "users";
    }




    @Override
    protected PreparedStatement addQuery(Utilizator entity, Connection connection) throws SQLException {

        String bio = entity.getSchool()+";"+entity.getLiving()+";"+entity.getFrom()+";"+entity.getHobby()+";"+entity.getSex()+";"+entity.getAbout();
            return connection.prepareStatement("INSERT INTO users VALUES ("+entity.getId().toString() +",'"+entity.getFirstName()+"','"+entity.getLastName()+"', '"+bio+"')");


    }

    @Override
    protected PreparedStatement delQuery(Long id, Connection connection) throws SQLException {
        return connection.prepareStatement("DELETE FROM users WHERE idU = "+id.toString());
    }

    @Override
    protected PreparedStatement updateQuery(Utilizator entity, Connection connection) throws SQLException {
        return connection.prepareStatement("UPDATE  users SET firstname = '"+entity.getFirstName()+"',lastname = '" + entity.getLastName()+"' ,bio = '" + entity.getBio()+"' WHERE idU = "+entity.getId());
    }



}
