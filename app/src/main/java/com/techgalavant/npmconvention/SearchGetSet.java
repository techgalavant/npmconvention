package com.techgalavant.npmconvention;

/**
 * Created by Mike Fallon
 *
 * Used with JsonParse and with search the events JSON file
 *
 * Credit - http://manishkpr.webheavens.com/android-autocompletetextview-example-json/
 */

public class SearchGetSet {

    String evid,name,presented,description,room,day;

    public SearchGetSet(String evid, String name, String presented, String description, String room, String day){
        this.setEvid(evid);
        this.setName(name);
        this.setPresented(presented);
        this.setDescription(description);
        this.setRoom(room);
        this.setDay(day);
    }

    public String getEvid() {
        return evid;
    }

    public void setEvid(String evid) {
        this.evid = evid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPresented() {
        return presented;
    }

    public void setPresented(String presented) {
        this.presented = presented;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

}

