package com.moxieit.orderplatform.lambda.request;

public class AlexaRequest {

	private String version;

	Session session;

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
