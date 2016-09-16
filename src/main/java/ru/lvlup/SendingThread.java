package ru.lvlup;

import com.google.gson.Gson;

import java.io.PrintWriter;
import java.util.ArrayList;

public class SendingThread extends Thread{
    private ClientManager clientManager;
    private ArrayList<String> queue;
    private boolean alive = true;
    private Gson gson = new Gson();
    private String receiver;

    public SendingThread(ClientManager clientManager){
        this.clientManager = clientManager;
        queue = new ArrayList<String>();
    }

    public void run() {
        while(alive){
            if(queue.isEmpty()){
                Thread.yield();
            } else if(alive){
                clientManager.sendMessage(queue.get(0), receiver);
                queue.remove(0);
            }
        }
    }

    public void addMessage(String message){
        queue.add(gson.fromJson(message, Message.class).getSender()+" пишет:"
        +gson.fromJson(message, Message.class).getMessage());
        receiver = gson.fromJson(message, Message.class).getReceiver();
    }

    public void stopSending(){
        alive = false;
    }
}
