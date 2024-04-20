package ltd.finelink.tool.disk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import lombok.RequiredArgsConstructor;
import ltd.finelink.tool.disk.server.DHTServer;
import ltd.finelink.tool.disk.server.NettyServer;


@RequiredArgsConstructor
@SpringBootApplication
@EnableScheduling
public class ServerApplication {
	
	private final NettyServer nettyServer;
	
	private final DHTServer dHTServer;
	
	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}
	
	@Bean
	public ApplicationListener<ApplicationReadyEvent> readyEventApplicationListener() {
		return new ApplicationListener<ApplicationReadyEvent>() {
			@Override
			public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
				new Thread(()->{
					nettyServer.start();
				}).start();
				new Thread(()->{
					dHTServer.start();
				}).start();
			}
		};
	}

}
