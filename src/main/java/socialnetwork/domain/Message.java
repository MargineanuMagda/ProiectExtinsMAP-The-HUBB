package socialnetwork.domain;

import socialnetwork.utils.Utils;

import java.time.LocalDateTime;

public class Message extends Entity<Long>{
    Long idMessage;
    Long userFrom;
    Long userTo;
    LocalDateTime date;
    Long reply;
    String mesaj;

    public Message(Long userFrom, Long userTo, Long reply, String mesaj) {
        this.userFrom = userFrom;
        this.userTo = userTo;
        this.reply = reply;
        this.mesaj = mesaj;
        this.date= LocalDateTime.now();
    }

    public Message(Long idMessage, Long userFrom, Long userTo, LocalDateTime date, Long reply, String mesaj) {
        this.idMessage = idMessage;
        this.userFrom = userFrom;
        this.userTo = userTo;
        this.date = date;
        this.reply = reply;
        this.mesaj = mesaj;
    }

    public LocalDateTime getDate() {
        return date;
    }


    public Long getUserFrom() {
        return userFrom;
    }

    public Long getUserTo() {
        return userTo;
    }

    public Long getReply() {
        return reply;
    }

    public String getMesaj() {
        return mesaj;
    }

    @Override
    public String toString() {
        return "ID: "+ getId()+"\tFrom: "+ userFrom +"\tTo: "+userTo+"\tDate: "+ date.format(Utils.myFormatObj)+"\tMessage: "+ mesaj+"\tReply: "+reply;
    }
}
