package telnet.model;

public class EchoMessage {

    private String message;

    public EchoMessage() {
    }

    public EchoMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
