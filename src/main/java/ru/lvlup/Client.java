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

    public Client(Socket socket) {
        this.socket = socket;
        prepareStreams();
    }

    /**
     * метод открытия потоков ввода вывода
     */
    private void prepareStreams() {
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
    public void login() {
        while (userName == null) {
            try {
                String username = reader.readLine();
                if (ClientManager.getInstance().hasClient(username)) {
                    writer.println("Client with same username exists\nTry another username");
                    writer.flush();
                } else {
                    this.userName = username;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ClientManager.getInstance().onClientSignedIn(this);
    }

    /**
     * запуск основной работы с клиеном(откр. возм. переписки
     */
    public void startMessaging() {
        sender = new SendingThread();
        sender.start();

        try {
            while (true) {
                String message = reader.readLine();
                if (message == null) {
                    stopClient();
                    break;
                }
                if (message.startsWith("@")) {
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
    public void stopClient() {
        ClientManager.getInstance().onClientDisconnected(this);
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
     *
     * @param message - сообщение в необработанном виде
     */
    public void onMessageReceived(String message) {
        System.out.println(message);
        sender.addMessage(message);
    }

    /**
     * отправка сообщ
     *
     * @param message - сообщение
     */
    public void sendMessage(String message) {
        writer.println(message);
        writer.flush();
    }
}
