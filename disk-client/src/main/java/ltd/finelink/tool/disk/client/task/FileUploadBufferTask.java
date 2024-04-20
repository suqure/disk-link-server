package ltd.finelink.tool.disk.client.task;

import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.commons.codec.binary.Base64;

import dev.onvoid.webrtc.RTCDataChannel;
import dev.onvoid.webrtc.RTCDataChannelState;
import lombok.extern.slf4j.Slf4j;
import ltd.finelink.tool.disk.client.context.UserContext;
import ltd.finelink.tool.disk.client.entity.ShareFile;
import ltd.finelink.tool.disk.client.enums.ChannelBasicType;
import ltd.finelink.tool.disk.client.enums.ChannelFileType;
import ltd.finelink.tool.disk.client.enums.ChannelMessageType;
import ltd.finelink.tool.disk.client.listener.ChunkReadListener;
import ltd.finelink.tool.disk.client.utils.ClientMessageUtil;
import ltd.finelink.tool.disk.client.vo.FileInfo;

@Slf4j
public class FileUploadBufferTask implements Runnable {

	private ShareFile file;

	private String device;

	private long startChunk;

	private int chunkSize;

	private long maxBuffer;

	private ChunkReadListener listener;

	private final static long DEFAUL_BUFFER = 15 * 1024 * 1024;

	private final static int DEFAULT_CHUNK_SIZE = 128000;

	public FileUploadBufferTask(ShareFile file, String device, int chunkSize, long startChunk, long maxBuffer) {
		this(file, device, chunkSize, startChunk, maxBuffer, null);
	}

	public FileUploadBufferTask(ShareFile file, String device, int chunkSize, long startChunk, long maxBuffer,
			ChunkReadListener listener) {
		this.file = file;
		this.device = device;
		this.listener = listener;
		if (startChunk < 0) {
			this.startChunk = 0;
		} else {
			this.startChunk = startChunk;
		}
		if (chunkSize <= 0) {
			this.chunkSize = DEFAULT_CHUNK_SIZE;
		} else {
			this.chunkSize = chunkSize;
		}
		if (maxBuffer <= 0) {
			this.maxBuffer = DEFAUL_BUFFER;
		} else {
			this.maxBuffer = maxBuffer;
		}

	}

	public FileUploadBufferTask(ShareFile file, String device) {
		this(file, device, null);
	}

	public FileUploadBufferTask(ShareFile file, String device, ChunkReadListener listener) {

		this(file, device, 0, 0, 0, listener);

	}

	public FileUploadBufferTask(ShareFile file, String device, int chunkSize, long startChunk,
			ChunkReadListener listener) {

		this(file, device, chunkSize, startChunk, 0, listener);
	}

	public void addListener(ChunkReadListener listener) {
		this.listener = listener;
	}

	@Override
	public void run() {
		if (file == null) {
			return;
		}
		RTCDataChannel channel = UserContext.localChannels.get(device);
		RTCDataChannel fileChannel = UserContext.fileChannels.get(device);
		if (channel == null || fileChannel == null) {
			return;
		}
		String taskId = device + file.getKey();
		if (UserContext.uploadTasks.contains(taskId)) {
			log.warn("upload task aready running {}", taskId);
			return;
		}
		RandomAccessFile f = null;
		FileInfo info = new FileInfo();
		try {
			UserContext.uploadTasks.add(taskId);
			f = new RandomAccessFile(file.getPath(), "r");
			long totalChunk = (long) (file.getSize() % chunkSize == 0 ? file.getSize() / chunkSize
					: Math.ceil(file.getSize() / (double) chunkSize));
			if (file.getSize() < chunkSize) {
				totalChunk = 1;
			}
			long offset = chunkSize * startChunk;
			if (offset > 0) {
				f.seek(offset);
			}

			info.setCurrent(startChunk);
			info.setName(file.getName());
			info.setId(file.getKey());
			info.setSize(file.getSize());
			info.setTotal(totalChunk);
			info.setChunkSize(chunkSize);
			info.setDevice(device);
			if (RTCDataChannelState.CLOSED.equals(channel.getState())
					|| RTCDataChannelState.CLOSING.equals(channel.getState())) {
				return;
			}
			channel.send(ClientMessageUtil.buildChannelBuffer(ClientMessageUtil.buildChannelMessage(
					ChannelMessageType.BASIC, ClientMessageUtil.buildBasicData(ChannelBasicType.FILECHUNK, info))));
			byte[] buffer = new byte[chunkSize];
			while (true) {
				if (!UserContext.uploadTasks.contains(taskId)) {
					break;
				}
				if (RTCDataChannelState.CLOSED.equals(fileChannel.getState())
						|| RTCDataChannelState.CLOSING.equals(fileChannel.getState())) {
					break;
				}
				if (fileChannel.getBufferedAmount() < maxBuffer) {
					if (f.read(buffer) == -1) {
						break;
					}
					fileChannel.send(ClientMessageUtil.buildByteBuffer(buffer));
					if (listener != null) {
						listener.onRead(info);
					}
					info.setCurrent(info.getCurrent() + 1);
				} else {
					Thread.sleep(10);
				}

			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			UserContext.uploadTasks.remove(taskId);
			if (listener != null) {
				listener.onClose(info);
			}
			if (f != null) {
				try {
					f.close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}

		}

	}

}
