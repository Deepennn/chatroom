package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Anteroom extends Thread{//专门用于等候新的聊客连接并建立通信通道。

    ServerSocket serverSocket;
    int port = 8888;

    public Anteroom(){
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(">>>服务器端已启动! 端口号为: " + port);
    }

    @Override
    public void run() {
        while (!isInterrupted()){
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            BufferedReader cin = null;
            try {
                cin = new BufferedReader (new InputStreamReader( clientSocket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            PrintWriter cout = null;
            try {
                cout = new PrintWriter (clientSocket.getOutputStream(),true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String clientName= null;
            try {
                clientName = cin.readLine().trim();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(clientName.equals("$end_socket")){
                cout.println("$end_succeeded");
                try {
                    cin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                cout.close();
                break;
            }
            else if (!Server.clientNames.contains(clientName)){
                Server.clientNames.add(clientName);
                cout.println("$succeeded");
                SenderReceiver ssr = null;
                try {
                    ssr = new SenderReceiver(clientName,cin,cout);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Server.senderReceivers.add(ssr);
                ssr.start();
            }
            else {
                cout.println("$failed");
                try {
                    cin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                cout.close();
            }
        }
        System.out.println(">>>服务器端已终止！");
    }
}
