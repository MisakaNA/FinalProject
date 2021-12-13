package features;

public class Reminder implements java.io.Serializable {
    private final String userName;
    private final String date;
    private final String notes;
    private String newNotes = null;
    private String newDate = null;
    private final String flag;

    public Reminder(String userName, String date, String notes, String flag) {
        this.userName = userName;
        this.date = date;
        this.notes = notes;
        this.flag = flag;
    }

    public Reminder(String userName, String date, String notes, String newNotes, String newDate, String flag) {
        this.userName = userName;
        this.date = date;
        this.notes = notes;
        this.newNotes = newNotes;
        this.newDate = newDate;
        this.flag = flag;
    }

    public String getNewNotes() {
        return newNotes;
    }

    public String getNewDate() {
        return newDate;
    }

    public String getUserName() {
        return userName;
    }

    public String getDate() {
        return date;
    }

    public String getNotes() {
        return notes;
    }

    public String getFlag() {
        return flag;
    }

    @Override
    public String toString() {
        return "Reminder {" +
                "userName='" + userName + '\'' +
                ", date='" + date + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
}
