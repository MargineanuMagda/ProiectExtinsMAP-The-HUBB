package socialnetwork.repository.database;

import socialnetwork.domain.FriendRequest;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.validators.RequestValidator;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class RequestDb extends AbstractDbRepository<Tuple<Long,Long>, FriendRequest> {
    public RequestDb(String url, String username, String password, RequestValidator validator) {
        super(url, username, password, validator);
    }

    /**
     *
     * @param ids of the request we find
     * @return a string which represents the query for finding a request
     */
    @Override
    protected String findOneQuery(Tuple<Long, Long> ids) {
        return "SELECT * from friendrequest WHERE ( userFrom = " + ids.getLeft().toString() + "AND userTo = " + ids.getRight().toString() + ") OR (  userFrom = " + ids.getRight().toString() + "AND userTo = " + ids.getLeft().toString() + ")";
    }


    /**
     *
     * @param resultSet - result set
     * @return a request
     * @throws SQLException when the columns are not written properly
     */
    @Override
    protected FriendRequest createEntity(ResultSet resultSet) throws SQLException {
        Long id1 = resultSet.getLong("userFrom");
        Long id2 = resultSet.getLong("userTo");
        String status = resultSet.getString("status");
        LocalDate data = resultSet.getDate("dataR").toLocalDate();
        LocalDateTime data1 = LocalDateTime.of(data, LocalTime.now());


        FriendRequest request = new FriendRequest(status,data1);
        request.setId(new Tuple<>(id1,id2));
        return request;
    }

    /**
     *
     * @return table name of requests
     */
    @Override
    protected String getTableName() {
        return "friendrequest";
    }


    /**
     *
     * @param entity- entity we want to add
     * @param connection-connection established
     * @return a statement
     * @throws SQLException when sql query does not function properly
     */
    @Override
    protected PreparedStatement addQuery(FriendRequest entity, Connection connection) throws SQLException {
        return connection.prepareStatement("INSERT INTO friendrequest VALUES (" + entity.getId().getLeft().toString() + ",'" + entity.getId().getRight() + "','" + entity.getStatus() + "','"+entity.getData()+"')");

    }

    /**
     *
     * @param id-id of the user we want to delete
     * @param connection-connection established
     * @return a statement
     * @throws SQLException when sql query does not function properly
     */
    @Override
    protected PreparedStatement delQuery(Tuple<Long, Long> id, Connection connection) throws SQLException {
        return connection.prepareStatement("DELETE FROM friendrequest WHERE userFrom = " + id.getLeft().toString() + " AND userTo =" + id.getRight().toString());

    }

    /**
     *
     * @param entity we want to update
     * @param connection  connection established
     * @return a statement
     * @throws SQLException hen sql query does not function properly
     */
    @Override
    protected PreparedStatement updateQuery(FriendRequest entity, Connection connection) throws SQLException {
        return connection.prepareStatement("UPDATE  friendrequest SET status = '" + entity.getStatus() + "' WHERE userFrom = " + entity.getId().getLeft().toString() + " AND userTo =" + entity.getId().getRight().toString());

    }


}
