package com.moxieit.orderplatform.lambda.response;

public class Requested_user_info {

	private String shipping_address;
	private String contact_name;
	private String contact_phone;
	private String contact_email;

	public String getShipping_address() {
		return shipping_address;
	}

	public void setShipping_address(String shipping_address) {
		this.shipping_address = shipping_address;
	}

	public String getContact_name() {
		return contact_name;
	}

	public void setContact_name(String contact_name) {
		this.contact_name = contact_name;
	}

	public String getContact_phone() {
		return contact_phone;
	}

	public void setContact_phone(String contact_phone) {
		this.contact_phone = contact_phone;
	}

	public String getContact_email() {
		return contact_email;
	}

	public void setContact_email(String contact_email) {
		this.contact_email = contact_email;
	}

	@Override
	public String toString() {
		return "Requested_user_info [shipping_address=" + shipping_address + ", contact_name=" + contact_name
				+ ", contact_phone=" + contact_phone + ", contact_email=" + contact_email + "]";
	}

}
