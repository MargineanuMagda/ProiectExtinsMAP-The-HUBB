package socialnetwork.utils.events;

import socialnetwork.domain.Utilizator;

public class UserChangeEvent implements Event{
    private ChangeEventType type;
    private Utilizator user, oldUser;

    public UserChangeEvent(ChangeEventType type) {
        this.type = type;
    }

    public UserChangeEvent(ChangeEventType type, Utilizator user) {
        this.type = type;
        this.user = user;
    }

    public UserChangeEvent(ChangeEventType type, Utilizator user, Utilizator oldUser) {
        this.type = type;
        this.user = user;
        this.oldUser = oldUser;
    }

    public ChangeEventType getType() {
        return type;
    }

    public Utilizator getUser() {
        return user;
    }

    public Utilizator getOldUser() {
        return oldUser;
    }
}
