package features;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class Main extends Application {
    private String user = "";
    private List<Reminder> resReminderList = null;
    private List<UserAccount> resUserList = null;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Client client = new Client();

        while (client.connect() == -1) {
            Thread.sleep(1);
        }

        //main client screen
        FXMLLoader clientLoader = new FXMLLoader();
        clientLoader.setLocation(getClass().getResource("Client.fxml"));
        Parent clientRoot = clientLoader.load();
        primaryStage.setTitle("CS-GY 9053 Final Project - Reminder Manager");
        Scene clientScene = new Scene(clientRoot, 451, 319);

        //user interact screen
        FXMLLoader userLoader = new FXMLLoader();
        userLoader.setLocation(getClass().getResource("UserInteraction.fxml"));
        Parent userRoot = userLoader.load();
        primaryStage.setTitle("CS-GY 9053 Final Project - Reminder Manager");
        Scene userScene = new Scene(userRoot, 451, 319);

        //admin interact screen
        FXMLLoader adminLoader = new FXMLLoader();
        adminLoader.setLocation(getClass().getResource("AdminInteract.fxml"));
        Parent adminRoot = adminLoader.load();
        primaryStage.setTitle("CS-GY 9053 Final Project - Reminder Manager");
        Scene adminScene = new Scene(adminRoot, 451, 319);

        /*------Log in------*/
        Button loginButton = (Button) clientScene.lookup("#loginButton");
        TextField loginUserName = (TextField) clientScene.lookup("#loginUserName");
        TextField loginPassword = (TextField) clientScene.lookup("#loginPassword");
        Label welcomeLabel = (Label) userScene.lookup("#welcomeLabel");
        Text errLogin = (Text) clientScene.lookup("#errLogin");

        loginButton.setOnMouseClicked(event -> {
            String userName = loginUserName.getText();
            String password = loginPassword.getText();

            if (!userName.equals("") && !password.equals("")) {
                if (userName.equals("admin")) {
                    resUserList = Controller.INSTANCE.login(userName, password, client);
                } else {
                    resReminderList = Controller.INSTANCE.login(userName, password, client);
                }

                if (resReminderList != null) {
                    user = userName;
                    errLogin.setText("");
                    welcomeLabel.setText("  Welcome, " + userName);
                    try {
                        resReminderList = Controller.INSTANCE.refreshReminder(user, userScene, client);
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    primaryStage.setScene(userScene);
                    primaryStage.show();

                } else if (resUserList != null) {
                    try {
                        resUserList = Controller.INSTANCE.refreshUser(adminScene, client);
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    primaryStage.setScene(adminScene);
                    primaryStage.show();
                } else {
                    errLogin.setText("\tWrong password or user name not exist in the database!");
                }
            }
        });

        /*------Registration------*/
        Button registerButton = (Button) clientScene.lookup("#registerButton");
        TextField regUserName = (TextField) clientScene.lookup("#regUserName");
        TextField regPassword = (TextField) clientScene.lookup("#regPassword");
        TextField confirmPassword = (TextField) clientScene.lookup("#confirmPassword");
        Text errReg = (Text) clientScene.lookup("#errReg");

        registerButton.setOnMouseClicked(event -> {
            String userName = regUserName.getText();
            String password = regPassword.getText();
            String confirmPwd = confirmPassword.getText();

            if (!userName.equals("") && !password.equals("") && !confirmPwd.equals("")) {
                Character res = Controller.INSTANCE.register(userName, password, confirmPwd, client);
                switch (res) {
                    case 'I':
                        errReg.setText("Invalid Input!");
                        break;
                    case 'E':
                        errReg.setText("User Exists!");
                        break;
                    case 'T':
                        user = userName;
                        errLogin.setText("");
                        welcomeLabel.setText("  Welcome, " + userName);
                        try {
                            resReminderList = Controller.INSTANCE.refreshReminder(user, userScene, client);
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        primaryStage.setScene(userScene);
                        primaryStage.show();
                        break;
                    default:
                }
            }
        });

        /*------Refresh------*/
        Label refresh = (Label) userScene.lookup("#refresh");
        refresh.setOnMouseClicked(event -> {
            try {
                resReminderList = Controller.INSTANCE.refreshReminder(user, userScene, client);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

        /*------add reminder------*/
        Button addButton = (Button) userScene.lookup("#addButton");
        TextField newReminder = (TextField) userScene.lookup("#newReminder");
        addButton.setOnMouseClicked(event -> {
            if (!newReminder.getText().equals("")) {
                try {
                    resReminderList = Controller.INSTANCE.addNewReminder(user, newReminder.getText(), client);
                    resReminderList = Controller.INSTANCE.refreshReminder(user, userScene, client);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        /*------edit reminder------*/
        ChoiceBox<Integer> reminderSelector = (ChoiceBox) userScene.lookup("#reminderSelector");
        Button editButton = (Button) userScene.lookup("#editButton");
        TextField changedText = (TextField) userScene.lookup("#changedText");
        editButton.setOnMouseClicked(event -> {
            if (reminderSelector.getValue() != null && !changedText.getText().equals("")) {
                try {
                    resReminderList = Controller.INSTANCE.editReminder(user,
                            resReminderList.get(reminderSelector.getValue() - 1).getDate(),
                            resReminderList.get(reminderSelector.getValue() - 1).getNotes(), changedText.getText(), client);
                    resReminderList = Controller.INSTANCE.refreshReminder(user, userScene, client);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        /*------delete reminder------*/
        ChoiceBox<Integer> deleteNo = (ChoiceBox) userScene.lookup("#deleteNo");
        Button deleteButton = (Button) userScene.lookup("#deleteButton");
        deleteButton.setOnMouseClicked(event -> {
            if (deleteNo.getValue() != null) {
                try {
                    resReminderList = Controller.INSTANCE.deleteReminder(user,
                            resReminderList.get(deleteNo.getValue() - 1).getDate(),
                            resReminderList.get(deleteNo.getValue() - 1).getNotes(), client);
                    resReminderList = Controller.INSTANCE.refreshReminder(user, userScene, client);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        /*------delete User------*/
        Button deleteUserButton = (Button) adminScene.lookup("#deleteUserButton");
        ChoiceBox<Integer> uidSelector = (ChoiceBox) adminScene.lookup("#uidSelector");
        deleteUserButton.setOnMouseClicked(event -> {
            if (uidSelector.getValue() != null) {
                try {
                    resUserList = Controller.INSTANCE.deleteUser(uidSelector.getValue(), client);
                    resUserList = Controller.INSTANCE.refreshUser(adminScene, client);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        /*------Log out user------*/
        Label logout = (Label) userScene.lookup("#logout");
        logout.setOnMouseClicked(event -> {
            newReminder.setText("");
            reminderSelector.getItems().clear();
            changedText.setText("");
            deleteNo.getItems().clear();
            resUserList = null;
            resReminderList = null;
            Controller.INSTANCE.clearInputs(clientScene);
            primaryStage.setScene(clientScene);
            primaryStage.show();
        });

        /*------log out admin------*/
        Label logoutAdmin = (Label) adminScene.lookup("#logoutAdmin");
        logoutAdmin.setOnMouseClicked(event -> {
            uidSelector.getItems().clear();
            resUserList = null;
            resReminderList = null;
            Controller.INSTANCE.clearInputs(clientScene);
            primaryStage.setScene(clientScene);
            primaryStage.show();
        });
        primaryStage.setScene(clientScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
