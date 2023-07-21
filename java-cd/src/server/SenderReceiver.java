package server;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SenderReceiver extends Thread{//服务器端发送接收数据线程
    String name;
    SimpleDateFormat formatter= new SimpleDateFormat("HH:mm:ss");
    Date date = new Date(System.currentTimeMillis());
    BufferedReader cin = null;
    PrintWriter cout = null;
    volatile boolean logout = false;

    public SenderReceiver(String name, BufferedReader cin, PrintWriter cout) throws IOException {
        this.name=name;
        this.cin=cin;
        this.cout=cout;
    }

    public void send(String msg){
        cout.println(msg);
    }
    public String receive(){
        StringBuffer msg = new StringBuffer();
        String temp = null;
        try {
            temp = cin.readLine();
            if(temp.equals("$interrupt"))return temp;
            if(temp.equals("$end_life")){
//                System.out.println("server -> $end_life");
                return temp;
            }
            if (temp != null && !temp.equals("$terminate")) {
                msg.append(temp);
                temp = cin.readLine();
                while (temp != null && !temp.equals("$terminate")) {
                    msg.append("\n"+temp);
                    temp = cin.readLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return String.valueOf(msg);
    }

    @Override
    public void run() {
        try {
            Server.sendAll("[ "+formatter.format(date)+" ] "+"欢迎 "+ name + " 进入聊天室！");
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (!interrupted()){
            String msg = receive();
            if(msg.equals("$end_life")) {
                try {
                    Server.sendAll("[ "+formatter.format(date)+" ] "+ name + " 退出了聊天室");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            if(msg.equals("$interrupt")){
                if(!Server.endServer[0]){
                    try {
                        Server.sendAll("[ "+formatter.format(date)+" ] "+ name + " 被踢出了聊天室");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            try {
                Server.sendAll("[ "+ formatter.format(date) + " | " + name +" ] "+msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        cout.println("$interrupt");//解决clientSenderReceiver的readline阻塞
        logout = true ;
        synchronized (this){
            try {
                wait();
            } catch (InterruptedException e) {
//                e.printStackTrace();
            }
        }
        try {
            cin.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        cout.close();
    }
}

//class Receiver extends Thread{ } //用于接收特定客户端的信息。
//class Sender extends Thread{ } //用于发送特定客户的的信息。
