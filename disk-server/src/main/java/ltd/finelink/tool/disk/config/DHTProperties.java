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

import java.util.Arrays;
import java.util.List;

import javax.validation.constraints.Size;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;
import ltd.finelink.tool.disk.utils.BTUtil;

@Data
@Configuration
@ConfigurationProperties(prefix = "disk.dht")
public class DHTProperties {

	private List<Integer> ports = Arrays.asList(43567, 43568, 43569, 43570, 43571);
	
	@Size(min=1000, max=65535) 
	private int port = 43567;
	
	private String ip = "127.0.0.1";
	
	private String nodeId = BTUtil.generateNodeIdString();

	private List<String> nodes = Arrays.asList("router.utorrent.com:6881", "router.bittorrent.com:6881",
			"router.bitcomet.com:6881", "dht.transmissionbt.com:6881");
	
	private String token = "zx";
	
	private boolean enabled = true;

}
