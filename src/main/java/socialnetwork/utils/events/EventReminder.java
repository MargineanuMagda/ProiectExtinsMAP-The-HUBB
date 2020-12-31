package socialnetwork.utils.events;

import java.time.LocalDateTime;
import java.util.TimerTask;

public class EventReminder extends TimerTask implements Event {
    private String name;
    private LocalDateTime date;

    @Override
    public void run() {
        System.out.println("REMINDER : 1h before " + name);
    }

    public EventReminder(String name, LocalDateTime date) {
        this.name = name;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getDate() {
        return date;
    }
}
