package ltd.finelink.tool.disk.client.utils;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OKHttpClientUtil {
	private static long CONNECT_TIMEOUT_SECOND = 30;
	private static long WRITE_TIMEOUT_SECOND = 60;
	private static long READ_TIMEOUT_SECOND = 60;
	public static OkHttpClient client;

	static {
		client = new OkHttpClient.Builder().connectTimeout(CONNECT_TIMEOUT_SECOND, TimeUnit.SECONDS)
				.writeTimeout(WRITE_TIMEOUT_SECOND, TimeUnit.SECONDS).readTimeout(READ_TIMEOUT_SECOND, TimeUnit.SECONDS)
				.build();
	}

	public static String postJSON(String url, Map<String, String> headers, String body) throws IOException {
		Builder builder = new Request.Builder(); 
		builder.url(url).post(RequestBody.create(body, MediaType.get("application/json; charset=utf-8")));
		if (headers != null && !headers.isEmpty()) {
			for (String key : headers.keySet()) {
				builder.addHeader(key, headers.get(key));
			}
		}
		Request request = builder.build();
		Response response = client.newCall(request).execute();
		return response.body().string();
	}

	public static String get(String url, Map<String, String> headers, Map<String, String> params) throws IOException {
		Builder builder = new Request.Builder();
		if (params != null && !params.isEmpty()) {
			String query = "";
			for (String key : params.keySet()) {
				if (query.isEmpty()) {
					query = key + "=" + params.get(key);
				} else {
					query += "&" + key + "=" + params.get(key);
				}
			}
			if (url.contains("?")) {
				if (url.endsWith("?")) {
					url += "?" + query;
				} else {
					url += "&" + query;
				}
			} else {
				url += "?" + query;
			}
		}
		builder.url(url).get();
		if (headers != null && !headers.isEmpty()) {
			for (String key : headers.keySet()) {
				if(headers.get(key)!=null) {
					builder.addHeader(key, headers.get(key));
				} 
			}
		}
		Request request = builder.build();
		Response response = client.newCall(request).execute();
		return response.body().string();
	}

}
