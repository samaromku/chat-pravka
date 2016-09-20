package ru.lvlup;

import java.net.Socket;
import java.util.ArrayList;

public class ClientManager {
    private static final ClientManager instance = new ClientManager();
    private ArrayList<Client> clients = new ArrayList<Client>();
    private ClientManager(){}
    public static ClientManager getInstance(){
        return instance;
    }

    /**
     * событие подключения клиента
     *
     * @param socket - сокет клиента
     */
    public void onClientConnected(final Socket socket) {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                Client client = new Client(socket);
                client.login();
            }
        });
        thread.start();
    }

    /**
     * событие окончание авторизации
     *
     * @param client авторизованный клиент
     */
    public void onClientSignedIn(Client client) {
        clients.add(client);
        client.startMessaging();
    }

    /**
     * событие отключения клинета
     *
     * @param client - отключенный клиент
     */
    public void onClientDisconnected(Client client) {
        if(clients.remove(client))
        {
            System.out.println("client exit");
        }
    }

    /**
     * @param message  - отправлемое сообщение
     * @param receiver - имяполучателя. Если null рассылка всем
     */
    public void sendMessage(String message, String receiver) {
        for (Client client : clients) {
            if (message != null)
                if (client.getUserName().equals(receiver)) {
                    client.sendMessage(message);
                    break;
                } else {
                    client.sendMessage(message);
                }
        }
    }

    public boolean hasClient(String username) {
        for (Client c : clients) {
            if (c.getUserName().equals(username)) {
                return true;
            }
        }
        return false;
    }
}