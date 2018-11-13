package TokenRing;

import java.net.InetAddress;
import java.util.ArrayList;

        /* Esta classe deve implementar uma fila de mensagens. Observe que esta fila será
         * acessada por um consumidor (MessageSender) e um produtor (Classe principal, TokenRing).
         * Portanto, implemente controle de acesso (sincronização), para acesso a fila.
         */

public class MessageQueue {
    /*Implemente uma estrutura de dados para manter uma lista de mensagens em formato string.
     * Você pode, por exemplo, usar um ArrayList().
     * Não se esqueça que em uma fila, o primeiro elemente a entrar será o primeiro
     * a ser removido.
     */
    ArrayList<NetworkMessage> queue = new ArrayList<>();

    public MessageQueue(){

    }


    public void AddMessage(String Message){
        /* Adicione a mensagem no final da fila. Não se esqueça de garantir que apenas uma thread faça isso
        por vez. */


    }

    public NetworkMessage PrepareToSend() throws InterruptedException{
        return this.queue.get(0);
    }

    public boolean containsMessage(){
        if(queue.isEmpty())
            return false;
        else
            return true;
    }



}
