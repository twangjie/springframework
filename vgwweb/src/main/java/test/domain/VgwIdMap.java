package test.domain;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "vgw_id_map")
@Table(name = "vgw_id_map")
@IdClass(VgwIdMapPK.class)
public class VgwIdMap implements Serializable {

    @Id
    @Column(nullable = false)
    private String guid;

    @Id
    @Column(nullable = false)
    private Integer type;

    @Id
    @Column(nullable = false)
    private Integer id;

    @Column(nullable = false)
    private String gbid;


    public String getGbid() {
        return gbid;
    }

    public void setGbid(String gbid) {
        this.gbid = gbid;
    }


    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
