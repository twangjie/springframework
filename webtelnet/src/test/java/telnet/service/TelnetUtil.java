package telnet.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.telnet.TelnetClient;

/**
 * Created by 王杰 on 2017/7/4.
 */
public class TelnetUtil {

    private static final Log logger = LogFactory.getLog(TelnetUtil.class);

    private String remoteip;
    private int remoteport;

    private TelnetClient tc = null;
    private InputStream inputStream = null;
    private OutputStream outputStream = null;


    public TelnetUtil(String ip) {
        this(ip, 23, null);
    }

    public TelnetUtil(String ip, int port) {
        this(ip, port, null);
    }

    public TelnetUtil(String ip, String spyFile) {
        this(ip, 23, spyFile);
    }

    /**
     * 最终构造方法
     *
     * @param ip
     * @param port
     * @param spyFile
     */
    public TelnetUtil(String ip, int port, String spyFile) {
        remoteip = ip;
        remoteport = port;

        initClient(spyFile);
    }

    private void initClient(String spyFile) {


        tc = new TelnetClient();

//        TerminalTypeOptionHandler ttopt = new TerminalTypeOptionHandler(
//                "VT220", false, false, true, false);
//        EchoOptionHandler echoopt = new EchoOptionHandler(true, false, true,
//                false);
//        SuppressGAOptionHandler gaopt = new SuppressGAOptionHandler(true, true,
//                true, true);

//        try {
////            tc.addOptionHandler(ttopt);
////            tc.addOptionHandler(echoopt);
////            tc.addOptionHandler(gaopt);
//
//            if (null != spyFile && !"".equals(spyFile)) {
//                FileOutputStream fout = null;
//                try {
//                    fout = new FileOutputStream("spy.log", true);
//                    tc.registerSpyStream(fout);
//                } catch (IOException e) {
//                    logger.error("Exception while opening the spy file: " + e.getMessage());
//                }
//            }
//        } catch (Exception e) {
//            logger.error(e);
//        }

    }

    public String connect(long waitTime) throws Exception {
        try {
            tc.connect(remoteip, remoteport);
            inputStream = tc.getInputStream();
            outputStream = tc.getOutputStream();

//            Thread reader = new Thread(this);
//            tc.registerNotifHandler(this);
//            reader.start();

            return getResponse();
        } catch (Exception e) {
            throw new Exception("telnet 连接失败", e);
        }
    }

    public void disConnect() {
        try {
            if (tc != null && tc.isConnected())
                tc.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception when close connecting:" + e.getMessage());
        }

    }

    /**
     * 发送命令
     *
     * @param command 命令
     * @return 执行结果
     * @throws Exception
     */
    public String sendCommand(String command) throws Exception {


        try {

            if (command.equals(" ")) {
                outputStream.write(' ');
            } else {
                outputStream.write(command.getBytes());
                outputStream.write(13);
                outputStream.write(10);
            }
            outputStream.flush();

//            Thread.sleep(100);

        } catch (Exception e) {
            throw new Exception("telnet 发送命令[" + command + "]失败", e);
        }

        return getResponse();

    }

    /**
     * 获取命令返回
     *
     * @return
     */
    public String getResponse() {

        String response = "";
        StringBuilder sb = new StringBuilder();
        try {

            Thread.sleep(500);

            byte[] buff = new byte[4096];

            int available = 0;
            while ((available = inputStream.available()) > 0) {
                int ret = inputStream.read(buff);
                if (ret > 0) {
                    sb.append(new String(buff, 0, ret));
                }
            }

            response = sb.toString();

        } catch (Exception e) {

            logger.error("Exception while reading socket:" + e.getMessage());
        }

        return response;
    }

    public static void main(String[] args) throws Exception {

        TelnetUtil util = new TelnetUtil("172.16.79.120", 10252);
        String welcome = util.connect(2000);
        if (welcome.contains("Microsoft")) {
            System.out.println("widows opreation");
        } else {
            System.out.println("linux opreation");
        }
        System.out.println(welcome);


//        System.out.println(util.sendCommand("admin"));
//        System.out.println(util.sendCommand("admin"));
//        System.out.println(util.sendCommand("help"));
//        System.out.println(util.sendCommand("?"));

        String cmd = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        do {
            cmd = br.readLine();
            if (cmd.isEmpty()) {
                cmd = " ";
            }
            System.out.println(util.sendCommand(cmd));

        } while (!cmd.equals("exit"));

        util.disConnect();
    }
}
