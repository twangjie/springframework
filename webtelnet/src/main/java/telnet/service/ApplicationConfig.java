package telnet.service;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Created by 王杰 on 2017/7/6.
 */
@Configuration
@ComponentScan
@EnableJpaRepositories
@EnableConfigurationProperties({TelnetSettings.class})
public class ApplicationConfig {


}
