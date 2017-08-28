package telnet.service;

import org.apache.catalina.connector.Connector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    private static final Log logger = LogFactory.getLog(Application.class);

    // application.properties配置中的 local.server.port
    @LocalServerPort
    private int port = 12345;

    @Autowired
    private TelnetSettings telnetSettings;

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);
    }

    /**
     * 修改tomcat默认配置
     *
     * @return
     */
    @Bean
    public TomcatEmbeddedServletContainerFactory servletContainer() {
        TomcatEmbeddedServletContainerFactory container = new TomcatEmbeddedServletContainerFactory();
        container.setPort(port);
        container.addConnectorCustomizers(new MyTomcatConnectorCustomizer());

        return container;
    }

    class MyTomcatConnectorCustomizer implements TomcatConnectorCustomizer {
        public void customize(Connector connector) {
            Http11NioProtocol http11NioProtocol = (Http11NioProtocol) connector.getProtocolHandler();

            //设置最大连接数
            http11NioProtocol.setMaxConnections(5000);
            logger.info("http11NioProtocol.setMaxConnections(5000)");

            //设置初始线程数  最小空闲线程数
            http11NioProtocol.setMinSpareThreads(100);
            logger.info("http11NioProtocol.setMinSpareThreads(100)");

            //设置最大线程数
            http11NioProtocol.setMaxThreads(500);
            logger.info("http11NioProtocol.setMaxThreads(500)");

            //设置超时
            http11NioProtocol.setConnectionTimeout(30000);
            logger.info("http11NioProtocol.setConnectionTimeout(30000)");

            http11NioProtocol.setKeepAliveTimeout(0);
            logger.info("http11NioProtocol.setKeepAliveTimeout(0)");
        }
    }
}
