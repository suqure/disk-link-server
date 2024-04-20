package ltd.finelink.tool.disk.dto;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import ltd.finelink.tool.disk.constants.ConstantsKey;
import ltd.finelink.tool.disk.entity.torrent.Node;
import ltd.finelink.tool.disk.exception.BizException;
import ltd.finelink.tool.disk.utils.BTUtil;
import ltd.finelink.tool.disk.utils.CodeUtil;

/**
 * author:ZhengXing datetime:2018-02-14 19:39 一个节点信息
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class NodeInfo {

	private Long id;

	/**
	 * 存储16进制形式的String, byte[20] 转的16进制String,长度固定为40
	 */
	private String nodeId = "";

	private String ip;

	private Integer port;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 修改时间
	 */
	private Date updateTime;

	/**
	 * nodeIds 字节表示
	 */ 
	private byte[] nodeIdBytes;

	/**
	 * 最后活动时间(收到请求或收到回复)
	 */ 
	private Date lastActiveTime = new Date();

	/**
	 * 权重,允许并发导致的一些误差 see {@link com.zx.bt.spider.enums.NodeRankEnum}
	 */ 
	private Integer rank = 0;

	/**
	 * 增加rank
	 */
	public NodeInfo addRank(int addValue) {
		if (Integer.MAX_VALUE - rank >= addValue)
			rank += addValue;
		else
			rank = Integer.MAX_VALUE;
		return this;
	}

	/**
	 * 检查该节点信息是否完整
	 */
	public void check() {
		// 此处对小于1024的私有端口.不作为错误.
		if (nodeIdBytes == null || nodeIdBytes.length != 20 || StringUtils.isBlank(ip) || port == null || port > 65535)
			throw new BizException("该节点信息有误:" + this);
	}

	/**
	 * Node 转 InetSocketAddress
	 */
	public InetSocketAddress toAddress() {
		return new InetSocketAddress(this.ip, this.port);
	}

	/**
	 * List<Node> 转 byte[]
	 */
	public static byte[] toBytes(List<NodeInfo> nodes) {
		if (nodes == null || nodes.isEmpty())
			return new byte[0];
		byte[] result = new byte[nodes.size() * ConstantsKey.NODE_BYTES_LEN];
		for (int i = 0; i + ConstantsKey.NODE_BYTES_LEN <= result.length; i += ConstantsKey.NODE_BYTES_LEN) {
			System.arraycopy(nodes.get(i / ConstantsKey.NODE_BYTES_LEN).toBytes(), 0, result, i,
					ConstantsKey.NODE_BYTES_LEN);
		}
		return result;
	}

	/**
	 * Node 转 byte[]
	 */
	public byte[] toBytes() {
		check();
		// nodeIds
		byte[] nodeBytes = new byte[ConstantsKey.NODE_BYTES_LEN];
		System.arraycopy(nodeIdBytes, 0, nodeBytes, 0, 20);

		// ip
		String[] ips = StringUtils.split(ip, ".");
		if (ips.length != 4)
			throw new BizException("该节点IP有误,节点信息:" + this);
		byte[] ipBytes = new byte[4];
		for (int i = 0; i < 4; i++) {
			ipBytes[i] = (byte) Integer.parseInt(ips[i]);
		}
		System.arraycopy(ipBytes, 0, nodeBytes, 20, 4);

		// ports
		byte[] portBytes = CodeUtil.int2TwoBytes(port);
		System.arraycopy(portBytes, 0, nodeBytes, 24, 2);

		return nodeBytes;
	}

	/**
	 * byte[26] 转 Node
	 */
	public NodeInfo(byte[] bytes) {
		if (bytes.length != ConstantsKey.NODE_BYTES_LEN)
			throw new BizException("转换为Node需要bytes长度为26,当前为:" + bytes.length);
		// nodeIds
		nodeIdBytes = ArrayUtils.subarray(bytes, 0, 20);

		// ip
		ip = CodeUtil.bytes2Ip(ArrayUtils.subarray(bytes, 20, 24));

		// ports
		port = CodeUtil.bytes2Port(ArrayUtils.subarray(bytes, 24, ConstantsKey.NODE_BYTES_LEN));

		initHexStrNodeId();
	}

	public NodeInfo(byte[] nodeIdBytes, String ip, Integer port) {
		this.nodeIdBytes = nodeIdBytes;
		this.ip = ip;
		this.port = port;
		initHexStrNodeId();

	}

	public NodeInfo(byte[] nodeIdBytes, String ip, Integer port, Integer rank) {
		this.nodeIdBytes = nodeIdBytes;
		this.ip = ip;
		this.port = port;
		this.rank = rank;
		initHexStrNodeId();
	}

	public NodeInfo(byte[] nodeIdBytes, InetSocketAddress sender, Integer rank) {
		this.nodeIdBytes = nodeIdBytes;
		this.ip = BTUtil.getIpBySender(sender);
		this.port = sender.getPort();
		this.rank = rank;
		initHexStrNodeId();
	}

	// 生成nodeId
	public void initHexStrNodeId() {
		if (this.nodeIdBytes != null)
			this.nodeId = CodeUtil.bytes2HexStr(this.nodeIdBytes);
	}
	
	public Node toNode() {
		Node node = new Node();
		node.setNodeId(nodeId);
		node.setIp(ip);
		node.setPort(port);
		node.setCreateTime(System.currentTimeMillis());
		node.setUpdateTime(System.currentTimeMillis());
		return node;
	}
}
