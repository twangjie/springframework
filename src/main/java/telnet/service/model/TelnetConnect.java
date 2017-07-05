package telnet.service.model;

/**
 * Created by 王杰 on 2017/7/4.
 */
public class TelnetConnect {

    private String userId;
    private String ipaddr;
    private int port;

    public TelnetConnect() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getIpaddr() {
        return ipaddr;
    }

    public void setIpaddr(String ipaddr) {
        this.ipaddr = ipaddr;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
