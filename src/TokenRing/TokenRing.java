package TokenRing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TokenRing {

    public static void main(String[] args) throws IOException {
        String ip_port;
        int port;
        int t_token = 0;
        boolean token = false;
        String nickname_origin;
        String nickname_destination;
        String message;
        Scanner scan = new Scanner(System.in);

        try (BufferedReader inputFile = new BufferedReader(new FileReader("ring.cfg"))) {

            ip_port = inputFile.readLine();
            String aux[] = ip_port.split(":");
            port = Integer.parseInt(aux[1]);

            nickname_origin = inputFile.readLine();

            t_token = Integer.parseInt(inputFile.readLine());

            token = Boolean.parseBoolean(inputFile.readLine());

        } catch (FileNotFoundException ex) {
            Logger.getLogger(TokenRing.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        MessageQueue queue = new MessageQueue();

        MessageController controller = new MessageController(queue, ip_port, t_token, token, nickname_origin);
        Thread thr_controller = new Thread(controller);
        Thread thr_receiver = new Thread(new MessageReceiver(queue, port, controller));

        thr_controller.start();
        thr_receiver.start();

        while (true) {
            System.out.println("Nickname do Destino:");
            nickname_destination = scan.nextLine();
            System.out.println("Mensagem: ");
            message = scan.nextLine();
            queue.AddMessage(messageParser(nickname_destination, message));
        }
    }
    public static String messageParser(String destination, String message){
        return destination + ":" + message;
    }
}