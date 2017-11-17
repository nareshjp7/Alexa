package com.moxieit.orderplatform.lambda.response;

import java.util.Arrays;

public class ResponseCard {

	private String version;
	private String contentType;
	GenericAttachments[] genericAttachments;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public GenericAttachments[] getGenericAttachments() {
		return genericAttachments;
	}

	public void setGenericAttachments(GenericAttachments[] genericAttachments) {
		this.genericAttachments = genericAttachments;
	}

	@Override
	public String toString() {
		return "ResponseCard [version=" + version + ", contentType=" + contentType + ", genericAttachments="
				+ Arrays.toString(genericAttachments) + "]";
	}

}
