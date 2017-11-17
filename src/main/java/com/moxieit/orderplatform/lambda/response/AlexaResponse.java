package com.moxieit.orderplatform.lambda.response;

public class AlexaResponse extends BaseResponse{

	private String version;

	Response response;

	Directives directives;

	public Directives getDirectives() {
		return directives;
	}

	public void setDirectives(Directives directives) {
		this.directives = directives;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

}
