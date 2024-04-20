package ltd.finelink.tool.disk.client.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeviceUtil {

	/**
	 * 获取本机 mac 地址集合
	 *
	 * @return mac 地址集合
	 */
	public static List<String> getMac() {
		List<String> list = new ArrayList<>();
		try {
			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
			while (networkInterfaces.hasMoreElements()) {
				NetworkInterface networkInterface = networkInterfaces.nextElement();
				Optional.ofNullable(networkInterface.getHardwareAddress()).ifPresent(mac -> list.add(format(mac)));
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return list;
	}

	public static String getCurrentMac() { 
		try {
			NetworkInterface inteface = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
			return format(inteface.getHardwareAddress()); 
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 将 mac 字节数组格式化为全大写并且使用 - 作为分隔符的字符串
	 *
	 * @param mac 获取到的 mac 字节数组
	 *
	 * @return 格式化后的 mac 地址
	 */
	private static String format(byte[] mac) {
		StringBuilder sb = new StringBuilder();
		for (byte b : mac) {
			sb.append(String.format("%02X", b)).append("-");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	public static String getComputerName() {
		return System.getenv("COMPUTERNAME");
	}

	public static String getLocalIp() {
		try {
			InetAddress address = InetAddress.getLocalHost();
			return address.getHostAddress();
		} catch (UnknownHostException e) {
			log.error(e.getMessage());
		}
		return "127.0.0.1";
	}

	public static String getOsName() {
		return System.getProperty("os.name");
	}
	
	public static String getOsArch() {
		return System.getProperty("os.arch");
	}

	public static String getDeviceInfo() {
		JSONObject device = new JSONObject();
		device.put("computerName", getComputerName());
		device.put("agent", "java client");
		device.put("mac", getCurrentMac());
		device.put("timezone", ZoneId.systemDefault());
		device.put("osName", getOsName());
		device.put("osArch", getOsArch());
		return device.toJSONString();
	}

}
