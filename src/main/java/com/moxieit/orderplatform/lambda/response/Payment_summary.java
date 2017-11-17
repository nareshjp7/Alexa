package com.moxieit.orderplatform.lambda.response;

import java.util.Arrays;

public class Payment_summary {

	private String currency;
	private String payment_type;
	private String is_test_payment;
	private String merchant_name;
	Requested_user_info[] requested_user_info;
	Price_list[] price_list;
	private String shipping_address;
	private String contact_name;
	private String contact_phone;
	private String contact_email;

	public Price_list[] getPrice_list() {
		return price_list;
	}

	public void setPrice_list(Price_list[] price_list) {
		this.price_list = price_list;
	}

	public Requested_user_info[] getRequested_user_info() {
		return requested_user_info;
	}

	public void setRequested_user_info(Requested_user_info[] requested_user_info) {
		this.requested_user_info = requested_user_info;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getPayment_type() {
		return payment_type;
	}

	public void setPayment_type(String payment_type) {
		this.payment_type = payment_type;
	}

	public String getIs_test_payment() {
		return is_test_payment;
	}

	public void setIs_test_payment(String is_test_payment) {
		this.is_test_payment = is_test_payment;
	}

	public String getMerchant_name() {
		return merchant_name;
	}

	public void setMerchant_name(String merchant_name) {
		this.merchant_name = merchant_name;
	}

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
		return "Payment_summary [currency=" + currency + ", payment_type=" + payment_type + ", is_test_payment="
				+ is_test_payment + ", merchant_name=" + merchant_name + ", requested_user_info="
				+ Arrays.toString(requested_user_info) + ", shipping_address=" + shipping_address + ", contact_name="
				+ contact_name + ", contact_phone=" + contact_phone + ", contact_email=" + contact_email + "]";
	}

}
