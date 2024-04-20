package ltd.finelink.tool.disk.utils;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class StringTemplateUtils {

	private static Pattern paramsPattern = Pattern.compile("\\$\\{(.*?)\\}");

	private static Pattern emailPattern = Pattern.compile("[a-zA-Z0-9_]+@[a-zA-Z0-9_]+(\\.[a-zA-Z0-9]+)+");

	public static String renderMessage(String template, Map<String, String> map) {
		if (map != null) {
			Set<Entry<String, String>> sets = map.entrySet();
			for (Entry<String, String> entry : sets) {
				String regex = "\\$\\{" + entry.getKey() + "\\}";
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(template);
				template = matcher.replaceAll(entry.getValue());
			}
		}
		return template;
	}

	public static String transferTemplateParams(String template) {
		String params = "";
		if (StringUtils.isNotBlank(template)) {
			Matcher matcher = paramsPattern.matcher(template);
			while (matcher.find()) {
				String param = matcher.group(1);
				if (!param.isEmpty()) {
					if (params.isEmpty()) {
						params += param;
					} else if (!params.contains(param)) {
						params += "," + param;
					}
				}

			}
		}

		return params;
	}

	public static boolean validateParams(String params, Map<String, String> paramMap) {
		if (StringUtils.isBlank(params)) {
			return true;
		}
		Set<String> keys = paramMap.keySet();
		for (String param : params.split(",")) {
			if (!keys.contains(param)) {
				return false;
			}
		}
		return true;
	}

	public static boolean verifyEmailAddress(String email) {
		if (StringUtils.isNoneBlank(email)) {
			Matcher matcher = emailPattern.matcher(email);
			return matcher.matches();
		}
		return false;
	}

}
