package ltd.finelink.tool.disk.client.vo;

import dev.onvoid.webrtc.RTCIceCandidate;
import lombok.Data;

@Data
public class CandidateVo {

	private int sdpMLineIndex;

	private String candidate;

	private String sdpMid;

	private String usernameFragment;

	public static CandidateVo transfer(RTCIceCandidate candidate) {
		CandidateVo vo = new CandidateVo();
		vo.setCandidate(candidate.sdp);
		vo.setSdpMid(candidate.sdpMid);
		vo.setSdpMLineIndex(candidate.sdpMLineIndex);
		vo.setUsernameFragment(candidate.sdp.substring(candidate.sdp.indexOf("ufrag")).split(" ")[1]);
		return vo;
	}

	public RTCIceCandidate to() {
		RTCIceCandidate cadidate = new RTCIceCandidate(sdpMid, sdpMLineIndex, candidate);
		return cadidate;
	}

}
