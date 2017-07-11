package telnet.service.executor;

import java.io.InputStream;
import java.io.PrintStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.telnet.TelnetClient;
import telnet.service.domain.Device;
import telnet.service.service.DeviceService;

/**
 * Created by 王杰 on 2017/7/4.
 */
public class TelnetExecutor {

    private static final Log logger = LogFactory.getLog(TelnetExecutor.class);

    private String userId;
    private Device device;
    private TelnetClient telnet = new TelnetClient();
    private InputStream in;
    private PrintStream out;

    private DeviceService deviceService;

    private boolean connected = false;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }


    public void setDeviceService(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    public boolean connect() {
        try {
            telnet.connect(device.getIp(), Integer.parseInt(device.getPort()));
            in = telnet.getInputStream();
            out = new PrintStream(telnet.getOutputStream());

            deviceService.updateStatus(device.getId(), Device.CONNECTED);
            connected = true;

        }catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getMessage(), ex);

            deviceService.updateStatus(device.getId(), Device.UNKNOWN);

            return false;
        }

        return true;
    }

    /**
     * 读取结果
     *
     * @param
     * @return
     */
    public String readResponse() {

        if(in == null) {
            return "";
        }

        String response = "";
        StringBuilder sb = new StringBuilder();

        try {

            Thread.sleep(500);

            byte[] buff = new byte[4096];

            int available = 0;
            while ((available = in.available()) > 0) {
                int ret = in.read(buff);
                if (ret > 0) {
                    sb.append(new String(buff, 0, ret));
                }
            }

            response = sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception while reading socket:" + e.getMessage());
        }

        return response;
    }
    /**
     * 写
     *
     * @param value
     */
    public void write(String value) {
        try {
            if (value.equals(" \r\n")) {
                out.print(' ');
            } else {
                out.print(value);
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void write(char value) {
        try {
            out.write(value);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 向目标发送命令字符串
     *
     * @param command
     * @return
     */
    public String sendCommand(String command) {
        try {
            write(command);
            return readResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String sendCommand(char command) {
        try {
            write(command);
            return readResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 关闭连接
     *
     */
    public void disconnect() {
        try {
            telnet.disconnect();
            if(connected) {
                deviceService.updateStatus(device.getId(), Device.DISCONNECTED);
            }else{
                deviceService.updateStatus(device.getId(), Device.UNKNOWN);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
