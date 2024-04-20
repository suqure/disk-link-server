package ltd.finelink.tool.disk.dto;

import org.apache.commons.lang3.ArrayUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import ltd.finelink.tool.disk.constants.ConstantsKey;
import ltd.finelink.tool.disk.exception.BizException;
import ltd.finelink.tool.disk.utils.CodeUtil;

 
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Peer {

    private String ip;

    private Integer port;

    /**
     * byte[6] 转 Node
     */
    public Peer(byte[] bytes) {
        if (bytes.length != ConstantsKey.PEER_BYTES_LEN)
            throw new BizException("转换为Peer需要bytes长度为6,当前为:" + bytes.length);
        //ip
        ip = CodeUtil.bytes2Ip(ArrayUtils.subarray(bytes, 0, 4));

        //ports
        port = CodeUtil.bytes2Port(ArrayUtils.subarray(bytes, 4, 6));
    }
}
