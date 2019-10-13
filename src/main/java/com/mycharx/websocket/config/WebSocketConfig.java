package com.mycharx.websocket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

/**
 * EnableWebSocketMessageBroker 此注解表示开启WebSocket支持。通过此注解开启使用STOMP协议来传输基于代理（message broker）的消息。
 *
 * @author 张卜亢
 * @date 2019.10.13 21:21:49
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
        //注册一个名为/endpointChat的节点，并指定使用SockJS协议。
        stompEndpointRegistry.addEndpoint("/endpointChat") //注册一个 /endpointChat 的 websocket 节点
                .addInterceptors(handshakeInterceptor()) //添加 websocket握手拦截器
                .setHandshakeHandler(defaultHandshakeHandler()) //添加 websocket握手处理器
                .setAllowedOrigins("*"); //设置允许可跨域的域名
//                .withSockJS(); //指定使用SockJS协议，为了兼容flutter的APP应用，不适用sockJS，而使用原生websocket
    }

    /**
     * websocket握手处理器
     */
    private DefaultHandshakeHandler defaultHandshakeHandler() {
        return new DefaultHandshakeHandler() {
            @Override
            protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
                //设置认证用户
                return (Principal) attributes.get("user");
            }
        };
    }

    /**
     * WebSocket 握手拦截器
     * 可做一些用户认证拦截处理
     */
    private HandshakeInterceptor handshakeInterceptor() {
        return new HandshakeInterceptor() {
            /**
             * websocket握手
             */
            @Override
            public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
                ServletServerHttpRequest req = (ServletServerHttpRequest) request;
                //获取token认证
                String token = req.getServletRequest().getParameter("token");
                //解析token获取用户信息
                Principal user = parseToken(token);
                if (user == null) {   //如果token认证失败user为null，返回false拒绝握手
                    return false;
                }
                //保存认证用户
                attributes.put("user", user);
                return true;
            }

            @Override
            public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

            }
        };
    }

    /**
     * 根据token认证授权
     *
     * @param token
     */
    private Principal parseToken(String token) {
        //实现用户的认证并返回用户信息，如果认证失败返回 null
        if (token != null) {
            //用户信息需继承 Principal 并实现 getName() 方法，返回全局唯一值
            return new LocalPrincipal(token);
        }
        return null;
    }

    /**
     * 配置消息代理（Message Broker），可以理解为信息传输的通道
     *
     * @param messageBrokerRegistry MessageBrokerRegistry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry messageBrokerRegistry) {
        //设置客户端接收消息地址的前缀（可不设置）
        //点对点式应增加一个/queue的消息代理。
        // 相应的如果是广播室模式可以设置为"/topic"
        messageBrokerRegistry.enableSimpleBroker(
                "/queue",
                "/topic"
        );

        //设置客户端接收点对点消息地址的前缀，默认为 /user
        messageBrokerRegistry.setUserDestinationPrefix("/user");
        //设置客户端向服务器发送消息的地址前缀（可不设置）
        messageBrokerRegistry.setApplicationDestinationPrefixes("/app");
    }
}
