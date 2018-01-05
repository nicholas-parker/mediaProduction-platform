package com.nvp.util;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

public class WebscriptUtil {

	public static JSONObject getJsonPost(WebScriptRequest req) throws IOException, ParseException {
		
		JSONParser parser = new JSONParser();
        Object jsonO = parser.parse(req.getContent().getReader());
        JSONObject JsonPost;
        if (jsonO instanceof JSONObject && jsonO != null)
        {
             JsonPost = (JSONObject)jsonO;
        }
        else
        {
             throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Wrong JSON type found " + jsonO);
        }
        
        return JsonPost;
	}
}
