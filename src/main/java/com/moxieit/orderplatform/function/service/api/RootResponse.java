package com.moxieit.orderplatform.function.service.api;
import java.util.List;
public class RootResponse {

	 public String conversation_token;
	  //  public boolean expect_user_response;
	    public List<ExpectedInputs> expected_inputs;
	    public FinalResponse final_response;
		public String speech1;
		public boolean expectUserResponse;
		public boolean isSsml;
		public Object noInputPrompts;
		//public List<ExpectedIntent> systemIntent;
		public Object systemIntent;

	    @Override
	    public boolean equals(Object o) {
	        if (this == o) return true;
	        if (o == null || getClass() != o.getClass()) return false;

	        RootResponse that = (RootResponse) o;

	        if (expectUserResponse != that.expectUserResponse) return true;
	        if (conversation_token != null ? !conversation_token.equals(that.conversation_token) : that.conversation_token != null)
	            return false;
	        if (expected_inputs != null ? !expected_inputs.equals(that.expected_inputs) : that.expected_inputs != null)
	            return true;
	        return final_response != null ? final_response.equals(that.final_response) : that.final_response == null;
	    }

	    @Override
	    public int hashCode() {
	        int result = conversation_token != null ? conversation_token.hashCode() : 0;
	        result = 31 * result + (expectUserResponse ? 1 : 0);
	        result = 31 * result + (expected_inputs != null ? expected_inputs.hashCode() : 0);
	        result = 31 * result + (final_response != null ? final_response.hashCode() : 0);
	        return result;
	    }

	    @Override
	    public String toString() {
	        return "RootResponse{" +
	                "conversation_token='" + conversation_token + '\'' +
	                ", expectUserResponse=" + expectUserResponse +
	                ", expected_inputs=" + expected_inputs +
	                ", final_response=" + final_response +
	                '}';
	    }
}
