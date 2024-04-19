package ru.konsist;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

public class SocketRequestService extends Thread {
    private Socket socketSomething;
    private String message;
    private JTextArea areaLog;

    public SocketRequestService(Socket socketSomething, String message, JTextArea areaLog) {
        this.socketSomething = socketSomething;
        this.message = message;
        this.areaLog = areaLog;
    }

    @Override
    public void run() {
        try {
            if(!message.isEmpty()){
                OutputStream outToServer = socketSomething.getOutputStream();
                InputStream inFromServer = socketSomething.getInputStream();

                DataOutputStream out = new DataOutputStream(outToServer);
                DataInputStream in = new DataInputStream(inFromServer);

                //Отправка запроса
                byte[] requestBytes = HexFormat.of().parseHex(message);;
                out.write(requestBytes);
                areaLog.setLineWrap(true);
                areaLog.setText(areaLog.getText() + "Отправленно сообщение:\n=====================================\n");
                areaLog.setText(areaLog.getText() + new String(requestBytes, "Windows-1251") + "\n=====================================\n");

                //Получение ответа на запрос
                String responseServer = "";
                byte readByte;
                List<Byte> listBytes = new ArrayList<>();
                while ((readByte = in.readByte()) != 4) {
                    listBytes.add(readByte);
                    if (readByte == 4) {
                        return;
                    }
                }
                listBytes.add((byte) 4);
                for (byte item : listBytes) {
                    responseServer += (char) item;
                }
                areaLog.setText(areaLog.getText() + "\nПринято сообщение:\n=====================================\n");
                areaLog.setText(areaLog.getText() + new String(responseServer) + "\n=====================================\n");
            }
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }
}
