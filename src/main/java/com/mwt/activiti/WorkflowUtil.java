package com.mwt.activiti;

public class WorkflowUtil {

    public static String getWorkflowAdminGroup(String groupName, String siteName){
		
		/** move this mapping to some kind of look up */
		String technicalGroup = null;
		if(groupName.contentEquals("Contracts admin team")){
			technicalGroup = "ContractAdministrator";}
		
		StringBuilder sb = new StringBuilder();
		sb.append("GROUP_site_");
		sb.append(siteName);
		sb.append("_");
		sb.append(technicalGroup);
		return sb.toString();
	}
    
}
