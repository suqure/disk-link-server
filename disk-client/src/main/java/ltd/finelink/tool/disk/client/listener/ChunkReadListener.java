package ltd.finelink.tool.disk.client.listener;

import ltd.finelink.tool.disk.client.vo.FileInfo;

public interface ChunkReadListener {

	void onRead(FileInfo chunk);
	
	void onClose(FileInfo chunk); 

}
