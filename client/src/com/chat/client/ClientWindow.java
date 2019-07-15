package com.chat.client;

import com.chat.network.TCPConnection;
import com.chat.network.TCPConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.EventListener;

import static javax.swing.UIManager.*;

public class ClientWindow extends JFrame implements ActionListener, TCPConnectionListener {

    private final String IP_ADDR = "10.10.90.82";
    private final int PORT = 20202;
    private final int WIDH = 600;
    private final int HEIGHT = 400;

    public static void main(String[] args) {

        /**
         * invokeLater(Runnable r) вызывает метод r.run() для выполнения ассинхронно с потоком выполнения событий AWT.
         * Этот поток будет обработан, как только он достигнет вершины очереди событий.
         */
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientWindow();
            }
        });
    }

    //    окно ввода сообщения
    private final JTextArea log = new JTextArea();
    // ввод никнейма
    private final JTextField fieldNickName = new JTextField("Rustam");
    // ввод сообщения
    private final JTextField fieldInput = new JTextField();

    private TCPConnection connection;

    private ClientWindow() {
        //        закрытие окна крестиком
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //        устанавливаем ширину и высоту
        setSize(WIDH, HEIGHT);
        //        устанавливаем окно по центру
        setLocationRelativeTo(null);
        //        поверх всех окон
        setAlwaysOnTop(true);
        //        увидим окно
        setVisible(true);
        //создаем соединение
        try {
            connection = new TCPConnection(this, IP_ADDR, PORT);
        } catch (IOException e) {
            printMsg("Connection exception: " + e);
        }
        JScrollPane scrollPane = new JScrollPane(log);
        //запрет на редактирование месейдж
        log.setEditable(false);
        // автоперенос по словам
        log.setLineWrap(true);
        //добавляем наше окно ввода в окно))
        add(scrollPane, BorderLayout.CENTER);
        //добавляем поле ввода в обработку событий
        fieldInput.addActionListener(this);
        add(fieldInput, BorderLayout.SOUTH);
        add(fieldNickName, BorderLayout.NORTH);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = fieldInput.getText();
        if (msg.equals(""))return;
        fieldInput.setText(null);
        connection.sendString(fieldNickName.getText() + ": " + msg);
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMsg("Connection ready...");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value) {
        printMsg(value);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMsg("Connection disconect");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        printMsg("Connection exception: " + e);
    }

    private synchronized void printMsg(String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //закидываем сообщение в поле и переходим на новую строку
                log.append(msg + "\n");
                //скролим вниз текста
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }
}
