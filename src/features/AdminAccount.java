package features;

public class AdminAccount extends Account implements java.io.Serializable {
    private int deleteUser = -1;
    private String userName;
    private String password;

    public AdminAccount(String loginUserName, String loginPassword) {
        this.userName = loginUserName;
        this.password = loginPassword;
    }

    public AdminAccount(int deleteUser) {
        this.deleteUser = deleteUser;
    }

    public int getDeleteUser() {
        return deleteUser;
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
