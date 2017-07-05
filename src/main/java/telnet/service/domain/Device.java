package telnet.service.domain;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by 王杰 on 2017/07/04.
 */
@Entity
public class Device implements Serializable {

    private static final long serialVersionUID = 1L;

    protected Device() {
    }

    public Device(String ip, Long port) {
        super();
        this.ip = ip;
        this.port = port;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String ip;

    @Column(nullable = false)
    private Long port = 23L;

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

    public Long getPort() {
        return port;
    }

    public void setPort(Long port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return getIp() + ":" + getPort();
    }


}
