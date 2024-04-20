package ltd.finelink.tool.disk.server;

import org.springframework.stereotype.Component;

import io.netty.bootstrap.Bootstrap;
import lombok.RequiredArgsConstructor;
import ltd.finelink.tool.disk.config.DHTProperties;

@RequiredArgsConstructor
@Component
public class DHTServer {

	private final DHTProperties dHtProperties;

	private final Bootstrap bootstrap;

	public void start() {
		if (dHtProperties.isEnabled()) {
			try {
				bootstrap.bind(dHtProperties.getPort()).sync().channel().closeFuture().await();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

}
