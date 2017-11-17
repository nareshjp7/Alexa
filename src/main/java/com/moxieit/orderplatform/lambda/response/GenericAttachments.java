package com.moxieit.orderplatform.lambda.response;

import java.util.List;

import com.amazonaws.services.lexruntime.model.Button;

public class GenericAttachments {

	private String title;
	private String subTitle;
	private String imageUrl;
	private String attachmentLinkUrl;
	List<Buttons> buttons;

	public GenericAttachments() {
		// TODO Auto-generated constructor stub
	}

	public GenericAttachments(String title, String subTitle, String imageUrl, String attachmentLinkUrl,
			List<Buttons> buttons, List<Button> button) {
		super();
		this.title = title;
		this.subTitle = subTitle;
		this.imageUrl = imageUrl;
		this.attachmentLinkUrl = attachmentLinkUrl;
		this.buttons = buttons;
	}

	public List<Buttons> getButtons() {
		return buttons;
	}

	public void setButtons(List<Buttons> buttons) {
		this.buttons = buttons;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getAttachmentLinkUrl() {
		return attachmentLinkUrl;
	}

	public void setAttachmentLinkUrl(String attachmentLinkUrl) {
		this.attachmentLinkUrl = attachmentLinkUrl;
	}

	@Override
	public String toString() {
		return "GenericAttachments [title=" + title + ", subTitle=" + subTitle + ", imageUrl=" + imageUrl
				+ ", attachmentLinkUrl=" + attachmentLinkUrl + ", buttons=" + buttons + "]";
	}

}
