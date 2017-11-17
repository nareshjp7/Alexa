package com.moxieit.orderplatform.lambda.response;

public class VideoItem {

	private String source;

	Metadata metadata;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Metadata getMetadata() {
		return metadata;
	}

	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

}
