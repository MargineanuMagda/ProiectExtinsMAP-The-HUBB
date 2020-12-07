package socialnetwork.repository.database;

import socialnetwork.domain.Message;
import socialnetwork.domain.validators.Validator;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MessageDb extends AbstractDbRepository<Long, Message> {
    public MessageDb(String url, String username, String password, Validator<Message> validator) {
        super(url, username, password, validator);
    }


    /**
     *
     * @param id of the message we find
     * @return a string which represents the query for finding a message
     */
    @Override
    protected String findOneQuery( Long id) {
        return "SELECT * from messages WHERE idF = "+ id;

    }


    /**
     *
     * @param resultSet - result set
     * @return a message
     * @throws SQLException when the columns are not written properly
     */
    @Override
    protected Message createEntity(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("idF");
        Long userFrom = resultSet.getLong("userFrom");
        String userToString = resultSet.getString("userTo");
        String mesaj = resultSet.getString("mesaj");
        Long reply = resultSet.getLong("reply");
        LocalDateTime date = resultSet.getTimestamp("dataM").toLocalDateTime();
        List<Long> userTo = Arrays.stream(userToString.split(";")).map(x->Long.parseLong(x)).collect(Collectors.toList());

        Message message = new Message(id,userFrom,userTo,date,reply,mesaj);
        message.setId(id);
        return message;
    }

    /**
     *
     * @return table name
     */
    @Override
    protected String getTableName() {
        return "messages";
    }


    /**
     *
     * @param entity- entity we want to add
     * @param connection-connection established
     * @return a statement
     * @throws SQLException - sql problems
     */
    @Override
    protected PreparedStatement addQuery(Message entity, Connection connection) throws SQLException {
        System.out.println(entity);
        final String[] userToS = {""};
        entity.getUserTo().stream().forEach(x-> userToS[0] +=  x + ";");
        System.out.println(userToS[0]);
        return connection.prepareStatement("insert into messages (userFrom , userTo ,dataM , mesaj,reply) values (" + entity.getUserFrom()+",'"+userToS[0] + "','" + Timestamp.valueOf(entity.getDate()).toString() + "' , '"+ entity.getMesaj()+"' ,"+entity.getReply() +")" );

    }

    /**
     *
     * @param aLong - id of the message
     * @param connection-connection established
     * @return a statement
     * @throws SQLException -when sql query does not function properly
     */
    @Override
    protected PreparedStatement delQuery(Long aLong, Connection connection) throws SQLException {
        return connection.prepareStatement("delete from messages where idF = " + aLong);
    }


    /**
     *
     * @param entity we want to update
     * @param connection  connection established
     * @return a statement
     * @throws SQLException when sql query does not function properly
     */
    @Override
    protected PreparedStatement updateQuery(Message entity, Connection connection) throws SQLException {
        return connection.prepareStatement("UPDATE  users SET mesaj = '"+entity.getMesaj()+"',reply = " + entity.getReply()+" WHERE idF = "+entity.getId());

    }


}
