package com.chat.network;

public interface TCPConnectionListener {

    /**
     * соединение готово
     * @param tcpConnection
     */
    void onConnectionReady(TCPConnection tcpConnection);
    /**
     * принимаем строку
     * @param tcpConnection
     * @param value
     */
    void onReceiveString(TCPConnection tcpConnection, String value);
    /**
     * соединение разорвано
     * @param tcpConnection
     */
    void onDisconnect(TCPConnection tcpConnection);
    /**
     * Возможное возникшее исключение
     */
    void onException(TCPConnection tcpConnection, Exception e);
}
