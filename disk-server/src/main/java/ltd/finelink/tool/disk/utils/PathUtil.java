package ltd.finelink.tool.disk.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class PathUtil {
	
	public static String handlerRelativePath(String path) {
		if (isRelativePath(path)) {
			String classPath = PathUtil.class.getResource("/").getPath();
			return classPath + path;
		}

		return path;
	}
	
	public static boolean isRelativePath(String path) {
		if (StringUtils.isNotBlank(path)) {
			if (path.startsWith("/") || path.matches("^[A-z]:[/|\\\\]\\S*")) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean runInJar() {
		URL url = PathUtil.class.getResource("");
		if ("jar".equals(url.getProtocol())) {
			return true;
		}
		return false;
	}
	
	public static InputStream getInputStream(String filePath) throws IOException {
		if (StringUtils.isNotBlank(filePath)) {
			if (filePath.startsWith("http")) {
				URL url = new URL(filePath);
				URLConnection conn = url.openConnection();
				return conn.getInputStream();
			}
			if (runInJar()) {
				if (isRelativePath(filePath)) {
					Resource resource = new ClassPathResource(filePath);
					return resource.getInputStream();
				} else {
					return new FileInputStream(new File(filePath));
				}
			} else {
				String path = handlerRelativePath(filePath);
				return new FileInputStream(new File(path));
			}
		}
		return null;
	}

}
