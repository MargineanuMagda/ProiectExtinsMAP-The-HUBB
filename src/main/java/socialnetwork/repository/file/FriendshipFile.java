package socialnetwork.repository.file;

import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.validators.Validator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FriendshipFile extends AbstractFileRepository<Tuple<Long, Long>, Prietenie> {


    public FriendshipFile(String fileName, Validator<Prietenie> validator) {
        super(fileName, validator);
    }

    @Override
    public Prietenie extractEntity(List<String> attributes) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        Prietenie friendship = new Prietenie(LocalDateTime.parse(attributes.get(2), formatter));
        Tuple<Long, Long> ids = new Tuple<Long, Long>(Long.parseLong(attributes.get(0)), Long.parseLong(attributes.get(1)));
        friendship.setId(ids);
        return friendship;
    }

    @Override
    protected String createEntityAsString(Prietenie entity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return entity.getId().getLeft().toString() + ";" + entity.getId().getRight().toString() + ";" + entity.getDate().format(formatter);
    }
}
