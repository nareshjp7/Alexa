package com.moxieit.orderplatform.lambda.function.service.impl;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moxieit.orderplatform.lambda.response.Address;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlexaGetDeviceAddress {	
	/**
	 * This is a small wrapper client around the Alexa Device Address API.
	 */


	    private final Logger log = LoggerFactory.getLogger(AlexaGetDeviceAddress.class);

	    private String deviceId;
	    private String apiAccessToken;
	    private String apiEndpoint;

	    private static final String BASE_API_PATH = "/v1/devices/";
	    private static final String SETTINGS_PATH = "/settings/";
	    private static final String FULL_ADDRESS_PATH = "address";
	    private static final String COUNTRY_AND_POSTAL_CODE_PATH = "address/countryAndPostalCode";

	    /**
	     * Constructor for the AlexaDeviceAddressClient. It will take the device ID, consent token, and api endpoint.
	     * Those values will be used when making requests to the Device Address API.
	     * @param requestDeviceId the deviceId of the device being retrieved.
	     * @param requestApiAccessToken the apiAccessToken used for authorization against the Device Address API.
	     * @param requestApiEndpoint the endpoint of the Device Address API. This could be the address endpoint for NA, EU, etc.
	     */
	    public AlexaGetDeviceAddress(String requestDeviceId, String requestApiAccessToken, String requestApiEndpoint) {
	        deviceId = requestDeviceId;
	        apiAccessToken = requestApiAccessToken;
	        apiEndpoint = requestApiEndpoint;
	    }

	    /**
	     * This method will make a request to the Device Address API path for retrieving the full address.
	     * @return JsonNode the JSON response from the API.
	     * @throws DeviceAddressClientException When the client fails to perform or complete the request
	     */
	    public Address getFullAddress() throws DeviceAddressClientException {
	        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();

	        String requestUrl = apiEndpoint + BASE_API_PATH + deviceId + SETTINGS_PATH + FULL_ADDRESS_PATH;
	        log.info("Request will be made to the following URL: {}", requestUrl);

	        HttpGet httpGet = new HttpGet(requestUrl);

	        httpGet.addHeader("Authorization", "Bearer " + apiAccessToken);

	        log.info("Sending request to Device Address API");
	        Address address;
	        try {
	            HttpResponse addressResponse = closeableHttpClient.execute(httpGet);
	            int statusCode = addressResponse.getStatusLine().getStatusCode();

	            log.info("The Device Address API responded with a status code of {}", statusCode);

	            if (statusCode == HttpStatus.SC_OK) {
	                HttpEntity httpEntity = addressResponse.getEntity();
	                String responseBody = EntityUtils.toString(httpEntity);

	                ObjectMapper objectMapper = new ObjectMapper();
	                address = objectMapper.readValue(responseBody, Address.class);
	            } else if (statusCode == HttpStatus.SC_FORBIDDEN) {
	                log.info("Failed to authorize with a status code of {}", statusCode);
	                throw new UnauthorizedException("Failed to authorize.");
	            } else {
	                String errorMessage = "Device Address API query failed with status code of " + statusCode;
	                log.info(errorMessage);
	                throw new DeviceAddressClientException(errorMessage);
	            }
	        }  catch (IOException e) {
	            throw new DeviceAddressClientException(e);
	        } finally {
	            log.info("Request to Address Device API completed.");
	        }

	        return address;
	    }



}
