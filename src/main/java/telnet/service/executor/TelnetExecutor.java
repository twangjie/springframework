package telnet.service.executor;

import java.io.InputStream;
import java.io.PrintStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.telnet.TelnetClient;

/**
 * Created by 王杰 on 2017/7/4.
 */
public class TelnetExecutor {

    private static final Log logger = LogFactory.getLog(TelnetExecutor.class);

    private String userId;
    private String ip;
    private int port;
    private TelnetClient telnet = new TelnetClient();
    private InputStream in;
    private PrintStream out;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean connect() {
        try {
            telnet.connect(ip, port);
            in = telnet.getInputStream();
            out = new PrintStream(telnet.getOutputStream());

        }catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getMessage(), ex);

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

            if(value.equals(' ')){
                out.print(' ');
            }
            else{
                out.println(value);
            }

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
    /**
     * 关闭连接
     *
     */
    public void disconnect() {
        try {
            telnet.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
