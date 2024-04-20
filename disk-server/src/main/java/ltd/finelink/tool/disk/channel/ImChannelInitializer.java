
package ltd.finelink.tool.disk.channel;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.RequiredArgsConstructor;
import ltd.finelink.tool.disk.config.NettyProperties;
import ltd.finelink.tool.disk.handler.HttpRequestHandler;
import ltd.finelink.tool.disk.handler.IdleEventHandler;
import ltd.finelink.tool.disk.handler.MessageDecodeHander;
import ltd.finelink.tool.disk.handler.MessageEncodeHandler;
import ltd.finelink.tool.disk.handler.MessageHandler;
import ltd.finelink.tool.disk.handler.WebSocketHandler;

/**
 * Channel Initializer
 *
 * @author chenjinghe
 */
@Component
@RequiredArgsConstructor
public class ImChannelInitializer extends ChannelInitializer<SocketChannel> {
	
	private final WebSocketHandler webSocketHandler;
	
	private final HttpRequestHandler httpRequestHandler;
	
	private final MessageHandler messageHandler;
	
	private final IdleEventHandler idleEventHandler;
 
	private final NettyProperties nettyProperties;

    @Override
    protected void initChannel(SocketChannel socketChannel) { 
        ChannelPipeline pipeline = socketChannel.pipeline(); 
        pipeline.addLast("idle", new IdleStateHandler(0, 0, nettyProperties.getMaxIdleSec(), TimeUnit.SECONDS));//空闲检测
        pipeline.addLast("idle-event",idleEventHandler);//空闲事件处理
        pipeline.addLast("http-codec", new HttpServerCodec()); // Http消息编码解码
        pipeline.addLast("aggregator", new HttpObjectAggregator(65536)); // Http消息组装
        pipeline.addLast("http-chunked", new ChunkedWriteHandler()); // WebSocket通信支持 
        pipeline.addLast(new MessageDecodeHander()); 
        pipeline.addLast(messageHandler);
        pipeline.addLast(httpRequestHandler);
        pipeline.addLast(webSocketHandler);  
        //发送消息protobuf编码
        pipeline.addLast(new MessageEncodeHandler()); 
        
    }
}
