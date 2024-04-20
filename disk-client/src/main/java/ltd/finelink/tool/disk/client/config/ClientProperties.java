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
package ltd.finelink.tool.disk.client.config;

import javax.validation.constraints.Size;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;
import ltd.finelink.tool.disk.enums.WSProtocol;

@Data
@Configuration
@ConfigurationProperties(prefix = "disk.server")
public class ClientProperties {
	/**
	 * netty端口号 默认80
	 */
	@Size(min = 1000, max = 65535)
	private int port = 8080;

	private String query = "anonymous";
	
	private String auth = "base-server";

	private String userId;

	private String token;

	private WSProtocol protocol = WSProtocol.WS;

	private String host = "localhost";

	private String api = "http://localhost:8080";

	private String channel = "DEFALUT";
}
