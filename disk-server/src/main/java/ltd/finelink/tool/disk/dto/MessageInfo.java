package ltd.finelink.tool.disk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import ltd.finelink.tool.disk.enums.MethodEnum;
import ltd.finelink.tool.disk.enums.YEnum;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class MessageInfo {

    /**
     * 方法
     * See {@link MethodEnum}
     */
    private MethodEnum method;

    /**
     * 状态
     * See {@link YEnum}
     */
    private YEnum status;

    /**
     * 消息id
     */
    private String messageId;


}
