package com.chat.network;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class TCPConnection {

    /**
     * сокет связаный с соединением
     */
    private final Socket socket;
    /**
     * поток который слушает входящее сооединение.
     */
    private final Thread rxThread;
    /**
     * слушатель событий
     */
    private final TCPConnectionListener eventListener;
    /**
     * поток ввода
     */
    private final BufferedReader in;
    /**
     * поток вывода
     */
    public final BufferedWriter out;

    /**
     * (перегружаем) конструктор для создания соединения(сокета) внутри.
     * @param eventListener
     * @param ipAddr
     * @param port
     * @throws IOException
     */
    public TCPConnection(TCPConnectionListener eventListener, String ipAddr, int port) throws IOException {
        /**
         * вызываем метод из второго конструктора.
         */
        this(eventListener, new Socket(ipAddr, port));
    }

    /**
     * создает соединение с сокетом(конструктор для соединения которое уже сделано кем то).
     * @param socket
     * @param eventListener
     */
    public TCPConnection(TCPConnectionListener eventListener, Socket socket) throws IOException {
        this.socket = socket;
        this.eventListener = eventListener;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        /**
         * создаем поток и помещаем в него анонимный класс в котором переопределяем метод ран
         */
        rxThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    /**
                     * передали экземпляр обрамляющего класса
                     */
                    eventListener.onConnectionReady(TCPConnection.this);
                    while (!rxThread.isInterrupted()) {
                        String msg = in.readLine();
                        eventListener.onReceiveString(TCPConnection.this, msg);
                    }
                } catch (IOException e) {
                    eventListener.onException(TCPConnection.this, e);
                } finally {
                    eventListener.onDisconnect(TCPConnection.this);
                }
            }
        });
        rxThread.start();
    }

    /**
     * синхронизируем методы для безопасного обращения из разных потоков
     * @param value
     */
    public synchronized void sendString(String value) {
        try {
            /**
             * засовываем строку в поток вывода и если возникло исключение то обрабатываем его и вызываем дисконект соединения
             */
            out.write(value + "\r\n");
            /**
             * принудительно отправляем все и закрываем буффер
             */
            out.flush();

        } catch (IOException e) {
            eventListener.onException(TCPConnection.this, e);
            disconnect();
        }
    }

    public synchronized void disconnect() {
        /**
         * закрываем поток
         */
        rxThread.interrupt();
        /**
         * закрываем socket и при возникновении эксепшена обрабатываем его в eventListener
         */
        try {
            socket.close();
        } catch (IOException e) {
           eventListener.onException(TCPConnection.this, e);
        }
    }

    @Override
    public String toString() {
        return "TCPConnection: " + socket.getInetAddress() + ": " + socket.getPort();
    }
}
