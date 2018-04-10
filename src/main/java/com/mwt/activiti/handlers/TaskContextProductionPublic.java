package com.mwt.activiti.handlers;

import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

import com.mwt.activiti.AbstractAlfrescoListener;

public class TaskContextProductionPublic  extends AbstractAlfrescoListener implements TaskListener{

	public static String CONTEXT_PRODUCTION_PUBLIC = "CONTEXTPRODUCTIONPUBLIC";
	
	/**
	 * 
	 * Sets the CONTEXT_PRODUCTION_PUBLIC flag for the current task.
	 * Only displayed in a production task list to people who are in the potential group if unassigned
	 * or the group of the person who it has been assigned to
	 * 
	 * 
	 */
	@Override
	public void notify(DelegateTask task) {
		
		TaskService taskService = task.getExecution().getEngineServices().getTaskService();
		taskService.setVariableLocal( task.getId(), CONTEXT_PRODUCTION_PUBLIC, CONTEXT_PRODUCTION_PUBLIC);
		
		task.setCategory(CONTEXT_PRODUCTION_PUBLIC);
		
		
	}


}

