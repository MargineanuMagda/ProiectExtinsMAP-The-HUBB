package socialnetwork.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Prietenie extends Entity<Tuple<Long, Long>> {

    LocalDateTime date;

    public Prietenie() {

        date = LocalDateTime.now();
    }

    public Prietenie(LocalDateTime t) {

        date = t;
    }

    /**
     * @return the date when the friendship was created
     */
    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return "Prietenie: User1 = " + getId().getLeft().toString() + " User2 = " + getId().getRight().toString() + " Data=" + getDate().format(formatter);
    }
}
