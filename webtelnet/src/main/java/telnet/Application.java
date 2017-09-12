package telnet;

import org.apache.catalina.connector.Connector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
@ComponentScan
public class Application {

    private static final Log logger = LogFactory.getLog(Application.class);

    // application.properties配置中的 local.server.port

//    @Autowired
//    private TelnetSettings telnetSettings;

    @Autowired
    private TomcatConnectorProperties tomcatConnectorProperties;

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);
    }

    /**
     * 修改tomcat默认配置
     *
     * @return
     */
    @Bean
    public TomcatEmbeddedServletContainerFactory servletContainer() throws IOException {
        TomcatEmbeddedServletContainerFactory container = new TomcatEmbeddedServletContainerFactory();
        container.setPort(tomcatConnectorProperties.getHttpPort());
        try {
            String ipAddr = tomcatConnectorProperties.getIp();
            if (ipAddr != null && !ipAddr.isEmpty() && ipAddr.compareTo("0.0.0.0") != 0) {
                logger.info("Bind to " + ipAddr);
                container.setAddress(InetAddress.getByName(ipAddr));
            } else {
                logger.info("Bind to 0.0.0.0");
                ipAddr = "0.0.0.0";
            }

            container.setAddress(InetAddress.getByName(ipAddr));

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        container.addConnectorCustomizers(new MyTomcatConnectorCustomizer());

        if (tomcatConnectorProperties.isSslEnabled()) {
            container.addAdditionalTomcatConnectors(httpsConnector());
        }

        return container;
    }

    @Bean
    public Connector httpsConnector() throws IOException {

        // 启动类这块加上了一个httpConnector，为了支持https访问和http访问
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");

        Http11NioProtocol http11NioProtocol = (Http11NioProtocol) connector.getProtocolHandler();

        tomcatConnectorProperties.configureHttpsConnector(connector);

        //设置最大连接数
        http11NioProtocol.setMaxConnections(tomcatConnectorProperties.getMaxConnections());
        logger.info("http11NioProtocol.setMaxConnections: " + tomcatConnectorProperties.getMaxConnections());

        //设置初始线程数  最小空闲线程数
        http11NioProtocol.setMinSpareThreads(tomcatConnectorProperties.getMinSpareThreads());
        logger.info("http11NioProtocol.setMinSpareThreads: " + tomcatConnectorProperties.getMinSpareThreads());

        //设置最大线程数
        http11NioProtocol.setMaxThreads(tomcatConnectorProperties.getMaxThreads());
        logger.info("http11NioProtocol.setMaxThreads: " + tomcatConnectorProperties.getMaxThreads());

        //设置超时
        http11NioProtocol.setConnectionTimeout(tomcatConnectorProperties.getConnectionTimeout());
        logger.info("http11NioProtocol.setConnectionTimeout: " + tomcatConnectorProperties.getConnectionTimeout());

        http11NioProtocol.setKeepAliveTimeout(tomcatConnectorProperties.getKeepAliveTimeout());
        logger.info("http11NioProtocol.setKeepAliveTimeout: " + tomcatConnectorProperties.getKeepAliveTimeout());

        return connector;
    }

    class MyTomcatConnectorCustomizer implements TomcatConnectorCustomizer {
        public void customize(Connector connector) {
            Http11NioProtocol http11NioProtocol = (Http11NioProtocol) connector.getProtocolHandler();

            //设置最大连接数
            http11NioProtocol.setMaxConnections(tomcatConnectorProperties.getMaxConnections());
            logger.info("http11NioProtocol.setMaxConnections: " + tomcatConnectorProperties.getMaxConnections());

            //设置初始线程数  最小空闲线程数
            http11NioProtocol.setMinSpareThreads(tomcatConnectorProperties.getMinSpareThreads());
            logger.info("http11NioProtocol.setMinSpareThreads: " + tomcatConnectorProperties.getMinSpareThreads());

            //设置最大线程数
            http11NioProtocol.setMaxThreads(tomcatConnectorProperties.getMaxThreads());
            logger.info("http11NioProtocol.setMaxThreads: " + tomcatConnectorProperties.getMaxThreads());

            //设置超时
            http11NioProtocol.setConnectionTimeout(tomcatConnectorProperties.getConnectionTimeout());
            logger.info("http11NioProtocol.setConnectionTimeout: " + tomcatConnectorProperties.getConnectionTimeout());

            http11NioProtocol.setKeepAliveTimeout(tomcatConnectorProperties.getKeepAliveTimeout());
            logger.info("http11NioProtocol.setKeepAliveTimeout: " + tomcatConnectorProperties.getKeepAliveTimeout());
        }
    }
}
