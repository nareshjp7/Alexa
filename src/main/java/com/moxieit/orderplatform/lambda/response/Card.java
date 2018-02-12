package com.moxieit.orderplatform.lambda.response;

import java.util.Set;

public class Card {

	private String type;

	private String title;

	private String content;
	private Set<String> permissions;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Set<String> getPermissions() {
		return permissions;
	}

	public void setPermissions(Set<String> permissions2) {
		this.permissions = permissions2;
	}

}
