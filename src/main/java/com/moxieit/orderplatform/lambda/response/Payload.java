package com.moxieit.orderplatform.lambda.response;

import java.util.List;

import com.moxieit.orderplatform.lambda.request.Elements;
import com.moxieit.orderplatform.lambda.request.Summary;

public class Payload {

	private String template_type;

	private String recipient_name;

	private String order_number;

	private String currency;

	private String payment_method;

	private String order_url;

	private Number timestamp;

	Elements[] elements;

	Address address;

	Summary summary;

	Adjustments[] adjustments;

	private String text;

	List<Buttons> buttons;

	public Payload() {
		// TODO Auto-generated constructor stub
	}

	public List<Buttons> getButtons() {
		return buttons;
	}

	public void setButtons(List<Buttons> buttons) {
		this.buttons = buttons;
	}

	public String getTemplate_type() {
		return template_type;
	}

	public void setTemplate_type(String template_type) {
		this.template_type = template_type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getRecipient_name() {
		return recipient_name;
	}

	public void setRecipient_name(String recipient_name) {
		this.recipient_name = recipient_name;
	}

	public String getOrder_number() {
		return order_number;
	}

	public void setOrder_number(String order_number) {
		this.order_number = order_number;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getPayment_method() {
		return payment_method;
	}

	public void setPayment_method(String payment_method) {
		this.payment_method = payment_method;
	}

	public String getOrder_url() {
		return order_url;
	}

	public void setOrder_url(String order_url) {
		this.order_url = order_url;
	}

	public Number getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Number timestamp) {
		this.timestamp = timestamp;
	}

	public Elements[] getElements() {
		return elements;
	}

	public void setElements(Elements[] elements) {
		this.elements = elements;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Summary getSummary() {
		return summary;
	}

	public void setSummary(Summary summary) {
		this.summary = summary;
	}

	public Adjustments[] getAdjustments() {
		return adjustments;
	}

	public void setAdjustments(Adjustments[] adjustments) {
		this.adjustments = adjustments;
	}

}
