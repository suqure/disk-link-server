 
package ltd.finelink.tool.disk.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;


/**
 * Netty Server
 *
 * @author chenjinghe
 */

@Slf4j
@RequiredArgsConstructor
@Component
public class NettyServer {

    private final ServerBootstrap serverBootstrap;

    private final InetSocketAddress socketAddress;

    private Channel serverChannel;

    public void start()  {
        try {
            ChannelFuture serverChannelFuture = serverBootstrap.bind(socketAddress).sync();
            log.info("Netty Server is started : port {}", socketAddress.getPort());
            serverChannel = serverChannelFuture.channel().closeFuture().sync().channel();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @PreDestroy
    public void stop() {
        if ( serverChannel != null ) {
            serverChannel.close();
            serverChannel.parent().close();
        }
    }
}
