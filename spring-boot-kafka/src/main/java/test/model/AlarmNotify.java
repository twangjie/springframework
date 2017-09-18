package test.model;

import java.util.List;

public class AlarmNotify {

    private int taskid;
    private String plate;
    private String type;
    private String creatorid;
    private String creatorname;
    private String firstalarmtime;
    private String firstalarmdev;
    private String lastalarmtime;
    private String lastalarmdev;
    private int alarmcount;
    private List<Integer> alarmids;
    private List<String> ids;

    public void setTaskid(int taskid) {
        this.taskid = taskid;
    }

    public int getTaskid() {
        return taskid;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getPlate() {
        return plate;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setCreatorid(String creatorid) {
        this.creatorid = creatorid;
    }

    public String getCreatorid() {
        return creatorid;
    }

    public void setCreatorname(String creatorname) {
        this.creatorname = creatorname;
    }

    public String getCreatorname() {
        return creatorname;
    }

    public void setFirstalarmtime(String firstalarmtime) {
        this.firstalarmtime = firstalarmtime;
    }

    public String getFirstalarmtime() {
        return firstalarmtime;
    }

    public void setFirstalarmdev(String firstalarmdev) {
        this.firstalarmdev = firstalarmdev;
    }

    public String getFirstalarmdev() {
        return firstalarmdev;
    }

    public void setLastalarmtime(String lastalarmtime) {
        this.lastalarmtime = lastalarmtime;
    }

    public String getLastalarmtime() {
        return lastalarmtime;
    }

    public String getLastalarmdev() {
        return lastalarmdev;
    }

    public void setLastalarmdev(String lastalarmdev) {
        this.lastalarmdev = lastalarmdev;
    }

    public void setAlarmcount(int alarmcount) {
        this.alarmcount = alarmcount;
    }

    public int getAlarmcount() {
        return alarmcount;
    }

    public void setAlarmids(List<Integer> alarmids) {
        this.alarmids = alarmids;
    }

    public List<Integer> getAlarmids() {
        return alarmids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public List<String> getIds() {
        return ids;
    }

}
