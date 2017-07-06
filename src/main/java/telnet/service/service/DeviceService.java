package telnet.service.service;

import telnet.service.domain.Device;

import java.util.List;

/**
 * Created by 王杰 on 2017/07/04.
 */
public interface DeviceService {

    List<Device> getDevice(String ip, String port);

    List<Device> getDevices();

    Device save(Device device);

    void delete(Device device);

    Long deleteById(Long id);

    Long updateStatus(Long id, Short status);
}
