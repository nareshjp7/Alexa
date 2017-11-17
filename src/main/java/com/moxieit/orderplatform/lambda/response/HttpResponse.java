package com.moxieit.orderplatform.lambda.response;

public class HttpResponse {

	private Integer httpStatus;

	private String responseBody;

	public Integer getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(Integer httpStatus) {
		this.httpStatus = httpStatus;
	}

	public String getResponseBody() {
		return responseBody;
	}

	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}

	@Override
	public String toString() {
		return "HttpResponse [httpStatus=" + httpStatus + ", responseBody=" + responseBody + "]";
	}

}
