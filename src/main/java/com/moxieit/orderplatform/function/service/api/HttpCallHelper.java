package com.moxieit.orderplatform.function.service.api;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moxieit.orderplatform.lambda.response.HttpResponse;

public class HttpCallHelper {

	private static Logger logger = LoggerFactory.getLogger(HttpCallHelper.class);

	public static void main(String[] args) {
		HttpCallHelper httpCallHelper = new HttpCallHelper();
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");
		Map<String, String> params = new HashMap<>();
		params.put("access_token",
				"EAABze94b1PwBAK3CRRBZATqsqWhUQKzU50POQHNZA0TohVrdZCQnIdw6i6P1yCFzKZCf7ncbxWmcadZBgez8WeWDoKUtEeNrbdS6BMDC1bjJ9XLpXXp30QZAHKtLPaXkmr3b4bbXMMkNbU61Ti4IvBb4XsvcxpFWcqBuF9McBUKwZDZD");
		HttpResponse doPostCall = httpCallHelper.doPostCall(headers, "https://graph.facebook.com/v2.6/me/messages",
				params);
		System.out.println(doPostCall);
	}

	private HttpResponse populateHttpResponse(String endPoint, CloseableHttpResponse response, long startTime1)
			throws IOException {
		HttpResponse HttpResponse = new HttpResponse();
		logger.info("time taken for GET URI: " + endPoint + " is:" + (System.currentTimeMillis() - startTime1));
		HttpEntity entity = response.getEntity();
		int statusCode = response.getStatusLine().getStatusCode();
		HttpResponse.setHttpStatus(statusCode);
		if (statusCode >= HttpStatus.SC_BAD_REQUEST) {
			logger.error("connection problem with url {} , code: {} ", endPoint, statusCode);
		}
		if (entity != null) {
			HttpResponse.setResponseBody(EntityUtils.toString(entity));
		} else {
			logger.error("empty response");
		}
		return HttpResponse;
	}

	private void populateRequestParams(Map<String, String> params, URIBuilder uriBuilder) {
		if (MapUtils.isNotEmpty(params)) {
			for (Entry<String, String> entry : params.entrySet()) {
				uriBuilder.addParameter(entry.getKey(), entry.getValue());
			}
		}
	}

	public HttpResponse doPostCall(Map<String, String> headers, String endPoint, Map<String, String> params) {

		return doPostCall(headers, endPoint, params, null);
	}

	public HttpResponse doPostCall(Map<String, String> headers, String endPoint, Map<String, String> params,
			String requestBody) {
		HttpResponse HttpResponse = new HttpResponse();
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		try {
			URIBuilder uriBuilder = new URIBuilder(endPoint);
			populateRequestParams(params, uriBuilder);

			URI uri = uriBuilder.build();
			HttpPost http = new HttpPost(uri);
			if (StringUtils.isNotBlank(requestBody)) {
				HttpEntity stringEntity = new StringEntity(requestBody);
				http.setEntity(stringEntity);
			}
			populateHeaders(headers, http);

			long startTime1 = System.currentTimeMillis();

			response = httpclient.execute(http);

			logger.info("time taken for POST URI: " + endPoint + " is:" + (System.currentTimeMillis() - startTime1));
			HttpResponse = populateHttpResponse(endPoint, response, startTime1);

		} catch (Exception e) {
			logger.error("", e);
		} finally {
			finallyMethod(httpclient, response);

		}
		return HttpResponse;
	}

	private void populateHeaders(Map<String, String> headers, HttpRequestBase http) {
		if (MapUtils.isNotEmpty(headers)) {
			List<Header> basicHeaders = new ArrayList<Header>();
			for (Entry<String, String> entry : headers.entrySet()) {
				basicHeaders.add(new BasicHeader(entry.getKey(), entry.getValue()));
			}
			http.setHeaders(basicHeaders.toArray(new Header[] {}));
		}
	}

	private void finallyMethod(CloseableHttpClient httpclient, CloseableHttpResponse response) {
		if (response != null) {
			try {
				response.close();
			} catch (IOException e) {
				logger.error("", e);
			}
		}
		try {
			httpclient.close();
		} catch (IOException e) {
			logger.error("", e);
		}
	}
	
	public String doGetCall(Map<String, String> headers, String endPoint, Map<String, String> params) {
		try {
			URIBuilder uriBuilder = new URIBuilder(endPoint);
			populateRequestParams(params, uriBuilder);
			URI uri = uriBuilder.build();
			return doGetCall(uri);
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}

	}

	public HttpResponse doGetCallIvHttpResponse(Map<String, String> headers, String endPoint, Map<String, String> params) {
		HttpResponse HttpResponse = new HttpResponse();
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		try {
			URIBuilder uriBuilder = new URIBuilder(endPoint);
			populateRequestParams(params, uriBuilder);
			URI uri = uriBuilder.build();
			HttpGet http = new HttpGet(uri);
			populateHeaders(headers, http);
			long startTime1 = System.currentTimeMillis();
			response = httpclient.execute(http);
			HttpResponse = populateIvHttpResponse(endPoint, response, startTime1);
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			finallyMethod(httpclient, response);

		}
		return HttpResponse;
	}
	
	public String doGetCall(URI uri) {
		HttpResponse HttpResponse = new HttpResponse();
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		try {
			HttpGet http = new HttpGet(uri);
			long startTime = System.currentTimeMillis();
			response = httpclient.execute(http);
			HttpResponse = populateIvHttpResponse(uri.getHost() + uri.getPath(), response, startTime);
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			finallyMethod(httpclient, response);
		}
		return HttpResponse.getResponseBody();
	}
	

	private HttpResponse populateIvHttpResponse(String endPoint, CloseableHttpResponse response, long startTime1) throws IOException {
		HttpResponse HttpResponse = new HttpResponse();
		logger.info("time taken for GET URI: " + endPoint + " is:" + (System.currentTimeMillis() - startTime1));
		HttpEntity entity = response.getEntity();
		int statusCode = response.getStatusLine().getStatusCode();
		HttpResponse.setHttpStatus(statusCode);
		if (statusCode >= HttpStatus.SC_BAD_REQUEST) {
			logger.error("connection problem with url {} , code: {} ", endPoint, statusCode);
		}
		if (entity != null) {
			HttpResponse.setResponseBody(EntityUtils.toString(entity));
		} else {
			logger.error("empty response");
		}
		return HttpResponse;
	}

}
