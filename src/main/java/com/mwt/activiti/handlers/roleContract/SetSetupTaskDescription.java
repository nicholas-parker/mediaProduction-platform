package com.mwt.activiti.handlers.roleContract;

import java.time.LocalDate;
import java.util.Date;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

import com.mwt.activiti.AbstractAlfrescoListener;
import com.nvp.util.ProcessUtil;

public class SetSetupTaskDescription  extends AbstractAlfrescoListener implements TaskListener{

	/**
	 * 
	 *  Sets the task description.
	 *  This task is executed immediately after setup or when the candidate has declined the role.
	 *  The description of the task changes depending on if the candidate has declined the role or if this is the first time. 
	 * 
	 */
	@Override
	public void notify(DelegateTask task) {
		
		/**
		 * 
		 * the name of the task variable which the candidate will set to indicate the review outcome
		 * 
		 */
		final String candidateReviewOutcome = "candidateReviewOutcome";
		
		Boolean firstTime = Boolean.FALSE;
		Boolean declinedTime = Boolean.FALSE;
		
		
		
		/**
		 * 
		 * work out if this task is created because of a candidate declination
		 * 
		 */
		if(this.hasVariable(candidateReviewOutcome, task)) {
			
			if(task.getVariable(candidateReviewOutcome) == null ) {
				
				firstTime = Boolean.TRUE;
				
			}
			
            if(task.getVariable(candidateReviewOutcome) != null && task.getVariable(candidateReviewOutcome).toString() == "Declined" ) {
				
				declinedTime = Boolean.TRUE;
				
			}
			
		} else {
			
			firstTime = Boolean.TRUE;
			
		}
		
		/**
		 * 
		 * if first time then set the task details
		 *
		 */
		if(firstTime) {
			
			StringBuilder description = new StringBuilder();
			description.append(task.getVariable("contract_serviceName").toString());
			description.append(": complete the offer details , select the candidate and submit the offer to the candidate");
			task.setDescription(description.toString());

			Date dueDate = ProcessUtil.getServiceLevelDate(7);
			task.setDueDate(dueDate);
			
			task.setName("Complete offer");
			
		}
		
		/**
		 * 
		 * if candidate declined the set the task details
		 * 
		 */
		if(declinedTime) {

			StringBuilder description = new StringBuilder();
			description.append(task.getVariable("contract_serviceName").toString());
			description.append(": candidate has declined the position, ammend the offer or forward to an alternative candidate.");
			task.setDescription(description.toString());
			
			Date dueDate = ProcessUtil.getServiceLevelDate(3);
			task.setDueDate(dueDate);
			
			task.setName("Ammend offer");

		}
		
	}
	
}
