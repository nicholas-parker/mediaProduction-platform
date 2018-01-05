package com.mwt.crew;

public class CrewServiceException extends Exception {

	/**
	 * 
	 */  
	private static final long serialVersionUID = 1L;

	public CrewServiceException(){};
	
	public CrewServiceException(String m, Throwable e) {
		super(m, e);
	}
}
