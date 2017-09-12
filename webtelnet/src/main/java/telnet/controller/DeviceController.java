package telnet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import telnet.domain.Device;
import telnet.service.DeviceService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by 王杰 on 2017/07/04.
 */
@Controller
@ResponseBody
@RequestMapping("api")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @RequestMapping(path = "/device", method = RequestMethod.POST)
    public Device save(HttpServletRequest request, HttpServletResponse response, @RequestBody Device device) {
        return save2(request, response, device);
    }

    @RequestMapping(path = "/device", method = RequestMethod.PUT)
    public Device save2(HttpServletRequest request, HttpServletResponse response, @RequestBody Device device) {
        List<Device> list = deviceService.getDevice(device.getIp(), device.getPort());
        if (list == null || list.isEmpty()) {
            deviceService.save(device);
        }
        return deviceService.getDevice(device.getIp(), device.getPort()).get(0);
    }

    @RequestMapping(path = "/device", method = RequestMethod.DELETE)
    public void delete(HttpServletRequest request, HttpServletResponse response, @RequestParam(name = "id") Long id) {
        deviceService.deleteById(id);
    }

    @RequestMapping(path = "/device", method = RequestMethod.GET)
    public List<Device> list(HttpServletRequest request, HttpServletResponse response) {
        return deviceService.getDevices();
    }

    @RequestMapping(path = "/device/{id}", method = RequestMethod.GET)
    public Device findById(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") Long id) {
        return deviceService.findById(id);
    }

    @RequestMapping(path = "find", method = RequestMethod.POST)
    public List<Device> findByIpAndPort(HttpServletRequest request, HttpServletResponse response, @RequestBody Device device) {

        if (device == null || device.getIp() == null || device.getIp().isEmpty()) {
            return deviceService.getDevices();
        } else {
            return deviceService.getDevice(device.getIp(), device.getPort());
        }
    }
}
