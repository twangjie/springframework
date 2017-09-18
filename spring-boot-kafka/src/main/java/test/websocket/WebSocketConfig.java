package test.websocket;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.ByteArrayMessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import java.util.List;

@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer implements WebSocketConfigurer {

    private String sockJsClientLibraryUrl = "webjars/sockjs-client/sockjs.min.js";

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(8192);
        container.setMaxBinaryMessageBufferSize(8192);
        return container;
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        messageConverters.add(new ByteArrayMessageConverter());
        return false;
    }

    @Override
    public void configureWebSocketTransport(final WebSocketTransportRegistration registration) {

        //registration.addDecoratorFactory(TelnetWebSocketHandlerDecoratorFactory.getInstance());

        super.configureWebSocketTransport(registration);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        //应用程序以/app为前缀，代理目的地以/topic、/user为前缀
        registry.enableSimpleBroker("/topic", "/user");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    // WebSocket with STOMP over SockJS
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        // alarms
        registry.addEndpoint("/v2/rest/monctrl/alarms-ws")
                .setAllowedOrigins("*")
                .withSockJS()
                .setClientLibraryUrl(sockJsClientLibraryUrl);

        // test
        registry.addEndpoint("/test/ws-echo-endpoint")
                .setAllowedOrigins("*")
                .withSockJS()
                .setClientLibraryUrl(sockJsClientLibraryUrl);
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

//        // WebSocket with SockJS
//        registry.addHandler(new WebSocketHandler(), "/test/ws-without-stomp")
//                .setAllowedOrigins("*")
//                .withSockJS()
//                .setClientLibraryUrl(sockJsClientLibraryUrl);
//
//        // WebSocket without SockJS
//        registry.addHandler(new WebSocketHandler(), "/test/ws-without-sockjs")
//                .setAllowedOrigins("*");
    }
}