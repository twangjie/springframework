package telnet;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;

/**
 * Created by 王杰 on 2017/8/24.
 */

@ConfigurationProperties(prefix = "custom.tomcat")
public class TomcatConnectorProperties {

    private String ip;
    private int maxConnections = 5000;
    private int minSpareThreads = 100;
    private int maxThreads = 500;
    private int connectionTimeout = 30000;
    private int keepAliveTimeout = 0;

    private Integer httpPort;

    private Integer httpsPort;

    private Boolean httpsEnabled;

    private Boolean httpsSecure;

    private String httpsScheme;

    private File httpsKeystore;

    private File httpsKeyalias;

    private String httpsKeystorepassword;

    public void configureHttpsConnector(Connector connector) {

        if (httpsEnabled != null) {
            connector.setAttribute("SSLEnabled", httpsEnabled.toString());
        }

        if (httpsPort != null) {
            connector.setPort(httpsPort);
        }
        if (httpsSecure != null) {
            connector.setSecure(httpsSecure);
        }
        if (httpsScheme != null) {
            connector.setScheme(httpsScheme);
        }

        if (httpsKeystore != null) {
            connector.setAttribute("keystoreFile", httpsKeystore.getAbsolutePath());
            connector.setAttribute("keystorePass", httpsKeystorepassword);
        }

//        connector.setAttribute("SSLEnabled", true);
//        connector.setAttribute("sslProtocol", "TLS");
//        connector.setAttribute("protocol", "org.apache.coyote.http11.Http11Protocol");
//        connector.setAttribute("clientAuth", false);
//        connector.setAttribute("keystoreFile", absoluteKeystoreFile);
//        connector.setAttribute("keystoreType", keystoreType);
//        connector.setAttribute("keystorePass", keystorePassword);
//        connector.setAttribute("keystoreAlias", keystoreAlias);
//        connector.setAttribute("keyPass", keystorePassword);
    }


    public Integer getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(Integer httpPort) {
        this.httpPort = httpPort;
    }

    public void setHttpsPort(Integer httpsPort) {
        this.httpsPort = httpsPort;
    }

    public void setHttpsKeystore(File httpsKeystore) {
        this.httpsKeystore = httpsKeystore;
    }

    public void setHttpsScheme(String httpsScheme) {
        this.httpsScheme = httpsScheme;
    }

    public void setHttpsSecure(Boolean httpsSecure) {
        this.httpsSecure = httpsSecure;
    }

    public Boolean isSslEnabled() {
        return httpsEnabled;
    }

    public void setHttpsEnabled(Boolean ssl) {
        this.httpsEnabled = ssl;
    }

    public File getHttpsKeyalias() {
        return httpsKeyalias;
    }

    public void setHttpsKeyalias(File httpsKeyalias) {
        this.httpsKeyalias = httpsKeyalias;
    }

    public void setHttpsKeystorepassword(String httpsKeystorepassword) {
        this.httpsKeystorepassword = httpsKeystorepassword;
    }


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public int getMinSpareThreads() {
        return minSpareThreads;
    }

    public void setMinSpareThreads(int minSpareThreads) {
        this.minSpareThreads = minSpareThreads;
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getKeepAliveTimeout() {
        return keepAliveTimeout;
    }

    public void setKeepAliveTimeout(int keepAliveTimeout) {
        this.keepAliveTimeout = keepAliveTimeout;
    }


}