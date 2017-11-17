package com.moxieit.orderplatform.lambda.response;

public class Stream {

	private String token;

	private String url;

	private String offsetInMilliseconds;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getOffsetInMilliseconds() {
		return offsetInMilliseconds;
	}

	public void setOffsetInMilliseconds(String offsetInMilliseconds) {
		this.offsetInMilliseconds = offsetInMilliseconds;
	}

}
