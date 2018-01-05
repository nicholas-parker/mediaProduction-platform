package com.nvp.util;

import java.io.Serializable;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;

public class NodeRefUtil {

	public static String UUIDfromURL(String URL) {
		return URL.substring(24,60);
	}
	
	public static NodeRef NodeReffromUUID(String UUID) {
		return new NodeRef("workspace", "SpacesStore", UUID);
	}
	
	public static NodeRef NodeReffromURL(String URL) {
		return new NodeRef("workspace", "SpacesStore", UUIDfromURL(URL));
	}
	
	public static NodeRef NodeReffromBPMPackage(DelegateExecution exec) {
		
		final String bpm_package = "bpm_package";
        if(!exec.hasVariable(bpm_package) || exec.getVariable(bpm_package).toString().isEmpty()) {
        	
			System.out.println("WARNING: This process does not have a bpm_package associated with it");
			return null;

        }

		ActivitiScriptNode n = (ActivitiScriptNode) exec.getVariable(bpm_package);
		return n.getNodeRef();
	}

	public static void mergeNodeProperties(NodeRef nodeRef, Map<QName, Serializable> newProps, NodeService nodeService) {
		
		Map<QName, Serializable> origProps = nodeService.getProperties(nodeRef);
		for(Map.Entry<QName, Serializable> entry: newProps.entrySet()) {
			
			if(origProps.containsKey(entry.getKey())) {
				origProps.remove(entry.getKey()); }
		    origProps.put(entry.getKey(), entry.getValue());
			}
		nodeService.setProperties(nodeRef, origProps);	
	 }
}
