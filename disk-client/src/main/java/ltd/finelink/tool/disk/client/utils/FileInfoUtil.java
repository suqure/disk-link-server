package ltd.finelink.tool.disk.client.utils;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;
import ltd.finelink.tool.disk.client.entity.ShareDir;
import ltd.finelink.tool.disk.client.entity.ShareFile;
import ltd.finelink.tool.disk.client.vo.DirInfo;
import ltd.finelink.tool.disk.utils.MD5Util;

@Slf4j
public class FileInfoUtil {

	public static final String[] sizeUnit = { "B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB" };

	public static final List<String> imageFormat = Arrays.asList(".png", ".jpg", ".jpeg", ".bmp", ".gif", ".webp",
			".psd", ".svg", ".tiff");
	public static final List<String> videoFormat = Arrays.asList(".avi", ".wmv", ".mpg", ".mpeg", ".mov", ".ram",
			".swf", ".flv", ".mp4", ".avi", ".rmvb", ".mpg", ".mkv");
	public static final List<String> audioFormat = Arrays.asList(".mp3", ".wma", ".rm", ".wav", ".mid", ".ape",
			".flac");
	public static final List<String> docFormat = Arrays.asList(".txt", ".pdf", ".doc", ".docx", ".ppt", ".pptx", ".xls",
			".xlsx", ".csv", ".wps", ".chm");

	public static String formatSize(Long bytes) {
		String format = " ";
		if (bytes == null) {
			return format;
		}
		if (bytes == 0) {
			return "0 B";
		}
		int k = 1024;
		int i = (int) Math.floor(Math.log(bytes) / Math.log(k));
		BigDecimal result = new BigDecimal(bytes / Math.pow(k, i));
		return result.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + " " + sizeUnit[i];
	}

	public static ShareFile transfer(File file) {
		ShareFile shareFile = new ShareFile();
		shareFile.setFormat(getFileFormat(file.getName()));
		shareFile.setName(file.getName());
		shareFile.setSize(file.length());
		shareFile.setPath(file.getPath());
		shareFile.setKey(MD5Util.encrypt(shareFile.getPath() + ":" + shareFile.getSize()));
		shareFile.setShareTime(new Date());
		return shareFile;
	}

	public static ShareDir transferDir(File file) {
		ShareDir shareDir = new ShareDir();
		shareDir.setName(file.getName());
		shareDir.setPath(file.getPath() + File.separator);
		shareDir.setKey(MD5Util.encrypt(shareDir.getPath()));
		shareDir.setShareTime(new Date());
		return shareDir;
	}

	public static String getFileFormat(String fileName) {
		String format = "file";
		if (fileName != null && fileName.contains(".")) {
			String endFix = fileName.substring(fileName.lastIndexOf('.'));
			if (imageFormat.contains(endFix.toLowerCase())) {
				format = "image";
			} else if (videoFormat.contains(endFix.toLowerCase())) {
				format = "video";
			} else if (audioFormat.contains(endFix.toLowerCase())) {
				format = "audion";
			} else if (docFormat.contains(endFix.toLowerCase())) {
				format = "doc";
			}
		}
		return format;
	}

	public static List<DirInfo> getDirInfoChildren(ShareDir root, DirInfo dirInfo) {
		List<DirInfo> children = new ArrayList<>();
		if (StringUtils.isNoneBlank(root.getPath(), dirInfo.getPath()) && dirInfo.getType() == 0) {
			File file = new File(root.getPath() + dirInfo.getPath());
			if (file.exists() && file.isDirectory()) {
				for (File child : file.listFiles()) {
					DirInfo info = new DirInfo();
					info.setRoot(dirInfo.getRoot());
					info.setDevice(dirInfo.getDevice());
					info.setName(child.getName());
					if (child.isDirectory()) {
						info.setId(MD5Util.encrypt(child.getPath() + File.separator));
						info.setType(0);
						info.setPath(dirInfo.getPath() + child.getName() + File.separator);
					} else {
						info.setId(MD5Util.encrypt(child.getPath() + ":" + child.length()));
						info.setType(1);
						info.setPath(dirInfo.getPath() + child.getName());
						info.setSize(child.length());
					}
					children.add(info);
				}
			}
		}

		return children;
	}

	public static ShareFile getDirShareFile(ShareDir root, DirInfo dirInfo) {
		if (StringUtils.isNoneBlank(root.getPath(), dirInfo.getPath()) && dirInfo.getType() == 1) {
			File file = new File(root.getPath() + dirInfo.getPath());
			if (file.exists()&&file.isFile()) {
				return transfer(file);
			}
		}
		return null;
	}

}
