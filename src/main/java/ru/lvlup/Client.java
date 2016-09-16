package ru.lvlup;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class Client {
    private Socket socket;
    private ClientManager clientManager;
    private BufferedReader reader;
    private PrintWriter writer;
    private SendingThread sender;
    private String userName;
    private ArrayList<Message> messages = new ArrayList<Message>();
    private String receiver;

    public String getUserName() {
        return userName;
    }

    public String getReceiver() {
        return receiver;
    }

    public Client(Socket socket, ClientManager clientManager){
        this.socket = socket;
        this.clientManager = clientManager;
        prepareStreams();
    }

    /**
     * метод открытия потоков ввода вывода
     */
    private void prepareStreams(){
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * авторизация
     */
    public void login(){
        try {
            while(true) {
                userName = reader.readLine();
                System.out.println(clientManager.getClients().size());
                int counter = 0;
                for (int i = 0; i < clientManager.getClients().size(); i++) {
                    if (clientManager.getClients().get(i).getUserName().equals(userName)) {
                        counter++;
                    }
                }
                if (counter == 1) {
                    writer.println("Это имя уже занято. Введите другое имя");
                    writer.flush();
                } else if (counter == 0) {
                    writer.println("Имя свободно. Чаться, дорогой!");
                    writer.flush();
                    break;
                }
                counter = 0;
            }
            clientManager.onClientSignedIn(Client.this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * запуск основной работы с клиеном(откр. возм. переписки
     */
    public void startMessaging(){
        sender = new SendingThread(clientManager);
        sender.start();

        try {
            while(true) {
                String message = reader.readLine();
                if(message == null){
                    stopClient();
                    break;
                }
                if(message.startsWith("@")){
                    String[] receivers = message.split("@");
                    receiver = receivers[1];
                    message = receivers[2];
                }
                Gson gson = new Gson();
                onMessageReceived(gson.toJson(new Message(userName, receiver, message)));
                receiver = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * отключение клиента
     */
    public void stopClient(){
        clientManager.onClientDisconnected(this);
        sender.stopSending();
        try {
            writer.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * событие получ сообщ
     * @param message - сообщение в необработанном виде
     */
    public void onMessageReceived(String message){
        System.out.println(message);
        sender.addMessage(message);
    }

    /**
     * отправка сообщ
     * @param message - сообщение
     */
    public void sendMessage(String message){
        writer.println(message);
        writer.flush();
    }
}
