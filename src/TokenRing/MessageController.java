package TokenRing;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageController implements Runnable {
    private MessageQueue queue;
    private InetAddress IPAddress;
    private int port;
    private Semaphore WaitForMessage;
    private String nickname;
    private int time_token;
    private Boolean initialToken;
    private int retryAttempts = 1;

    private static final String TOKEN = "4060";
    private static final String MESSAGE = "4066";
    private static final String AKC = "4067";
    private static final int MAX_ATTEMPTS = 3;

    private String originMessage;
    private String destNickname;
    private String originNickname;

    private Boolean messageReadyToSend = false;
    private String message;

    public MessageController(MessageQueue q,
                             String ip_port,
                             int t_token,
                             Boolean t,
                             String n) throws UnknownHostException {

        queue = q;
        String aux[] = ip_port.split(":");
        IPAddress = InetAddress.getByName(aux[0]);
        port = Integer.parseInt(aux[1]);
        time_token = t_token;
        initialToken = t;
        nickname = n;
        WaitForMessage = new Semaphore(0);
    }

    public void ReceivedMessage(String rawMessage) {

        System.out.println("Mensagem recebida: " + rawMessage);

        if (this.isMessage(rawMessage) && this.isForMe()) {
            System.out.println("É uma mensagem para mim");
            System.out.println(this.originNickname + ": " + this.originMessage);
            System.out.println("Preparando para enviar ACK");
            this.prepareACK();
        } else if (this.isMessage(rawMessage) && this.sameOrigin()) {
            if (this.retryAttempts == MAX_ATTEMPTS) {
                System.out.println("Limite de tentativas excedido.");
                this.prepareToken();
                retryAttempts = 1;
            } else {
                retryAttempts++;
                System.out.println("Mensagem retonou à origem.");
                System.out.println("Reenviando mensagem.");
                this.message = rawMessage;
                this.messageReadyToSend = true;
            }
        } else if (this.isACK(rawMessage) && this.isForMe()) {
            System.out.println("É um ACK para mim");
            System.out.println("Preparando para liberar o Token");
            this.prepareToken();
        } else if (this.isToken(rawMessage)) {
            System.out.println("É um Token");
            System.out.println("Preparando para enviar uma mensagem, caso exista");
            this.prepareMessage();
        } else {
            System.out.println("Esta mensagem não é endereçada a mim");
            System.out.println("Repassando a mensagem");
            this.message = rawMessage;
            this.messageReadyToSend = true;
        }

        WaitForMessage.release();
    }

    private Boolean isMessage(String m) {
        String[] messageSplited = m.split(";");

        if (messageSplited.length != 2) {
            return false;
        }

        if (!messageSplited[0].equals(MessageController.MESSAGE)) {
            return false;
        }


        String[] messageInfo = messageSplited[1].split(":");

        if (messageInfo.length != 3) {
            return false;
        }

        this.originNickname = messageInfo[0];
        this.destNickname = messageInfo[1];
        this.originMessage = messageInfo[2];

        return true;
    }

    private Boolean isACK(String m) {
        String[] messageSplited = m.split(";");

        if (messageSplited.length != 2) {
            return false;
        }

        if (!messageSplited[0].equals(MessageController.AKC)) {
            return false;
        }

        this.destNickname = messageSplited[1];

        return true;
    }

    private Boolean isToken(String m) {
        return m.equals(MessageController.TOKEN);
    }

    private Boolean isForMe() {
        return this.destNickname.equals(this.nickname);
    }

    private Boolean sameOrigin() {
        return this.originNickname.equals(this.nickname);
    }

    private void prepareMessage() {
        if (this.queue.isEmpty()) {
            System.out.println("Fila de mensagens vazia");
            System.out.println("Preparando para liberar o Token");
            this.prepareToken();
        } else {
            this.message = MessageController.MESSAGE + ";" +
                    this.nickname + ":" + this.queue.RemoveMessage();
        }
        this.messageReadyToSend = true;
    }

    private void prepareACK() {
        this.message = MessageController.AKC + ";" + this.originNickname;
        this.messageReadyToSend = true;
    }

    private void prepareToken() {
        this.message = new String();
        this.message = MessageController.TOKEN;

        this.messageReadyToSend = true;
    }

    public void cleanUpVariables() {
        this.message = new String();
        this.originMessage = new String();
        this.destNickname = new String();
        this.originNickname = new String();
    }

    @Override
    public void run() {
        DatagramSocket clientSocket = null;
        byte[] sendData;

        try {
            clientSocket = new DatagramSocket();
        } catch (SocketException ex) {
            Logger.getLogger(MessageController.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        while (true) {
            try {
                Thread.sleep(time_token * 1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(MessageController.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (this.initialToken || this.messageReadyToSend) {
                if (this.initialToken) {
                    this.message = MessageController.TOKEN;
                    this.initialToken = false;
                }
                sendData = this.message.getBytes();

                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);

                try {
                    System.out.println("Enviando mensagem: " + this.message);
                    clientSocket.send(sendPacket);
                    System.out.println("Mensagem enviada");
                    this.messageReadyToSend = false;
                    this.cleanUpVariables();
                } catch (IOException ex) {
                    Logger.getLogger(MessageController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            try {
                WaitForMessage.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(MessageController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}