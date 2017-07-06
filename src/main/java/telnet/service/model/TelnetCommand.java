package telnet.service.model;

public class TelnetCommand {

    private int deviceId;
    private String userId;
    private String content;

    public TelnetCommand() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public TelnetCommand(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }


    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }
}
