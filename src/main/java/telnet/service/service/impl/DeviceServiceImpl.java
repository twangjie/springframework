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
    private List<Device> deviceList;

    public DeviceServiceImpl(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
        deviceList = deviceRepository.findAll();
//        for (Device d : deviceList) {
//            d.setStatus(Device.DISCONNECTED);
//            deviceRepository.save(d);
//        }
    }

    @Override
    public Device findById(Long id) {

        Device ret = findByIdFromDeviceList(id);
        if (ret != null) {
            return ret;
        }

        return deviceRepository.findOne(id);
    }

    @Override
    public List<Device> getDevice(String ip, String port) {
        return deviceRepository.findByIpAndPort(ip, port);
    }

    @Override
    public List<Device> getDevices() {

        if (deviceList != null) {
            return deviceList;
        }

        return deviceRepository.findAll();
    }

    @Override
    public Device save(Device device) {
        updateDeviceList(device);
        return deviceRepository.save(device);
    }

    @Override
    public void delete(Device device) {
        deleteFromDeviceList(device.getId());
        deviceRepository.delete(device);
    }

    @Override
    public Long deleteById(Long id) {
        deleteFromDeviceList(id);
        return deviceRepository.deleteById(id);
    }

    @Override
    public Long updateStatus(Long id, Short status) {

        Device device = findByIdFromDeviceList(id);

        if (status == Device.CONNECTFAILED) {
            long failedCount = device.increaseConnectFailedCount();
            if (failedCount >= 3) {
                device.setStatus(Device.UNKNOWN);
            } else {
                device.setStatus(Device.DISCONNECTED);
            }
        } else {
            device.resetConnectFailedCount();
        }

        updateDeviceList(device);

        if (device != null) {
            device.setStatus(status);
            deviceRepository.save(device);
        }

        return 1L;
    }

    private Device findByIdFromDeviceList(Long id) {
        for (int i = 0; i < deviceList.size(); i++) {
            Device dev = deviceList.get(i);
            if (dev.getId() == id) {
                return dev;
            }
        }

        return null;
    }

    private void updateDeviceList(Device device) {
        boolean found = false;

        for (int i = 0; i < deviceList.size(); i++) {
            Device dev = deviceList.get(i);
            if (dev.getId() == device.getId()) {
                deviceList.set(i, device);
                return;
            }
        }

        if (!found) {
            deviceList.add(device);
        }
    }

    private void deleteFromDeviceList(Long id) {

        for (int i = 0; i < deviceList.size(); i++) {
            Device dev = deviceList.get(i);
            if (dev.getId() == id) {
                deviceList.remove(i);
                break;
            }
        }
    }
}
