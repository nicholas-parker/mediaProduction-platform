package com.mwt.contract;
 
public class ContractServiceException extends Exception {

	/**
	 * 
	 */  
	private static final long serialVersionUID = 1L;

	public ContractServiceException(){};
	
	public ContractServiceException(String m, Throwable e) {
		super(m, e);
	}
}
