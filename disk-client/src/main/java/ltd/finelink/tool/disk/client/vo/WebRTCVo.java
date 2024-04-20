package ltd.finelink.tool.disk.client.vo;

import lombok.Data;

@Data
public class WebRTCVo {
	
	private String type;
	
	private String code;
	
	private String sdp;
	
	private String pwd;
	
	private CandidateVo iceCandidate;

}
