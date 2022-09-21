package dtri.com.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

//https://www.toptal.com/java/stomp-spring-boot-websocket
//https://www.jianshu.com/p/6cee0c561860
@Configuration
@EnableWebSocketMessageBroker
public class ApplicationConfig implements WebSocketMessageBrokerConfigurer {

	/** 接收設置 */
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		// 此服務器未與 ws:// 連接，而是與 http:// 或 https:// 連接
		registry.addEndpoint("/pnmt")// 位置(Production management[生管排程])
				.setAllowedOrigins("*")// 網域 * =不限制
				.withSockJS();
	}

	/** 回傳/接收設置 */
	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		// 定義 回傳 "客戶端" 訂閱地址的線索信息，"客戶端" 接收位置
		config.enableSimpleBroker("/toAllClient/", "/toOneClient/");//可多個 "/toAllClient/", "/queue/"
		// 定義 傳遞 "服務端" 地址接收客戶之消息，"服務端" 接收位置
		 config.setApplicationDestinationPrefixes("/toServer");
	}
}
