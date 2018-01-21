package test.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by 王杰 on 2017/07/04.
 */
public interface VgwIdMapRepository extends JpaRepository<VgwIdMap, VgwIdMapPK> {


    List<VgwIdMap> findAll();

    List<VgwIdMap> findAllByType(Integer type);

    VgwIdMap save(VgwIdMap device);

    void delete(VgwIdMap device);
}
