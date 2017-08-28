package telnet.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.ByteArrayMessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import java.util.List;

// http://blog.csdn.net/haoyuyang/article/details/53364372

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {
//public class WebSocketConfig extends WebMvcConfigurerAdapter implements WebSocketConfigurer {

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(8192);
        container.setMaxBinaryMessageBufferSize(8192);
        return container;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        //应用程序以/app为前缀，代理目的地以/topic、/user为前缀
        registry.enableSimpleBroker("/topic", "/user");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        // telnet
        //registry.addEndpoint("/telnet").setAllowedOrigins("*").withSockJS().setClientLibraryUrl("/webjars/sockjs-client/sockjs.js");
        registry.addEndpoint("/telnet").setAllowedOrigins("*").withSockJS().setClientLibraryUrl("/webjars/sockjs-client/sockjs.js");
        registry.addEndpoint("/telnet/*").setAllowedOrigins("*").withSockJS().setClientLibraryUrl("/webjars/sockjs-client/sockjs.js");
//        registry.addEndpoint("/telnet").setAllowedOrigins("*").withSockJS()
//                .setClientLibraryUrl("/webjars/sockjs-client/sockjs.js");
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        messageConverters.add(new ByteArrayMessageConverter());
        return false;
    }

    @Override
    public void configureWebSocketTransport(final WebSocketTransportRegistration registration) {

        registration.addDecoratorFactory(TelnetWebSocketHandlerDecoratorFactory.getInstance());

        super.configureWebSocketTransport(registration);
    }
}