package com.nvp.util;

import java.time.LocalDate;
import java.util.Date;

import org.activiti.engine.delegate.DelegateExecution;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.PermissionService;

public class ProcessUtil {

	/**
	 * returns a date the given number of serviceLevelDays,
	 * Saturday and Sunday are counted as non working days
	 * 
	 * @param serviceLevelDays
	 * @return the date
	 */
	public static Date getServiceLevelDate(int workdays) {
		
		LocalDate date = LocalDate.now();
		int dayOfWeek = date.getDayOfWeek().getValue();
		int daysToAdd;
		
		if (dayOfWeek < 6) { // date is a workday
			daysToAdd = workdays + (workdays + dayOfWeek - 1) / 5 * 2;
	    } else { // date is a weekend
	    	daysToAdd = workdays + (workdays - 1) / 5 * 2 + (7 - dayOfWeek);
	    }
		
		return java.sql.Date.valueOf(date.plusDays(daysToAdd));
		
	}
	
	/**
	 * 
	 * returns the package node ref associated with a delegate execution of a process
	 * 
	 * @param exec
	 * @return
	 */
	public static NodeRef NodeReffromBPMPackage(DelegateExecution exec) {
		
		final String bpm_package = "bpm_package";
        if(!exec.hasVariable(bpm_package) || exec.getVariable(bpm_package).toString().isEmpty()) {
        	
			System.out.println("WARNING: This process does not have a bpm_package associated with it");
			return null;

        }

		ActivitiScriptNode n = (ActivitiScriptNode) exec.getVariable(bpm_package);
		return n.getNodeRef();
	}
	
	public static void giveUserReadPackageAccess(ServiceRegistry registry, String username, Integer processId) {
		
		
	}
	
    public static void removeUserReadPackageAccess(ServiceRegistry registry, String username, Integer processId) {
		
    	
	}
    
	public static void giveUserWritePackageAccess(ServiceRegistry registry, String username, DelegateExecution exec) {
		
		ProcessUtil.setPackageAccess(registry, username, exec, PermissionService.WRITE);
		
	}
	
    public static void removeUserWritePackageAccess(ServiceRegistry registry, String username, DelegateExecution exec) {
		
    	ProcessUtil.deletePackageAccess(registry, username, exec, PermissionService.WRITE);
    	
	}
    
    private static void setPackageAccess( ServiceRegistry registry, String username, DelegateExecution exec, String access) {
    	
    	NodeRef packageNodeRef = null;
    	packageNodeRef = ProcessUtil.NodeReffromBPMPackage(exec);
    	
    	if(null != packageNodeRef) {
    		
    	  PermissionService permissionService = registry.getPermissionService();
    	  permissionService.setPermission(packageNodeRef, username, access, Boolean.TRUE);
    
    	}
    }
    
    private static void deletePackageAccess( ServiceRegistry registry, String username, DelegateExecution exec, String access) {
    	
    	NodeRef packageNodeRef = null;
    	packageNodeRef = ProcessUtil.NodeReffromBPMPackage(exec);
    	
    	if(null != packageNodeRef) {
    		
    	  PermissionService permissionService = registry.getPermissionService();
    	  permissionService.deletePermission(packageNodeRef, username, access);
    
    	}
    }


}
