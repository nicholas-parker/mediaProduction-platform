package com.mwt.roles;

public class ProductionRoleException extends Exception {

	private static final long serialVersionUID = 1L;

	public ProductionRoleException(String message) {
		super(message);
	}
	
	public ProductionRoleException(Throwable cause) {
		super(cause);
	}
	
	public ProductionRoleException(String message, Throwable cause) {
		super(message, cause);
	}
}
