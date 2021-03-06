package test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties
public class Application {

    //     application.properties配置中的 local.server.port
    @LocalServerPort
    private int port = 12345;

    /**
     * 修改tomcat默认配置
     *
     * @return
     */
    @Bean
    public EmbeddedServletContainerFactory createEmbeddedServletContainerFactory() throws IOException {

        TomcatEmbeddedServletContainerFactory container = new TomcatEmbeddedServletContainerFactory();
        container.setPort(port);

        return container;
    }

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);
    }

}
