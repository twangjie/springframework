package telnet.executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.telnet.*;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import telnet.TelnetWebSocketHandlerDecoratorFactory;
import telnet.domain.Device;
import telnet.model.TelnetResponse;
import telnet.service.DeviceService;

import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by 王杰 on 2017/7/4.
 */
public class TelnetExecutor {

    private static final Log logger = LogFactory.getLog(TelnetExecutor.class);

    private String termType = "VT100";
    private Integer printCRLFTimes = 5;

    private String sessionId;
    private String userId;
    private Device device;
    private TelnetClient telnet = null;
    private InputStream in;
    private PrintStream out;

    private DeviceService deviceService;

    private SimpMessageSendingOperations simpMessageSendingOperations;

    private AtomicBoolean connected = new AtomicBoolean(false);
    private AtomicLong responseCount = new AtomicLong(0);

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

            telnet = new TelnetClient(termType);

            telnet.addOptionHandler(new TerminalTypeOptionHandler(
                    termType, false, false, false, false));

            telnet.addOptionHandler(new EchoOptionHandler(true, true, true,
                    false));
            telnet.addOptionHandler(new SuppressGAOptionHandler(true, true,
                    true, true));

//            telnet.addOptionHandler(new SimpleOptionHandler(TelnetOption.BINARY, false, false, false, false));

            logger.info(String.format("[%s]Connecting %s:%s", device.toString(), device.getIp(), device.getPort()));

            telnet.setCharset(Charset.forName("utf-8"));
            telnet.setDefaultTimeout(300000);
            telnet.connect(device.getIp(), Integer.parseInt(device.getPort()));
            telnet.setKeepAlive(false);
            telnet.setTcpNoDelay(true);

            logger.info(String.format("[%s]Connected %s:%s", device.toString(), device.getIp(), device.getPort()));
            connected.set(true);

            in = telnet.getInputStream();
            out = new PrintStream(telnet.getOutputStream());

            readResponseThread = new ReadResponseThread();
            telnet.registerNotifHandler(readResponseThread);

            readResponseThread.start();

            deviceService.updateStatus(device.getId(), Device.CONNECTED);

            if (true) {
                int waitLoop = 30;
                boolean gotResponse = false;
                while (waitLoop > 0) {
                    if (responseCount.get() > 0) {
                        gotResponse = true;
                        logger.info("Received welcome message..");
                        break;
                    }

                    waitLoop--;
                    Thread.sleep(100);
                }

                if (!gotResponse) {
                    for(int i = 0; i < printCRLFTimes; i++) {
                        out.print("\r\n");
                        out.flush();
                    }
                }
            } else {
                Thread.sleep(200);
                out.print("\r\n");
                out.flush();
            }

            logger.info(String.format("[%s]Received %d messages before print enter", device.toString(), responseCount.get()));

            if (responseCount.get() == 0) {
                logger.info(String.format("[%s]print enter", device.toString()));
            }

        } catch (Exception ex) {

            connected.set(false);

            ex.printStackTrace();
            logger.error(ex.getMessage(), ex);

            forceReleaseSession(String.format("[%s]Server replied nothing...", device.toString()));

            deviceService.updateStatus(device.getId(), Device.CONNECTFAILED);

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

        String reply = "";
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

            reply = sb.toString();

            // https://stackoverflow.com/questions/25189651/how-to-remove-ansi-control-chars-vt100-from-a-java-string
            if (reply.indexOf("\u001B") >= 0) {
                reply = reply.replaceAll("\u001B\\[[\\d;]*[^\\d;]", "");
            }

            if (reply.indexOf("\\e") >= 0) {
                reply = reply.replaceAll("\\e\\[[\\d;]*[^\\d;]", "");  // \e matches escape character
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception while reading socket:" + e.getMessage());
            connected.set(false);
        }

        return reply;
    }

    /**
     * 写
     *
     * @param value
     */
    private void write(String value) {
        try {

            if (responseCount.get() == 0) {
                forceReleaseSession(String.format("[%s]Server replied nothing...", device.toString()));
                return;
            }

            if (!telnet.isConnected() || !telnet.isAvailable()) {
                //disconnect();
                //connect();
                forceReleaseSession("");
            }

            if (value.length() == 1 && value.charAt(0) == '\n') {
                value = "\r\n";
            }

            out.print(value);
            out.flush();
            if (out.checkError()) {
                forceReleaseSession("");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getMessage(), ex);
            connected.set(false);
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

    /**
     * 关闭连接
     */
    public void disconnect() {
        try {

            logger.info(String.format("[%s] disconnect", device.toString()));

            telnet.disconnect();
            if (connected.get()) {
                deviceService.updateStatus(device.getId(), Device.DISCONNECTED);
            }

            connected.set(false);

        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getMessage(), ex);
            forceReleaseSession("");
        }
    }

    public void setSimpMessageSendingOperations(SimpMessageSendingOperations simpMessageSendingOperations) {
        this.simpMessageSendingOperations = simpMessageSendingOperations;
    }

    private void sendResponse(String reply) {
        TelnetResponse telnetResponse = new TelnetResponse();
        telnetResponse.setUserId(userId);
        telnetResponse.setContent(reply);
        simpMessageSendingOperations.convertAndSendToUser(userId, "/telnet/cmdresp", telnetResponse);
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    private void forceReleaseSession(String reason) {

        if (reason != null && !reason.isEmpty()) {
            sendResponse(reason);
        }

        disconnect();
        TelnetWebSocketHandlerDecoratorFactory.getInstance().closeSession(sessionId);
        TelnetExecutorFactory.getInstance().releaseTelnetExecutor(sessionId);
    }

    public void setTermType(String termType) {
        this.termType = termType;
    }

    public Integer getPrintCRLFTimes() {
        return printCRLFTimes;
    }

    public void setPrintCRLFTimes(Integer printCRLFTimes) {
        this.printCRLFTimes = printCRLFTimes;
    }

    class ReadResponseThread extends Thread implements TelnetNotificationHandler {

        private long lastReplyTime = System.currentTimeMillis();

        public ReadResponseThread() {

        }


        @Override
        public void run() {

            while (connected.get()) {

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

                        responseCount.incrementAndGet();

                        String reply = sb.toString();

                        // https://stackoverflow.com/questions/25189651/how-to-remove-ansi-control-chars-vt100-from-a-java-string
                        if (reply.indexOf("\u001B") >= 0) {
                            reply = reply.replaceAll("\u001B\\[[\\d;]*[^\\d;]", "");
                        }

                        if (reply.indexOf("\\e") >= 0) {
                            reply = reply.replaceAll("\\e\\[[\\d;]*[^\\d;]", "");  // \e matches escape character
                        }

                        sendResponse(reply);

                        lastReplyTime = System.currentTimeMillis();
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    logger.error(String.format("[%s]Exception while reading socket: ", device.toString(), ex.getMessage()));

                    forceReleaseSession("");

                    break;
                }

                if (System.currentTimeMillis() - lastReplyTime > 3600 * 1000) {
                    forceReleaseSession(String.format("[%s]Session timeout", device.toString()));
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
