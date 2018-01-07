package com.mwt.activiti;

import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.context.Context;
import org.alfresco.repo.workflow.activiti.ActivitiConstants;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.namespace.QName;

import com.mwt.contract.activiti.ContractInvitation;
import com.nvp.util.MapperUtil;

public class AbstractAlfrescoListener {

	protected static MapperUtil mapperUtil = null;
	
	protected DelegateExecution getExecution(DelegateTask task) {
	
		return task.getExecution();
	}
	
	protected ServiceRegistry getServiceRegistry() {
        ProcessEngineConfigurationImpl config = Context.getProcessEngineConfiguration();
        if (config != null) {
            // Fetch the registry that is injected in the activiti spring-configuration
            ServiceRegistry registry = (ServiceRegistry) config.getBeans().get(ActivitiConstants.SERVICE_REGISTRY_BEAN_KEY);
            if (registry == null) {
                throw new RuntimeException(
                            "Service-registry not present in ProcessEngineConfiguration beans, expected ServiceRegistry with key" + 
                            ActivitiConstants.SERVICE_REGISTRY_BEAN_KEY);
            }
            return registry;
        }
        throw new IllegalStateException("No ProcessEngineCOnfiguration found in active context");
    }
	

	/**
	 * returns true if the workflow contains this variable
	 * 
	 * @name name of the property to check for
	 * @return returns true if the property exists
	 * 
	 */
	protected Boolean hasVariable(String name, DelegateTask task) {
	
		return task.hasVariable(name);
		
	}
	
	protected Boolean hasVariable(String name, DelegateExecution exec) {
		
		return exec.hasVariable(name);
		
	}
	
	/**
	 * return true if the workflow contains this variable and it is not null or an empty string
	 * 
	 * @name name of the property to check for
	 * @return returns true if not null or empty
	 * 
	 */
	protected Boolean hasValue(String name, DelegateTask task) {
		
		if(task.hasVariable(name) == Boolean.FALSE) { return Boolean.FALSE; }
		if( null == task.getVariable(name))  { return Boolean.FALSE; }
		String val = task.getVariable(name).toString();
		if(val.isEmpty()) {
			return Boolean.FALSE;
		}
		
		return Boolean.TRUE;
		
	}
	
	protected Boolean hasValue(String name, DelegateExecution exec) {
		
		if(exec.hasVariable(name) == Boolean.FALSE) { return Boolean.FALSE; }
		if( null == exec.getVariable(name))  { return Boolean.FALSE; }
		String val = exec.getVariable(name).toString();
		if(val.isEmpty()) {
			return Boolean.FALSE;
		}
		
		return Boolean.TRUE;
		
	}
	
	
    protected MapperUtil getMapper(){
		
		if(AbstractAlfrescoListener.mapperUtil == null) { 
			AbstractAlfrescoListener.mapperUtil = new MapperUtil();
			AbstractAlfrescoListener.mapperUtil.setNamespaceService(getServiceRegistry().getNamespaceService());
		}
		return AbstractAlfrescoListener.mapperUtil;
		
	}
    
    /**
     * copies a list of task properties to workflow properties
     * 
     * @param properties a list of QName to copy
     */
    protected void CopyToWorkflow(List<QName> properties, DelegateTask task) {
    	
    	DelegateExecution exec = getExecution(task);
    	for(QName qname : properties) {
    		String flatName = this.getMapper().qNameToFlat(qname);
    		
    		if(task.hasVariable(flatName)) {
    		
    			if(exec.hasVariable(flatName)){
        			exec.setVariable(flatName, task.getVariable(flatName));
        		} else {
        			exec.setVariable(flatName, task.getVariable(flatName));
        		}
    			
    		}
    		
    	}
    }

}
