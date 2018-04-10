package com.mwt.activiti.handlers;

import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

import com.mwt.activiti.AbstractAlfrescoListener;

public class TaskContextPersonal extends AbstractAlfrescoListener implements TaskListener{

	public static String CONTEXT_PERSONAL = "CONTEXTPERSONAL";
	
	/**
	 * 
	 * Sets the CONTEXT_PERSONAL flag for the current task.
	 * Only displayed in a personal task list
	 * 
	 */
	@Override
	public void notify(DelegateTask task) {
		
		TaskService taskService = task.getExecution().getEngineServices().getTaskService();
		taskService.setVariableLocal( task.getId(), CONTEXT_PERSONAL, CONTEXT_PERSONAL);
		
		task.setCategory(CONTEXT_PERSONAL);
	}


}
