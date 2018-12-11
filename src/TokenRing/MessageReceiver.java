package TokenRing;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageReceiver implements Runnable{
    private MessageQueue queue;
    private int port;
    private MessageController controller;

    public MessageReceiver(MessageQueue t, int p, MessageController c){
        queue = t;
        port = p;
        controller = c;
    }

    @Override
    public void run() {
        DatagramSocket serverSocket = null;

        try {
            serverSocket = new DatagramSocket(port);
        } catch (SocketException ex) {
            Logger.getLogger(MessageReceiver.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }



        while(true){
            byte[] receiveData = new byte[1024];

            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            try {
                serverSocket.receive(receivePacket);
            } catch (IOException ex) {
                Logger.getLogger(MessageReceiver.class.getName()).log(Level.SEVERE, null, ex);
            }

            String msg = getRidOfAnnoyingChar(receivePacket);

            controller.ReceivedMessage(msg);
        }
    }

    public String getRidOfAnnoyingChar(DatagramPacket packet){
        String result = new String(packet.getData());
        char[] annoyingchar = new char[1];
        char[] charresult = result.toCharArray();
        result = "";
        for(int i=0;i<charresult.length;i++){
            if(charresult[i]==annoyingchar[0]){
                break;
            }
            result+=charresult[i];
        }
        return result;
    }
}