package ru.konsist;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.HexFormat;

public class Window {
    public void Show(){
        JFrame frame = new JFrame("My First GUI"); // Для окна нужна "рама" - Frame

        // стандартное поведение при закрытии окна - завершение приложения
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(870, 480); // размеры окна
        frame.setLocationRelativeTo(null); // окно - в центре экрана

        JPanel container = new JPanel();

        container.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.gridy = 0;
        constraints.gridx = 0;


        JLabel hostLabel = new JLabel("Host");
        hostLabel.setMinimumSize(new Dimension(50,15));
        container.add(hostLabel, constraints);

        constraints.gridy = 1;
        constraints.gridx = 0;

        JLabel portLabel = new JLabel("Port");
        hostLabel.setMinimumSize(new Dimension(50,15));
        container.add(portLabel, constraints);

        constraints.gridy = 0;
        constraints.gridx = 1;

        JTextField hostTextField = new JTextField();
        hostTextField.setText(Settings.getInstance().getIp());
        hostTextField.setColumns(15);
        container.add(hostTextField, constraints);

        constraints.gridy = 1;
        constraints.gridx = 1;

        JTextField portTextField = new JTextField();
        portTextField.setText(Integer.toString(Settings.getInstance().getPort()));
        hostTextField.setColumns(15);
        container.add(portTextField, constraints);

        constraints.gridheight = 3;
        constraints.gridy = 0;
        constraints.gridx = 2;

        JTextArea textSelectionArea = new JTextArea();
        textSelectionArea.setLineWrap(true);
        JScrollPane listScrollerTextSelectionArea = new JScrollPane(textSelectionArea);
        listScrollerTextSelectionArea.setPreferredSize(new Dimension(300, 390));
        container.add(listScrollerTextSelectionArea, constraints);

        constraints.gridheight = 3;
        constraints.gridy = 0;
        constraints.gridx = 3;

        JTextArea textSelectionLog = new JTextArea();
        textSelectionArea.setLineWrap(true);
        JScrollPane listScrollerTextSelectionLog = new JScrollPane(textSelectionLog);
        listScrollerTextSelectionLog.setPreferredSize(new Dimension(300, 390));
        container.add(listScrollerTextSelectionLog, constraints);

        constraints.gridwidth = 2;
        constraints.gridy = 2;
        constraints.gridx = 0;

        DefaultListModel listModel = new DefaultListModel();
        for (String item: Supports.readFile(Paths.get(".").toAbsolutePath().normalize().toString() + "/Requests.txt")) {
            if (!item.contains("#")){
                listModel.addElement (item);
            }
        }

        JList list = new JList(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setVisibleRowCount(-1);
        list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                byte[] responseBytes;
                responseBytes = HexFormat.of().parseHex(list.getSelectedValue().toString().split("-")[1]);
                String message = "";
                try {
                    message = new String(responseBytes, "Windows-1251");
                } catch (UnsupportedEncodingException ex) {
                    throw new RuntimeException(ex);
                }
                textSelectionArea.setText(message);
            }
        });

        JScrollPane listScroller = new JScrollPane(list);
        listScroller.setPreferredSize(new Dimension(250, 350));
        container.add(listScroller, constraints);


        constraints.gridwidth = 3;
        constraints.gridy = 4;
        constraints.gridx = 0;

        JButton buttonSend = new JButton("Отправить");
        buttonSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SocketRequestService socketRequestService = null;
                try {
                    socketRequestService = new SocketRequestService(new Socket(hostTextField.getText(), Integer.parseInt(portTextField.getText())), list.getSelectedValue().toString().split("-")[1], textSelectionLog);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                socketRequestService.setDaemon(true);
                socketRequestService.setName("TcpClient");
                socketRequestService.run();
            }
        });
        container.add(buttonSend, constraints);

        constraints.gridwidth = 1;
        constraints.gridy = 4;
        constraints.gridx = 3;

        JButton buttonClear = new JButton("Очистить");

        buttonClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textSelectionLog.setText("");
            }
        });
        container.add(buttonClear, constraints);

        frame.getContentPane().add(BorderLayout.WEST, container); // Добавляем кнопку на Frame
        frame.setVisible(true); // Делаем окно видимым
    }
}
