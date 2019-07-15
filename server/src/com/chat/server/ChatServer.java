package com.chat.server;

import com.chat.network.TCPConnection;
import com.chat.network.TCPConnectionListener;


import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ChatServer implements TCPConnectionListener {

    /**
     * список соединений с сервером
     */
    private final ArrayList<TCPConnection> connections = new ArrayList<>();

    public static void main(String[] args) {

        new ChatServer();
    }

    private ChatServer(){
        System.out.println("Server running...");

        try {
            ServerSocket serverSocket = new ServerSocket(20202);
//            System.out.println();
            while (true) {
                try {
                    new TCPConnection( this, serverSocket.accept());
                }catch (IOException e) {
                    System.out.println("TCPConnection exception: " + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        /**
         * если соединение установлено, то добавляем его в список.
         */
        connections.add(tcpConnection);
        sendToAllConnections("Client connected: " + tcpConnection);
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String value) {
        sendToAllConnections(value);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        /**
         * если соединение отвалилось удаляем его из списка
         */
        connections.remove(tcpConnection);
        sendToAllConnections("Client disconnected: " + tcpConnection);
    }

    @Override
    public synchronized void onException(com.chat.network.TCPConnection tcpConnection, Exception e) {
        /**
         * если произошла ошика то логируем ее
         */
        System.out.println("TCPConnection exception: " + e);
    }

    private void sendToAllConnections(String value){
        /**
         * метод для отправки сообщения всем подключенным пользователям.
         */
        System.out.println(value);
        for (TCPConnection connectionUser : connections) connectionUser.sendString(value);
    }
}
