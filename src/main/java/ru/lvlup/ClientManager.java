package ru.lvlup;

import java.net.Socket;
import java.util.ArrayList;

public class ClientManager {
    private ArrayList<Client> clients = new ArrayList<Client>();

    public ArrayList<Client> getClients() {
        return clients;
    }

    /**
     * событие подключения клиента
     * @param socket - сокет клиента
     */
    public void onClientConnected(final Socket socket){
        Thread thread = new Thread(new Runnable() {
            public void run() {
                    Client client = new Client(socket, ClientManager.this);
                    client.login();
            }
        });
        thread.start();
    }

    /**
     * событие окончание авторизации
     * @param client авторизованный клиент
     */
    public void onClientSignedIn(Client client){
        clients.add(client);
        client.startMessaging();
    }

    /**
     * событие отключения клинета
     * @param client - отключенный клиент
     */
    public void onClientDisconnected(Client client){
        clients.remove(client);
    }

    /**
     *
     * @param message - отправлемое сообщение
     * @param receiver - имяполучателя. Если null рассылка всем
     */
    public void sendMessage(String message, String receiver){
        for (Client client : clients){
            if(receiver ==null){
                client.sendMessage(message);
            }
            else if(client.getUserName().equals(receiver)){
                client.sendMessage(message);
            }
        }

//        if(receiver == null) {
//            for (Client client : clients) {
//                client.sendMessage(message);
//            }
//        }
//        else {
//            for (Client client : clients) {
//                if(client.getUserName().equals(receiver)){
//                    client.sendMessage(message);
//                }
//            }
//        }
    }
}
