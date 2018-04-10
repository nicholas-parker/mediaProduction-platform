package com.mwt.activiti.handlers.roleContract; 

public class RoleContractException extends Exception {

	private static final long serialVersionUID = 1L;

	public RoleContractException(String message) {
		super(message);
	}
	
	public RoleContractException(Throwable cause) {
		super(cause);
	}
	
	public RoleContractException(String message, Throwable cause) {
		super(message, cause);
	}
}
