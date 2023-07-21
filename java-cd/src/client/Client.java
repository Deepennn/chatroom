package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Client extends JFrame implements Runnable{//客户端图形界面线程
    JComboBox<Integer> boxSize = new JComboBox<>();
    JLabel labelIP = new JLabel("IP:");
    JTextField fieldIP = new JTextField("127.0.0.1",10);
    JLabel labelPort = new JLabel("端口:");
    JTextField fieldPort = new JTextField("8888",5);
    JLabel labelName = new JLabel("昵称:");
    JTextField fieldName = new JTextField(10);
    JButton buttonEnter = new JButton("进入聊天室");
    JButton buttonExit = new JButton("退出聊天室");
    JTextArea areaSend = new JTextArea(3,44);
    JButton buttonSend = new JButton("发送");
    JTextArea areaShow = new JTextArea(10,54);
    Component[] ctrls = {labelIP,fieldIP,labelPort,fieldPort,labelName,fieldName,buttonEnter,buttonExit};
    Component[] sends = {areaSend,buttonSend};
    JPanel panelCtrl = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
    JPanel panelSend = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
    JPanel panelShow = new JPanel();
    JScrollPane scrollPane = new JScrollPane(areaShow);

    volatile SenderReceiver currentClientSenderReceiver = null;
    boolean end_server =false;

    public Client(){
        setTitle("全民聊天室_客户端_计算机211_李嘉梁_211302104");
        setBounds(1200, 400, 1300, 650);
        buttonEnter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = fieldName.getText();
                String IP = fieldIP.getText();
                int port = Integer.parseInt(fieldPort.getText());

                if(name.equals("")) {
                    JOptionPane optionPane =new JOptionPane();
                    JOptionPane.showConfirmDialog(panelShow,"昵称不能为空","提示",JOptionPane.DEFAULT_OPTION,JOptionPane.INFORMATION_MESSAGE);
                    fieldName.setText("");
                    return;
                }
                else if (name.equals("$end_socket")){
                    JOptionPane optionPane =new JOptionPane();
                    JOptionPane.showConfirmDialog(panelShow,"昵称不能使用","提示",JOptionPane.DEFAULT_OPTION,JOptionPane.INFORMATION_MESSAGE);
                    fieldName.setText("");
                    return;
                }

                Socket socket = null;
                try {
                    socket = new Socket(IP, port);
                } catch (IOException ex) {
                    JOptionPane optionPane =new JOptionPane();
                    JOptionPane.showConfirmDialog(panelShow,"连接错误","提示",JOptionPane.DEFAULT_OPTION,JOptionPane.INFORMATION_MESSAGE);
                    ex.printStackTrace();
                }
                BufferedReader sin = null;
                try {
                    sin = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                PrintWriter sout = null;
                try {
                    sout = new PrintWriter(socket.getOutputStream(), true);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                sout.println(name);

                try {
                    if (sin.readLine().equals("$succeeded")) {
                        try {
                            currentClientSenderReceiver = new SenderReceiver(name, sin, sout);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        areaShow.setText("");
                        currentClientSenderReceiver.start();
                    }else{
                        JOptionPane optionPane =new JOptionPane();
                        JOptionPane.showConfirmDialog(panelShow,"群中已有相同的昵称，请更改","提示",JOptionPane.DEFAULT_OPTION,JOptionPane.INFORMATION_MESSAGE);
                        fieldName.setText("");
                        sin.close();
                        sout.close();
                        return;
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                buttonEnter.setEnabled(false);
                buttonExit.setEnabled(true);
                buttonSend.setEnabled(true);
                fieldIP.setEnabled(false);
                fieldPort.setEnabled(false);
                fieldName.setEnabled(false);
            }
        });
        buttonExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                currentClientSenderReceiver.preEndClientSenderReceiver =true;
                currentClientSenderReceiver.send("$end_life");
                currentClientSenderReceiver.interrupt();
            }
        });
        buttonSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                String msg = areaSend.getText();
                if(!msg.equals("")){
                    currentClientSenderReceiver.send(msg);
                    areaSend.setText("");
                }
                else{
                    JOptionPane optionPane =new JOptionPane();
                    JOptionPane.showConfirmDialog(panelShow,"输入不能为空","提示",JOptionPane.DEFAULT_OPTION,JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        for(Component ctrl :ctrls){
            panelCtrl.add(ctrl);
            ctrl.setFont(new Font("TimesRoman",Font.PLAIN,25));
        }
        for(Component send :sends){
            panelSend.add(send);
            send.setFont(new Font("TimesRoman",Font.PLAIN,25));
        }
//        panelShow.add(AreaShow);
        areaShow.setFont(new Font("TimesRoman",Font.PLAIN,25));
        areaShow.setEditable(false);
        areaShow.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent mouseEvent) {
                areaShow.setCursor(new Cursor(Cursor.TEXT_CURSOR));
            }
            public void mouseExited(MouseEvent mouseEvent) {
                areaShow.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        areaShow.getCaret().addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                areaShow.getCaret().setVisible(false);
            }
        });
        buttonExit.setEnabled(false);
        buttonSend.setEnabled(false);
        buttonSend.setPreferredSize(new Dimension(200,125));
        areaSend.setLineWrap(true);
        areaSend.setWrapStyleWord(true);
        areaShow.setLineWrap(true);
        areaShow.setWrapStyleWord(true);
        Border border = BorderFactory.createLineBorder(Color.GRAY);
        areaSend.setBorder(BorderFactory.createCompoundBorder(border,BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        areaShow.setBorder(BorderFactory.createCompoundBorder(border,BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        add(panelCtrl,BorderLayout.NORTH);
        add(panelSend,BorderLayout.SOUTH);
        add(panelShow,BorderLayout.CENTER);

        scrollPane.setPreferredSize(areaShow.getPreferredSize());
        panelShow.add(scrollPane);

//        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }
    public void exit(){
        fieldIP.setEnabled(true);
        fieldPort.setEnabled(true);
        fieldName.setEnabled(true);
        buttonExit.setEnabled(false);
        if(!end_server)buttonEnter.setEnabled(true);
        buttonSend.setEnabled(false);
    }

    @Override
    public void run(){
        while (true) {
            if(currentClientSenderReceiver !=null){
                while(!currentClientSenderReceiver.isInterrupted()){
                    String msg = currentClientSenderReceiver.receive();
                    if(msg==null)continue;
//                    System.out.println(msg);
                    if(msg.equals("$interrupt"))break;
//                    System.out.println("receive from server:" + msg );
                    if(!msg.equals("$end_server"))areaShow.setText(areaShow.getText()+msg+"\n");
                    if(msg.equals("$end_server"))end_server=true;
                }
                currentClientSenderReceiver.endClientSenderReceiver =true;
                currentClientSenderReceiver =null;
                exit();
            }
        }
    }

    public static void main(String[] args) {
        new Thread(new Client()).start();
    }
}
