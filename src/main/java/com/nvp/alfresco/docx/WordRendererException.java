package com.nvp.alfresco.docx;

public class WordRendererException extends Exception {
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WordRendererException(String message) {
		super(message);
	}
	
	public WordRendererException(Throwable cause) {
		super(cause);
	}
	
	public WordRendererException(String message, Throwable cause) {
		super(message, cause);
	}
}
