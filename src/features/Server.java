package features;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Server {

    public static void main(String[] args) throws IOException {
        new Server();
    }

    public Server() throws IOException {
        ServerSocket serverSocket = new ServerSocket(8000);
        System.out.println("--Debug Message-- Server started at localhost:8000");
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("--Debug Message-- New client accepted.");
                InetAddress inetAddress = socket.getInetAddress();
                new Thread(new ClientHandler(socket, inetAddress.getHostName())).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
        Socket socket;
        String hostName;

        public ClientHandler(Socket socket, String hostName) {
            this.socket = socket;
            this.hostName = hostName;
        }

        @Override
        public void run() {
            try {
                ObjectInputStream inputFromClient = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream outputToClient = new ObjectOutputStream(socket.getOutputStream());


                while (true) {
                    Object obj = inputFromClient.readObject();
                    if (obj instanceof UserAccount) {
                        UserAccount userAccount = (UserAccount) obj;
                        if (userAccount.getFlag()) {
                            outputToClient.writeObject(registerHandler(userAccount));
                        } else {
                            outputToClient.writeObject(userLoginHandler(userAccount));
                        }

                    } else if (obj instanceof Reminder) {
                        Reminder reminder = (Reminder) obj;
                        switch (reminder.getFlag()) {
                            case "ADD":
                                outputToClient.writeObject(reminderAddHandler(reminder));
                                break;
                            case "EDIT":
                                outputToClient.writeObject(reminderEditHandler(reminder));
                                break;
                            case "DELETE":
                                outputToClient.writeObject(reminderDeleteHandler(reminder));
                                break;
                        }
                    } else if (obj instanceof AdminAccount) {
                        AdminAccount adminAccount = (AdminAccount) obj;
                        if (adminAccount.getDeleteUser() != -1) {
                            outputToClient.writeObject(deleteUserAccount(adminAccount));
                        } else {
                            outputToClient.writeObject(adminLoginHandler(adminAccount));
                        }

                    } else if (obj instanceof String) {
                        String str = (String) obj;
                        outputToClient.writeObject(fetchUserReminders(str));
                    }

                }
            } catch (SocketException se) {
                System.err.println("--Server Error-- Client side connection closed!");
            } catch (IOException | ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }

        }

        public List<Reminder> userLoginHandler(UserAccount accountInfo) throws SQLException {
            List<Reminder> resList = null;
            String userName = accountInfo.getUserName();
            String password = accountInfo.getPassword();
            Connection jdbcConnection = DriverManager.getConnection("jdbc:sqlite:src/AccountDB.db");
            Statement statement = jdbcConnection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * " +
                    "FROM Users " +
                    "WHERE UName = '" + userName + "' " +
                    "AND Password = '" + password + "'");
            if (resultSet.next()) {
                System.out.println("--Debug Mesage-- User found in database! User name: " + resultSet.getString(2));

                resList = fetchUserReminders(resultSet.getString(2));
            }
            resultSet.close();
            statement.close();
            jdbcConnection.close();
            return resList;
        }

        public List<UserAccount> adminLoginHandler(AdminAccount accountInfo) throws SQLException {
            List<UserAccount> resList = null;
            String userName = accountInfo.getUserName();
            String password = accountInfo.getPassword();
            Connection jdbcConnection = DriverManager.getConnection("jdbc:sqlite:src/AccountDB.db");
            Statement statement = jdbcConnection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * " +
                    "FROM Users " +
                    "WHERE UName = '" + userName + "' " +
                    "AND Password = '" + password + "'");
            if (resultSet.next()) {
                System.out.println("--Debug Mesage-- Admin found in database! User name: " + resultSet.getString(2));

                resultSet = statement.executeQuery("SELECT * " +
                        "FROM Users " +
                        "WHERE UName != 'admin' ");

                while (resultSet.next()) {
                    if (resList == null) {
                        resList = new ArrayList<>();
                    }
                    resList.add(new UserAccount(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3)));
                }
            }
            resultSet.close();
            statement.close();
            jdbcConnection.close();
            return resList;
        }

        public Character registerHandler(UserAccount accountInfo) throws SQLException {
            String userName = accountInfo.getUserName();
            String password = accountInfo.getPassword();
            Connection jdbcConnection = DriverManager.getConnection("jdbc:sqlite:src/AccountDB.db");
            Statement statement = jdbcConnection.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT * " +
                    "FROM Users " +
                    "WHERE UName = '" + userName + "' ");
            if (resultSet.next()) {
                resultSet.close();
                statement.close();
                jdbcConnection.close();
                return 'E';
            } else {
                resultSet = statement.executeQuery("SELECT MAX(UID) " +
                        "FROM Users ");

                if (resultSet.next()) {
                    /*int UID = resultSet.getInt(1) + 1;
                    resultSet.close();
                    statement.close();
                    jdbcConnection.close();
                    jdbcConnection = null;
                    statement = null;
                    jdbcConnection = DriverManager.getConnection("jdbc:sqlite:src/AccountDB.db");
                    statement = jdbcConnection.createStatement();*/
                    statement.execute("INSERT INTO Users (UID, UName, Password)" +
                            "VALUES ('" + (resultSet.getInt(1) + 1) + "', '" + userName + "', '" + password + "')");
                }
                resultSet.close();
                statement.close();
                jdbcConnection.close();
            }

            return 'T';
        }

        public List<Reminder> reminderAddHandler(Reminder reminder) throws SQLException {
            List<Reminder> resList = new ArrayList<>();
            String userName = reminder.getUserName();
            String date = reminder.getDate();
            String notes = reminder.getNotes();

            int UID = getUID(userName);

            if (UID == -1) {
                return resList;
            }

            Connection jdbcReminderConnection = DriverManager.getConnection("jdbc:sqlite:src/ReminderDB.db");
            Statement reminderStatement = jdbcReminderConnection.createStatement();
            reminderStatement.executeUpdate("INSERT INTO Reminders (UID, Notes, Time)" +
                    "VALUES ('" + UID + "', '" + notes + "', '" + date + "')");

            System.out.println("--Debug Message-- New Reminder added: " + reminder.toString());
            resList = fetchUserReminders(userName);
            reminderStatement.close();
            jdbcReminderConnection.close();
            return resList;
        }

        public List<Reminder> reminderEditHandler(Reminder reminder) throws SQLException {
            List<Reminder> resList;
            String userName = reminder.getUserName();
            String date = reminder.getDate();
            String notes = reminder.getNotes();
            String newDate = reminder.getNewDate();
            String newNotes = reminder.getNewNotes();

            int UID = getUID(userName);
            Connection jdbcReminderConnection = DriverManager.getConnection("jdbc:sqlite:src/ReminderDB.db");
            Statement reminderStatement = jdbcReminderConnection.createStatement();

            reminderStatement.executeUpdate("UPDATE Reminders " +
                    "SET Notes = '" + newNotes + "', Time = '" + newDate + "'" +
                    "WHERE UID = '" + UID + "' AND Notes = '" + notes + "' " +
                    "AND Time = '" + date + "'");
            System.out.println("--Debug Message-- New Reminder changed: " + reminder.toString());
            resList = fetchUserReminders(userName);

            reminderStatement.close();
            jdbcReminderConnection.close();
            return resList;
        }

        public List<Reminder> reminderDeleteHandler(Reminder reminder) throws SQLException {
            List<Reminder> resList;
            String userName = reminder.getUserName();
            String date = reminder.getDate();
            String notes = reminder.getNotes();

            int UID = getUID(userName);
            Connection jdbcReminderConnection = DriverManager.getConnection("jdbc:sqlite:src/ReminderDB.db");
            Statement reminderStatement = jdbcReminderConnection.createStatement();

            reminderStatement.executeUpdate("DELETE FROM Reminders " +
                    "WHERE UID = '" + UID + "' AND Notes = '" + notes + "' " +
                    "AND Time = '" + date + "'");

            System.out.println("--Debug Message-- New Reminder deleted: " + reminder.toString());
            resList = fetchUserReminders(userName);

            reminderStatement.close();
            jdbcReminderConnection.close();
            return resList;
        }

        public int getUID(String userName) throws SQLException {
            Connection jdbcAccountConnection = DriverManager.getConnection("jdbc:sqlite:src/AccountDB.db");
            Statement statement = jdbcAccountConnection.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT UID " +
                    "FROM Users " +
                    "WHERE UName = '" + userName + "' ");

            if (resultSet.next()) {
                int UID = resultSet.getInt(1);
                resultSet.close();
                statement.close();
                jdbcAccountConnection.close();
                return UID;
            }

            resultSet.close();
            statement.close();
            jdbcAccountConnection.close();

            return -1;
        }

        public List<Reminder> fetchUserReminders(String userName) throws SQLException {
            List<Reminder> resList = new ArrayList<>();
            Connection jdbcReminderConnection = DriverManager.getConnection("jdbc:sqlite:src/ReminderDB.db");
            Statement reminderStatement = jdbcReminderConnection.createStatement();
            int UID = getUID(userName);

            if (UID == -1) {
                return resList;
            }
            ResultSet reminderResultSet = reminderStatement.executeQuery("SELECT * " +
                    "FROM Reminders " +
                    "WHERE UID = '" + UID + "'");

            while (reminderResultSet.next()) {
                resList.add(new Reminder(userName, reminderResultSet.getString(3), reminderResultSet.getString(2), "FETCH"));
            }
            reminderResultSet.close();
            reminderStatement.close();
            jdbcReminderConnection.close();
            return resList;
        }

        private List<UserAccount> deleteUserAccount(AdminAccount adminAccount) throws SQLException {
            List<UserAccount> resList = new ArrayList<>();
            int UID = adminAccount.getDeleteUser();
            Connection jdbcConnection = DriverManager.getConnection("jdbc:sqlite:src/AccountDB.db");
            Statement statement = jdbcConnection.createStatement();
            statement.executeUpdate("DELETE FROM Users WHERE UID = '" + UID + "'");

            Connection jdbcReminderConnection = DriverManager.getConnection("jdbc:sqlite:src/ReminderDB.db");
            Statement reminderStatement = jdbcReminderConnection.createStatement();
            reminderStatement.executeUpdate("DELETE FROM Reminders WHERE UID = '" + UID + "'");

            ResultSet userResultSet = statement.executeQuery("SELECT * " +
                    "FROM Users " +
                    "WHERE UName != 'admin' ");

            while (userResultSet.next()) {
                resList.add(new UserAccount(userResultSet.getInt(1), userResultSet.getString(2), userResultSet.getString(3)));
            }
            reminderStatement.close();
            jdbcReminderConnection.close();
            userResultSet.close();
            statement.close();
            jdbcConnection.close();
            return resList;
        }
    }
}
