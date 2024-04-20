package ltd.finelink.tool.disk.vo;

import java.util.List;

import lombok.Data;

@Data
public class AiChatVo {
	
	private String  model;
	  
	private List<AiMessage> messages;
	
	private boolean stream;

}
