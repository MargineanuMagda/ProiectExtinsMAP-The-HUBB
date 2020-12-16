package socialnetwork.repository.database;

import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.Pageable;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class FriendshipDb extends AbstractDbRepository<Tuple<Long, Long>, Prietenie> {


    public FriendshipDb(String url, String username, String password, Validator<Prietenie> validator) {
        super(url, username, password, validator);
    }

    @Override
    protected String findOneQuery(Tuple<Long, Long> id) {
        return "SELECT * from friendships WHERE idf1 = " + id.getLeft().toString() + " AND idf2 =" + id.getRight().toString();
    }




    @Override
    protected Prietenie createEntity(ResultSet resultSet)throws SQLException {
        Long idL = resultSet.getLong("idf1");
        Long idR = resultSet.getLong("idf2");
        LocalDate data = resultSet.getDate("dataf").toLocalDate();
        LocalDateTime dataf = LocalDateTime.of(data, LocalTime.now());
        Prietenie prietenie = new Prietenie(dataf);
        Tuple<Long, Long> id = new Tuple<>(idL, idR);
        prietenie.setId(id);
        return prietenie;
    }

    @Override
    protected String getTableName() {
        return "friendships";
    }




    @Override
    protected PreparedStatement addQuery(Prietenie entity, Connection connection) throws SQLException {
        return connection.prepareStatement("INSERT INTO friendships VALUES (" + entity.getId().getLeft().toString() + ",'" + entity.getId().getRight() + "','" + entity.getDate() + "')");
    }

    @Override
    protected PreparedStatement delQuery(Tuple<Long, Long> id, Connection connection) throws SQLException {
        return connection.prepareStatement("DELETE FROM friendships WHERE idf1 = " + id.getLeft().toString() + " AND idf2 =" + id.getRight().toString());
    }

    @Override
    protected PreparedStatement updateQuery(Prietenie entity, Connection connection) throws SQLException {
        return connection.prepareStatement("UPDATE  friendships SET dataf = '" + entity.getDate() + "' WHERE idf1 = " + entity.getId().getLeft().toString() + " AND idf2 =" + entity.getId().getRight().toString());

    }



    public List<Long> getFriends(Long idf1) {
        List<Long> friends = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendships WHERE idf1 = " + idf1 + " OR idf2 = " + idf1);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long idL = resultSet.getLong("idf1");
                Long idR = resultSet.getLong("idf2");
                if (!idL.equals(idf1))
                    friends.add(idL);
                else
                    friends.add(idR);
            }
            return friends;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


}
