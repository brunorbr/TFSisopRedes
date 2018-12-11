package TokenRing;


import java.util.LinkedList;
import java.util.Deque;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageQueue {
    Deque<String> deque;
    static Semaphore lock = new Semaphore(1);

    public MessageQueue(){
        this.deque = new LinkedList<>();

    }

    public Boolean isEmpty() {
        return this.deque.isEmpty();
    }

    public void AddMessage(String message){
        try {
            lock.acquire();
            deque.addLast(message);
        } catch (InterruptedException ex) {
            Logger.getLogger(MessageQueue.class.getName()).log(Level.SEVERE, null, ex);
        }

        lock.release();
    }

    public String RemoveMessage(){
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

