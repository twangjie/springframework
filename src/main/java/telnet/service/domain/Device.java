package telnet.service.domain;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by 王杰 on 2017/07/04.
 */
@Entity(name = "moxa")
@Table(name = "moxa")
public class Device implements Serializable {

    // 状态 1-连接 2-断开 3-未知,
    public static final short CONNECTED = 1;
    public static final short DISCONNECTED = 2;
    public static final short UNKNOWN = 3;
    public static final short CONNECTFAILED = 4;

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String ip;

    @Column(nullable = false)
    private String port = "23";

    @Column(nullable = false)
    private String name;

    @Column
    private String address;

    @Column
    private String info;

    @Column
    private Short status = DISCONNECTED;

    private Long connectFailedCount = new Long(0);

    public Device() {

    }

    public Device(String ip, String port, String name, String address, String info, Short status) {
        this.ip = ip;
        this.port = port;
        this.name = name;
        this.address = address;
        this.info = info;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return getIp() + ":" + getPort();
    }

    public long increaseConnectFailedCount() {
        if(connectFailedCount == null) {
            connectFailedCount = new Long(0);
        }
        return this.connectFailedCount++;
    }

    public void resetConnectFailedCount() {
        if(connectFailedCount == null) {
            connectFailedCount = new Long(0);
        }
        connectFailedCount = 0L;
    }

    public boolean isUnknownStatus() {
        if(connectFailedCount == null) {
            connectFailedCount = new Long(0);
            return false;
        }

        return this.connectFailedCount > 3;
    }
}
