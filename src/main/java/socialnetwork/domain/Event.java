package socialnetwork.domain;

import socialnetwork.utils.Utils;
import socialnetwork.utils.observer.Observable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Event extends Entity<String>{
    private String name;
    private LocalDateTime date;
    private List<Long> participants;

    public String getName() {
        return name;
    }

    public LocalDate getData(){
        return date.toLocalDate();
    }
    public LocalTime getTime(){
        return date.toLocalTime();
    }
    public LocalDateTime getDate() {
        return date;
    }

    public Event(String name, LocalDateTime date, List<Long> participants) {
        this.name = name;
        this.date = date;
        this.participants = participants;
    }

    public Event(String name, LocalDateTime date) {
        this.name = name;
        this.date = date;
        this.participants = new ArrayList<>();
    }
    public void addParticipant(Long id){
        participants.add(id);
    }
    public void removeParticipant(Long id){
        participants.remove(id);
    }

    public List<Long> getParticipants() {
        return participants;
    }

    @Override
    public String toString() {
        return getName()+" on "+ getDate().format(Utils.myFormatObj);
    }
}
