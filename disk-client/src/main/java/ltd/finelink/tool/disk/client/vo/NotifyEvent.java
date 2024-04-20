package ltd.finelink.tool.disk.client.vo;

import lombok.Data;
import ltd.finelink.tool.disk.client.enums.NotifyType;

@Data
public class NotifyEvent {

	private NotifyType type;

	private String code;

	private String message;

	private Object data;

}
