package TokenRing;

public class NetworkMessage {
    private String origin;
    private String destination;
    private String content;

    public NetworkMessage(String ori, String dest, String cont){
        this.origin = ori;
        this.destination = dest;
        this.content = cont.trim();
    }

    public String getContent(){
        return this.content;
    }
}
