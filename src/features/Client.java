package features;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;

public class Client {
    private ObjectOutputStream toServer = null;
    private ObjectInputStream fromServer = null;

    public int connect() {
        try {
            Socket socket = new Socket("localhost", 8000);
            toServer = new ObjectOutputStream(socket.getOutputStream());
            fromServer = new ObjectInputStream(socket.getInputStream());
            return 1;
        } catch (ConnectException ce) {
            System.err.println("Please execute server.java to initialize the server !");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public ObjectInputStream getFromServer() throws IOException, ClassNotFoundException {
        return fromServer;
    }

    public void sendObjectToServer(Object o) throws IOException {
        toServer.writeObject(o);
        toServer.flush();
    }
}
