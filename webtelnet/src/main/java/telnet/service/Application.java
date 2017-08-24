package telnet.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    // application.properties配置中的 local.server.port
    @LocalServerPort
    private int port = 12345;

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
//      container.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND,""));
        return container;
    }


}
