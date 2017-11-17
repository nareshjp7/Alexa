package com.moxieit.orderplatform.function.service.api;

import com.amazonaws.services.lambda.runtime.Context;
import com.moxieit.orderplatform.lambda.response.BaseResponse;

public interface AlexaService {

	public BaseResponse serveLex(AlexaDTO alexaDTO, Context context);
}
