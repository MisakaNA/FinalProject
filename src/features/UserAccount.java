package features;

public class UserAccount extends Account implements java.io.Serializable {
    private final String userName;
    private final String password;
    private final boolean flag;
    private int id;

    public UserAccount(String userName, String password, boolean flag) {
        this.userName = userName;
        this.password = password;
        this.flag = flag;
    }

    public UserAccount(int UID, String userName, String password) {
        this.id = UID;
        this.userName = userName;
        this.password = password;
        this.flag = false;
    }

    public int getId() {
        return id;
    }

    public boolean getFlag() {
        return flag;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public String getPassword() {
        return password;
    }
}
