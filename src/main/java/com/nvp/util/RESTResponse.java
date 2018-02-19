package com.nvp.util; 

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class RESTResponse {

	public static String STATUS_FIELD = "status";
	public static String MESSAGE_FIELD = "message";
	public static String ITEMS_FIELD = "items";
	
	public static String STATUS_OK = "OK";
	public static String STATUS_FAIL = "FAIL";
	
	JSONObject JSONResponse;
	
	public RESTResponse() {
		this.JSONResponse = new JSONObject();
		this.JSONResponse.put(ITEMS_FIELD, null);
		this.JSONResponse.put(STATUS_FIELD, null);
		this.JSONResponse.put(MESSAGE_FIELD, null);
	}
	
	@SuppressWarnings("unchecked")
	public void setStatusFail(String userMessage) {
		this.JSONResponse.put(STATUS_FIELD, STATUS_FAIL);
		this.JSONResponse.put(MESSAGE_FIELD, userMessage);
		this.JSONResponse.put(ITEMS_FIELD, "");
	}
	
	@SuppressWarnings("unchecked")
	public void setOK() {
		this.JSONResponse.put(STATUS_FIELD, STATUS_OK);
	}
	
	@SuppressWarnings("unchecked")
	public void setItemsOK(JSONArray items) {
		this.JSONResponse.put(ITEMS_FIELD, items);
		this.JSONResponse.put(STATUS_FIELD, STATUS_OK);
	}
	
	@SuppressWarnings("unchecked")
	public void setEntry(Object key, Object value) {
			this.JSONResponse.put(key, value);
	}
	
	public byte[] getBytes() {
		return this.JSONResponse.toJSONString().getBytes();
	}
}
