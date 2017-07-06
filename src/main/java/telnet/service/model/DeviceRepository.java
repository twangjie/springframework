package telnet.service.model;

import org.springframework.data.repository.Repository;
import telnet.service.domain.Device;

import java.util.List;

/**
 * Created by 王杰 on 2017/07/04.
 */
public interface DeviceRepository extends Repository<Device, Long> {

    Device findOne(Long id);

    List<Device> findByIpAndPort(String ip, String port);

    List<Device> findAll();

    Device save(Device device);

    void delete(Device device);

    Long deleteById(Long id);

}
