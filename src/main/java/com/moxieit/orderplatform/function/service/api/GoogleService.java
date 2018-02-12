package com.moxieit.orderplatform.function.service.api;

import com.amazonaws.services.lambda.runtime.Context;
import com.moxieit.orderplatform.lambda.response.BaseResponse;

public interface GoogleService {
	
	public BaseResponse serveLex(GoogleDTO googleDTO, Context context);

}
