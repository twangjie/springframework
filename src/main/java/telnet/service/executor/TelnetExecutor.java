package telnet.service.executor;

import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.telnet.*;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import telnet.service.domain.Device;
import telnet.service.model.TelnetResponse;
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

    private SimpMessageSendingOperations simpMessageSendingOperations;

    private boolean connected = false;

    private ReadResponseThread readResponseThread;

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

            telnet.addOptionHandler(new TerminalTypeOptionHandler(
                    "VT100", false, false, true, false));

            telnet.addOptionHandler(new EchoOptionHandler(true, false, true,
                    false));
            telnet.addOptionHandler(new SuppressGAOptionHandler(true, true,
                    true, true));

            telnet.setCharset(Charset.forName("utf-8"));
            telnet.connect(device.getIp(), Integer.parseInt(device.getPort()));
            telnet.setKeepAlive(true);
            telnet.setTcpNoDelay(true);

            connected = true;

            in = telnet.getInputStream();
            out = new PrintStream(telnet.getOutputStream());

            readResponseThread = new ReadResponseThread();
            telnet.registerNotifHandler(readResponseThread);

            readResponseThread.start();

            deviceService.updateStatus(device.getId(), Device.CONNECTED);

        } catch (Exception ex) {
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

        if (in == null) {
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

            // https://stackoverflow.com/questions/25189651/how-to-remove-ansi-control-chars-vt100-from-a-java-string
            // reply = reply.replaceAll("\u001B\\[[\\d;]*[^\\d;]","");
            response = response.replaceAll("\\e\\[[\\d;]*[^\\d;]","");  // \e matches escape character

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
    private void write(String value) {
        try {

            if (!telnet.isConnected() || !telnet.isAvailable()) {
                disconnect();
                connect();
            }

            if (value.equals(" \r\n")) {
                out.print(' ');
            } else {
                out.print(value);
            }
            out.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getMessage(), ex);
        }
    }

    private void write(char value) {
        try {
            out.write(value);
            out.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getMessage(), ex);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String sendCommand(char command) {
        try {
            write(command);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getMessage(), ex);
        }
        return null;
    }


    /**
     * 关闭连接
     */
    public void disconnect() {
        try {
            telnet.disconnect();
            if (connected) {
                deviceService.updateStatus(device.getId(), Device.DISCONNECTED);
            } else {
                deviceService.updateStatus(device.getId(), Device.UNKNOWN);
            }

            connected = false;

        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getMessage(), ex);
        }
    }

    public void setSimpMessageSendingOperations(SimpMessageSendingOperations simpMessageSendingOperations) {
        this.simpMessageSendingOperations = simpMessageSendingOperations;
    }

    class ReadResponseThread extends Thread implements TelnetNotificationHandler {

        public ReadResponseThread() {

        }


        @Override
        public void run() {

            while (connected) {

                if (in == null) {
                    break;
                }

                String response = "";
                StringBuilder sb = new StringBuilder();

                try {

                    byte[] buff = new byte[4096];

                    int available = 0;
                    while ((available = in.available()) > 0) {
                        int ret = in.read(buff);
                        if (ret > 0) {
                            sb.append(new String(buff, 0, ret));
                        }
                    }

                    if (sb.length() > 0) {

                        String reply = sb.toString();

                        // https://stackoverflow.com/questions/25189651/how-to-remove-ansi-control-chars-vt100-from-a-java-string
                        // reply = reply.replaceAll("\u001B\\[[\\d;]*[^\\d;]","");
                        reply = reply.replaceAll("\\e\\[[\\d;]*[^\\d;]","");  // \e matches escape character

                        TelnetResponse telnetResponse = new TelnetResponse();
                        telnetResponse.setUserId(userId);
                        telnetResponse.setContent(reply);
                        simpMessageSendingOperations.convertAndSendToUser(userId, "/telnet/cmdresp", telnetResponse);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    logger.error("Exception while reading socket: " + ex.getMessage());

                    disconnect();
                    break;
                }

                try {
                    Thread.sleep(50);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void receivedNegotiation(int negotiation_code, int option_code) {

            String command = "";

            switch (negotiation_code) {
                case TelnetNotificationHandler.RECEIVED_DO:
                    command = "DO";
                    break;
                case TelnetNotificationHandler.RECEIVED_DONT:
                    command = "DONT";
                    break;
                case TelnetNotificationHandler.RECEIVED_WILL:
                    command = "WILL";
                    break;
                case TelnetNotificationHandler.RECEIVED_WONT:
                    command = "WONT";
                    break;
                default:
                    command = Integer.toString(negotiation_code);
                    break;
            }

            logger.debug("Received " + command + " for option code " + option_code);
        }
    }
}
