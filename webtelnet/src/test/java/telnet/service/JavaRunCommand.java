package telnet.service;

import java.io.*;

public class JavaRunCommand {

    public static void main(String args[]) {

        String s = null;

        try {

            String[] cmd = {
                    "telnet",
                    "192.168.35.102",
                    "23"
            };

            // Execute command
            String command = "cmd.exe";
            final Process child = Runtime.getRuntime().exec(cmd);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    BufferedReader stdInput = new BufferedReader(new
                            InputStreamReader(child.getInputStream()));
                    String s;
                    try {
                        while ((s = stdInput.readLine()) != null) {
                            System.out.println(s);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            Thread.sleep(2000);

            // Get output stream to write from it
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(child.getOutputStream()));

            out.write("dir");
            out.newLine();
            out.flush();

            Thread.sleep(2000);

            out.write("telnet 192.168.37.102 23");
            out.newLine();
            out.flush();

            Thread.sleep(10000);

        } catch (Exception e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
