/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ltd.finelink.tool.disk.config;

import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;
import lombok.RequiredArgsConstructor;
import ltd.finelink.tool.disk.channel.DHTServerChannelHandler;
import ltd.finelink.tool.disk.channel.ImChannelInitializer;
import ltd.finelink.tool.disk.service.RoutingTable;

/**
 * NettyConfiguration
 *
 * @author chenjinghe
 */
@Configuration
@RequiredArgsConstructor
public class NettyConfiguration {

	private final NettyProperties nettyProperties;

	private final DHTProperties dhtProperties;

	@Bean(name = "serverBootstrap")
	public ServerBootstrap bootstrap(ImChannelInitializer imChannelInitializer) {
		ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup(), workerGroup()).channel(NioServerSocketChannel.class)
				.handler(new LoggingHandler(LogLevel.DEBUG)).childHandler(imChannelInitializer);
		b.option(ChannelOption.SO_BACKLOG, nettyProperties.getBacklog());
		return b;
	}

	@Bean(destroyMethod = "shutdownGracefully")
	public NioEventLoopGroup bossGroup() {
		return new NioEventLoopGroup(nettyProperties.getBossCount());
	}

	@Bean(destroyMethod = "shutdownGracefully")
	public NioEventLoopGroup workerGroup() {
		return new NioEventLoopGroup(nettyProperties.getWorkerCount());
	}

	@Bean
	public InetSocketAddress socketAddress() {
		return new InetSocketAddress(nettyProperties.getPort());
	}

	@Bean(name = "bootstrap")
	public Bootstrap bootstrap(DHTServerChannelHandler handler) {
		EventLoopGroup eventLoopGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2);
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(eventLoopGroup).channel(NioDatagramChannel.class).option(ChannelOption.SO_BROADCAST, true)
				.option(ChannelOption.SO_RCVBUF, 10 * 1024 * 1024).option(ChannelOption.SO_SNDBUF, 10 * 1024 * 1024)
				.handler(handler);
		return bootstrap;
	}

	@Bean
	public RoutingTable routingTable() {

		return new RoutingTable(dhtProperties.getNodeId().getBytes(CharsetUtil.ISO_8859_1), dhtProperties.getIp(),
				dhtProperties.getPort());
	}

	@Bean(name = "magnetTask")
	public Cache<String, Set<String>> magnetTask() {

		return Caffeine.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS).build();
	}

}
