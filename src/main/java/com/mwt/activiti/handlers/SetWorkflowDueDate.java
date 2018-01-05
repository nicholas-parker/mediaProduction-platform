package com.mwt.activiti.handlers;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

import com.mwt.activiti.AbstractAlfrescoListener;

public class SetWorkflowDueDate extends AbstractAlfrescoListener implements ExecutionListener{

	/**
	 * this listener should be placed on all start task listeners.
	 * Using the workflow name and the site name calculates the end date of the workflow
	 * 
	 */
	@Override
	public void notify(DelegateExecution arg0) throws Exception {
		
		/**
		 * for the moment there is a fixed SLA of 2 weeks
		 */
		
	   // Date dueDate =  
		
	}

}
