package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class Server extends Thread{//服务器端主线程
    public static volatile HashSet<String> clientNames = new HashSet<>();
    public static volatile ArrayList<SenderReceiver> senderReceivers = new ArrayList<>();
    public static volatile boolean [] endServer = {false,false};
    public static boolean add(String name){
        return clientNames.add(name);
    }
    public static void sendAll(String msg) throws IOException {
        for (SenderReceiver s : senderReceivers) {
            s.send(msg);
        }
    }

    @Override
    public void run() {
        Inspection inspection = new Inspection();
        inspection.start();
        Anteroom anteroom = new Anteroom();
        anteroom.start();
        Control control = new Control();
        control.start();
        while(true){
            boolean end = true;
            for(boolean e : endServer){
                end&=e;
            }
            if(end)break;
        }
        inspection.interrupt();
        anteroom.interrupt();
        control.interrupt();
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.start();
    }
}
