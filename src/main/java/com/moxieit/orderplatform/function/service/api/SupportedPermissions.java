package com.moxieit.orderplatform.function.service.api;

public enum SupportedPermissions {
	 /**
     * The user's name as defined in the https://developers.google.com/actions/reference/conversation#UserProfile|UserProfile object
     */
    NAME,
    /**
     * The location of the user's current device, as defined in the https://developers.google.com/actions/reference/conversation#Location|Location object.
     */
    DEVICE_PRECISE_LOCATION,
    /**
     * City and zipcode corresponding to the location of the user's current device, as defined in the https://developers.google.com/actions/reference/conversation#Location|Location object.
     */
    DEVICE_COARSE_LOCATION
}
