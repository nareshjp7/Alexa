package com.moxieit.orderplatform.lambda.response;

public class Buttons {

	private String text;
	private String value;
	private String url;
	private String type;
	private String title;
	private String payload;
	Payment_summary payment_summary;
	Price_list[] price_list;

	public Buttons() {
		// TODO Auto-generated constructor stub
	}

	public Buttons(String text, String value) {
		super();
		this.text = text;
		this.value = value;
	}

	public Buttons(String url, String type, String title) {
		super();
		this.url = url;
		this.type = type;
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Payment_summary getPayment_summary() {
		return payment_summary;
	}

	public void setPayment_summary(Payment_summary payment_summary) {
		this.payment_summary = payment_summary;
	}

	public Price_list[] getPrice_list() {
		return price_list;
	}

	public void setPrice_list(Price_list[] price_list) {
		this.price_list = price_list;
	}

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

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Buttons [text=" + text + ", value=" + value + ", type=" + type + ", title=" + title + ", payload="
				+ payload + "]";
	}

}
