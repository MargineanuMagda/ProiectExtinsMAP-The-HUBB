package socialnetwork.repository.database;

import socialnetwork.domain.Event;
import socialnetwork.domain.validators.Validator;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EventDb extends AbstractDbRepository<String, Event> {
    public EventDb(String url, String username, String password, Validator<Event> validator) {
        super(url, username, password, validator);
    }

    @Override
    protected String findOneQuery(String s) {
        return "SELECT * from events WHERE nameEv = '"+ s + "'";
    }

    @Override
    protected Event createEntity(ResultSet resultSet) throws SQLException {
        String name = resultSet.getString("nameEv");
        LocalDateTime date = resultSet.getTimestamp("dataEv").toLocalDateTime();
        String particip = resultSet.getString("participants");
        List<Long> part = Arrays.stream(particip.split(",")).map(x->Long.parseLong(x)).collect(Collectors.toList());
        Event ev = new Event(name,date,part);
        ev.setId(name);
        return ev;
    }

    @Override
    protected String getTableName() {
        return "events";
    }

    @Override
    protected PreparedStatement addQuery(Event entity, Connection connection) throws SQLException {
        final String[] participants = {""};
        entity.getParticipants().stream().map(x->String.valueOf(x)).forEach(x->{
            participants[0] +=x+",";
        });
        return connection.prepareStatement("insert into events (nameEv,dataEv,participants) values ( '" + entity.getName()+"', '" + Timestamp.valueOf(entity.getDate()).toString() + "', '"+ participants[0] + "')" );

    }

    @Override
    protected PreparedStatement delQuery(String s, Connection connection) throws SQLException {
        return connection.prepareStatement("delete from events where nameEv = '" + s + "'");
    }

    @Override
    protected PreparedStatement updateQuery(Event entity, Connection connection) throws SQLException {
        final String[] participants = {""};
        entity.getParticipants().stream().map(x->String.valueOf(x)).forEach(x->{
            participants[0] +=x+",";
        });
        return connection.prepareStatement("update events set participants = '"+participants[0]+"' where nameEv = '"+entity.getName()+"'");
    }
}
