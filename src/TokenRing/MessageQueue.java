package TokenRing;


import java.util.LinkedList;
import java.util.Deque;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/* Esta classe deve implementar uma fila de mensagens. Observe que esta fila será
 * acessada por um consumidor (MessageSender) e um produtor (Classe principal, TokenRing).
 * Portanto, implemente controle de acesso (sincronização), para acesso a fila.
 */

public class MessageQueue {
    Deque<String> deque;
    static Semaphore lock = new Semaphore(1);

    public MessageQueue(){
        this.deque = new LinkedList<>();

    }

    public Boolean isEmpty() {
        return this.deque.isEmpty();
    }

    /*Implemente uma estrutura de dados para manter uma lista de mensagens em formato string.
     * Você pode, por exemplo, usar um ArrayList().
     * Não se esqueça que em uma fila, o primeiro elemente a entrar será o primeiro
     * a ser removido.
     */
    public void AddMessage(String message){
        /* Adicione a mensagem no final da fila. Não se esqueça de garantir que apenas uma thread faça isso
        por vez. */
        try {
            lock.acquire();
            deque.addLast(message);
        } catch (InterruptedException ex) {
            Logger.getLogger(MessageQueue.class.getName()).log(Level.SEVERE, null, ex);
        }

        lock.release();
    }

    public String RemoveMessage(){
        /* Retive uma mensagem do inicio da fila. Não se esqueça de garantir que apenas uma thread faça isso
        por vez.  */
        String message = new String();

        try {
            lock.acquire();
            message = deque.removeFirst();
        } catch (InterruptedException ex) {
            Logger.getLogger(MessageQueue.class.getName()).log(Level.SEVERE, null, ex);
        }

        lock.release();

        return message;
    }
}

