package telnet.service.service.impl;

import org.springframework.stereotype.Component;
import telnet.service.domain.Device;
import telnet.service.model.DeviceRepository;
import telnet.service.service.DeviceService;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by 王杰 on 2017/07/04.
 */

@Component("deviceService")
@Transactional
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;

    public DeviceServiceImpl(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Override
    public List<Device> getDevice(String ip, String port) {
        return deviceRepository.findByIpAndPort(ip, port);
    }

    @Override
    public List<Device> getDevices() {
        return deviceRepository.findAll();
    }

    @Override
    public Device save(Device device) {
        return deviceRepository.save(device);
    }

    @Override
    public void delete(Device device) {
        deviceRepository.delete(device);
    }

    @Override
    public Long deleteById(Long id) {
        return deviceRepository.deleteById(id);
    }

    @Override
    public Long updateStatus(Long id, Short status) {
        Device device = deviceRepository.findOne(id);
        if(device != null) {
            device.setStatus(status);
            deviceRepository.save(device);
        }

        return 0L;
    }
}
