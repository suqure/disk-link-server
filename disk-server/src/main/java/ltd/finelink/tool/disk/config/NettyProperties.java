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

import javax.validation.constraints.Size;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

 
@Data
@Configuration
@ConfigurationProperties(prefix = "disk.netty")
public class NettyProperties {
	/**
	 * netty端口号 默认8080
	 */
	@Size(min=1000, max=65535) 
    private int port = 8080;
	/**
	 * 默认路径
	 */
	private String basePath = "base-server";
	
	/**
	 * 匿名登录
	 */
	private String anonymousPath = "anonymous";
	
	/**
	 * 反馈接收邮箱
	 */
	private String feebackMail="feeback@xxx.com";
	
	private String mailSender = "support@xxx.com";
	

	/**
	 * netty主线程数
	 */
    private int bossCount;

    /**
     * netty工作线程数
     */
    private int workerCount;
    
    /**
     * 连接最大空闲时间（单位秒），默认60s
     */
    private long maxIdleSec = 60;
    
    /**
     * 最大帧长度
     */
    private int maxFrameLength = 65536;
 

    /**
     * 主线程队列 默认200
     */
    private int backlog = 200;
    
    private String qiniuAk = "xxxxxxxxxxxxxxx"; //七牛ak
    
    private String qiniuSk = "xxxxxxxxxxxxxxx";//七牛sk
    
    private String qiniuCb = "https://xxx.com/qiniuCb"; //七牛服务器回调地址
    
    private String qiniuBucket = "xxxx"; //七牛bucket
    
    private String keyPerfix = "share/";
    
    private String aiApi = "http://xxxxxxxxx.com/v1/chat/completions";//open ai风格的ai接口
    
    private int chatToken = 10;
    
}
