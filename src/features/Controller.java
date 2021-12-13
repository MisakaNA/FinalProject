package features;

import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public enum Controller {
    INSTANCE;

    public List login(String loginUserName, String loginPassword, Client client) {
        try {
            if (!loginUserName.equals("admin")) {
                client.sendObjectToServer(new UserAccount(loginUserName, loginPassword, false));
            } else {
                AdminAccount adminAccount = new AdminAccount(loginUserName, loginPassword);
                client.sendObjectToServer(adminAccount);
            }

            return (List) client.getFromServer().readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Character register(String regUserName, String regPassword, String confirmPassword, Client client) {
        if (!regPassword.equals(confirmPassword)) {
            return 'I';
        }
        for (Character c : regPassword.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                return 'I';
            }
        }

        try {
            client.sendObjectToServer(new UserAccount(regUserName, regPassword, true));
            return (Character) client.getFromServer().readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 'T';
    }

    public ArrayList<Reminder> addNewReminder(String user, String notes, Client client) throws IOException, ClassNotFoundException {
        String date = java.time.LocalDateTime.now().toString().replace('T', ' ');
        client.sendObjectToServer(new Reminder(user, date, notes, "ADD"));

        return (ArrayList<Reminder>) client.getFromServer().readObject();
    }

    public ArrayList<Reminder> editReminder(String user, String date, String notes, String newNotes, Client client)
            throws IOException, ClassNotFoundException {
        String newDate = java.time.LocalDateTime.now().toString().replace('T', ' ');
        client.sendObjectToServer(new Reminder(user, date, notes, newNotes, newDate, "EDIT"));

        return (ArrayList<Reminder>) client.getFromServer().readObject();
    }

    public ArrayList<Reminder> deleteReminder(String user, String date, String notes, Client client)
            throws IOException, ClassNotFoundException {
        client.sendObjectToServer(new Reminder(user, date, notes, "DELETE"));

        return (ArrayList<Reminder>) client.getFromServer().readObject();
    }

    public ArrayList<UserAccount> deleteUser(int UID, Client client) throws IOException, ClassNotFoundException {
        client.sendObjectToServer(new AdminAccount(UID));

        return (ArrayList<UserAccount>) client.getFromServer().readObject();
    }

    public List<Reminder> refreshReminder(String user, Scene userScene, Client client) throws IOException, ClassNotFoundException {
        client.sendObjectToServer(user);
        List<Reminder> resList = (ArrayList<Reminder>) client.getFromServer().readObject();
        ChoiceBox<Integer> reminderSelector = (ChoiceBox) userScene.lookup("#reminderSelector");
        ChoiceBox<Integer> deleteNo = (ChoiceBox) userScene.lookup("#deleteNo");
        TextArea resText = (TextArea) userScene.lookup("#resText");
        resText.setText("");
        deleteNo.getItems().clear();
        reminderSelector.getItems().clear();
        List<Integer> numbers = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("Reminders for ").append(user).append(" in record are:\n\n");
        for (int i = 0; i < resList.size(); i++) {
            numbers.add(i + 1);
            sb.append("No. ").append(i + 1).append("\n");
            sb.append("Reminder Notes: ").append(resList.get(i).getNotes()).append("\n");
            sb.append("Create/Edit Date: ").append(resList.get(i).getDate()).append("\n");
            sb.append("--------------------------------------------------\n");
        }
        sb.append("-> End of record...");
        resText.setText(sb.toString());
        deleteNo.getItems().addAll(numbers);
        reminderSelector.getItems().addAll(numbers);
        return resList;
    }

    public List<UserAccount> refreshUser(Scene adminScene, Client client) throws IOException, ClassNotFoundException {
        client.sendObjectToServer(new AdminAccount("admin", "admin"));
        List<UserAccount> resUserList = (ArrayList<UserAccount>) client.getFromServer().readObject();
        ChoiceBox<Integer> uidSelector = (ChoiceBox) adminScene.lookup("#uidSelector");
        TextArea usersTextArea = (TextArea) adminScene.lookup("#usersTextArea");
        usersTextArea.setText("");
        uidSelector.getItems().clear();
        List<Integer> numbers = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (UserAccount ua : resUserList) {
            numbers.add(ua.getId());
            sb.append("UID = ").append(ua.getId()).append("\t\tUser Name = ").append(ua.getUserName())
                    .append("\t\tPassword = ").append(ua.getPassword()).append("\n");
            sb.append("----------------------------------------------------------------------------\n");
        }
        uidSelector.getItems().addAll(numbers);
        usersTextArea.setText(sb.toString());
        return resUserList;
    }

    public void clearInputs(Scene clientScene) {
        TextField loginUserName = (TextField) clientScene.lookup("#loginUserName");
        TextField loginPassword = (TextField) clientScene.lookup("#loginPassword");
        Text errLogin = (Text) clientScene.lookup("#errLogin");
        TextField regUserName = (TextField) clientScene.lookup("#regUserName");
        TextField regPassword = (TextField) clientScene.lookup("#regPassword");
        TextField confirmPassword = (TextField) clientScene.lookup("#confirmPassword");
        Text errReg = (Text) clientScene.lookup("#errReg");

        loginUserName.setText("");
        loginPassword.setText("");
        errLogin.setText("");
        regUserName.setText("");
        regPassword.setText("");
        confirmPassword.setText("");
        errReg.setText("");
    }

}
