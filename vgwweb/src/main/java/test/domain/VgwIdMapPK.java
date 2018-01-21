package test.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

//@Embeddable
public class VgwIdMapPK implements Serializable {

    private static final long serialVersionUID = -7189167162738318201L;

    @Column(nullable = false)
    private String guid;

    @Column(nullable = false)
    private Integer id;

    @Column(nullable = false)
    private Integer type;

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