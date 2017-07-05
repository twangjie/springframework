package telnet.service.service;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import telnet.service.domain.Device;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by 王杰 on 2017/07/04.
 */
public interface DeviceRepository extends Repository<Device, Long> {

    List<Device> findByIpAndPort(String ip, Long port);

    List<Device> findAll();

    Device save(Device device);

    void delete(Device device);

    Long deleteById(Long id);
}
