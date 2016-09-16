package ru.lvlup;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerEndPoint {
    private static final int PORT = 8083;
    private ServerSocket serverSocket;
    private ClientManager clientManager = new ClientManager();

    /**
     * создание сокета, прослушка в главном потоке
     */
    public void startServer(){
        try {
            serverSocket = new ServerSocket(PORT);
            while(true){
                Socket client  = serverSocket.accept();
                clientManager.onClientConnected(client);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
