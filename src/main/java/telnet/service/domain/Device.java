package telnet.service.domain;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by 王杰 on 2017/07/04.
 */
@Entity(name = "moxa")
@Table(name = "moxa")
public class Device implements Serializable {

    public  enum Status {
        ZERO,
        CONNECTED,
        DISCONNECTED,
        UNKNOWN
    }

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
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
    private Short status = 3;

    public Device() {

    }

    public Device(String ip,String port,String name, String address, String info, Short status) {
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

    @Override
    public String toString() {
        return getIp() + ":" + getPort();
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
}
