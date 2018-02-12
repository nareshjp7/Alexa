package com.moxieit.orderplatform.function.service.api;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonSetter;


public class PermissionValueSpec {
	public String opt_context;
    public String[] permissions;
   
   /* Gson gson2 = new Gson();
	String jsonString = new Gson().toJson("@type");*/
    
    @SerializedName("@type")
    public String type;


  

	public String optContext;



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PermissionValueSpec that = (PermissionValueSpec) o;

        if (opt_context != null ? !opt_context.equals(that.opt_context) : that.opt_context != null) return false;
        return permissions != null ? permissions.equals(that.permissions) : that.permissions == null;
    }

    @Override
    public int hashCode() {
        int result = opt_context != null ? opt_context.hashCode() : 0;
        result = 31 * result + (permissions != null ? permissions.hashCode() : 0);
        return result;
    }

   @Override
    public String toString() {
        return "{@type: type.googleapis.com/google.actions.v2.PermissionValueSpec,optContext: To pick you up,permissions: [NAME, DEVICE_PRECISE_LOCATION    ]    }";
   
    }

	 /* public String toString() {
	        return "{    speech: PLACEHOLDER_FOR_PERMISSION,    data: { google: {  expectUserResponse: true,  isSsml: false,"
	        		+ "            noInputPrompts: [],"
	        		+ "            systemIntent: {                intent: actions.intent.PERMISSION,                data: {"
	        		+ "                    @type: type.googleapis.com/google.actions.v2.PermissionValueSpec,"
	        		+ "                    optContext: To pick you up,       "
	        		+ "             permissions: [                  "
	        		+ "      NAME,                        DEVICE_PRECISE_LOCATION         ]         }       }        }    }}";
	   
	    }*/

}
