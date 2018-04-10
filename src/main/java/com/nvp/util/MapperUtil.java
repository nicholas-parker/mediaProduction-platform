package com.nvp.util;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.task.Task;
import org.activiti.engine.delegate.DelegateExecution;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.json.simple.JSONObject;

import com.mwt.roles.DefaultRoleModel;


public class MapperUtil { 

	private HashMap<String, String> nsPrefixes = new HashMap<String, String>();
	private NamespaceService nsService = null;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	private static String SEPARATOR = "_";
	
	public MapperUtil() { }
	
	public MapperUtil(NamespaceService nsService) {
		this.nsService = nsService;
	}
	
	public void setNamespaceService(NamespaceService nsService) {
	
		this.nsService = nsService;
		   
	}
	
	/**
	 * Given a QName returns a string which represents the flat name
	 * 
	 * @param qname
	 * @return
	 */
	public String qNameToFlat(QName qname) {
		
		if(!nsPrefixes.containsKey(qname.getNamespaceURI())){
			Collection<String> prefixes = nsService.getPrefixes(qname.getNamespaceURI());
			if(!prefixes.isEmpty()){
				String[] nsP = new String[2];
				prefixes.toArray(nsP);
				nsPrefixes.put(qname.getNamespaceURI(), nsP[0]);
			}
		}
		StringBuilder concatName = new StringBuilder();
		concatName.append(nsPrefixes.get(qname.getNamespaceURI()));
		concatName.append(SEPARATOR);
		concatName.append(qname.getLocalName());
		return concatName.toString();
	}
	
	/**
	 * 
	 * Given a QName extract that variable from the workflow and returns as an Object
	 * 
	 * @param qname
	 * @return
	 */
	public Object getExecVar(DelegateExecution exec, QName var) {
		
		String flatName = qNameToFlat(var);
		if(exec.hasVariable(flatName)) {
			return exec.getVariable(flatName);
		} else {
			return null;
		}
	}
	
	/**
	 * Given a flat property reference, such as cm_firstname
	 * returns a QName
	 * 
	 * @param prop
	 * @return
	 */
	//TODO - check that name and suffix are valid 
	public QName flatToQName(String prop) {
		
		int separatorPosn = prop.indexOf(SEPARATOR);
		String name = prop.substring(separatorPosn+1, prop.length());
		String suffix = prop.substring(0, separatorPosn);
		
		String nsURI = nsService.getNamespaceURI(suffix);
		
		return QName.createQName(nsURI, name);
	}
	
	/**
	 * Given a list of <QName, Serializable> returns a HashMap of flattened values suitable for use in Javascript
	 * 
	 * @param input
	 * @return Map<String, String>
	 */
	public Map<String, String> QNameListToFlatMap(Map<QName, Serializable> input) {
		
		Map<String, String> output = new HashMap<String,String>();
		
		Iterator it = input.entrySet().iterator();
		String value;
		while(it.hasNext()) {
			@SuppressWarnings("unchecked")
			Map.Entry<QName, Serializable> entry = (Map.Entry<QName, Serializable>)it.next();
			String flatName = qNameToFlat(entry.getKey());
			if(entry.getValue() != null) {
			  value = entry.getValue().toString();}
			else {
			  value = ""; }
			  output.put(flatName, value);	
		}
		return output;
		
	}
  
		
	/**
	 * 
	 * Extracts a specific QNamed property from a workflow execution and places it into a map
	 * 
	 * @param map
	 * @param property
	 * @param exec
	 */
	public void taskToMap(Map<QName, Serializable> map, QName property, DelegateExecution exec) {
		taskToMap(map, property, exec, property);
	}
	
	/**
	 * 
	 * Extracts a specific QNamed property from a workflow execution and places it into a map
	 * but under a different property name
	 * 
	 * @param map
	 * @param property
	 * @param exec
	 */
	public void taskToMap(Map<QName, Serializable> map, QName targetProperty, DelegateExecution exec, QName sourceProperty) {
		String sourceFlatName = qNameToFlat(sourceProperty);
		String value = null;
		if(exec.hasVariable(sourceFlatName)) {
			
			System.out.println("MapperUtil: extracting " + sourceFlatName + " from workflow " + exec.getProcessDefinitionId() + " / " + exec.getCurrentActivityName() + " / " + exec.getProcessInstanceId());
		    Object val = exec.getVariable(sourceFlatName);
		    if(null !=val && val.getClass().equals(java.util.Date.class)){
		    	value = dateFormat.format((Date) val);
		    } else if (null !=val && val.getClass().equals(java.lang.Boolean.class)) {
		    	Boolean b = (Boolean) exec.getVariable(sourceFlatName);
		        value = b.toString();
		    } else {
		    	value = (String) exec.getVariable(sourceFlatName);
		    }
		  map.put(targetProperty, value);

		} else {
	
			StringBuilder sb = new StringBuilder();
			sb.append("MapperUtil: couldn't find property ");
			sb.append(sourceFlatName);
			sb.append(" in workflow ");
			sb.append(exec.getProcessInstanceId());
			sb.append( " / ");
			sb.append(exec.getCurrentActivityName());
			System.out.println(sb.toString());
	    }
	}
	
	
	/**
	 * Extract a property from a workflow task and put them into a map
	 * 
	 * @param map the target map which the form fields will be placed into
	 * @param property the QName of the property which exists in the form and is to be copied into the map
	 * @param task the workflow task containing variables which we want to put into a map
	 */
	public void taskToMap(Map<QName, Serializable> map, QName property, DelegateTask task) {
		taskToMap(map, property, task, property);
	}
	
	/**
	 * Extract properties from a workflow task and put them into a map as 
	 * different property names
	 * 
	 * @param map the target map we wish to copy properties into
	 * @param targetProperty the name of the property to be copied into the map
	 * @param task the workflow task which has the properties we want
	 * @param sourceProperty the name of the property in the workflow task
	 */
	public void taskToMap(Map<QName, Serializable> map, QName targetProperty, DelegateTask task, QName sourceProperty) {

		String flatName = qNameToFlat(sourceProperty);
		String value = null;
		if(task.hasVariable(flatName)) {
		    Object val = task.getVariable(flatName);
		    if(null !=val && val.getClass().equals(java.util.Date.class)){
		    	value = dateFormat.format((Date) val);
		    } else if (null !=val && val.getClass().equals(java.lang.Boolean.class)) {
		    	Boolean b = (Boolean) task.getVariable(flatName);
		        value = b.toString();
		    } else {
		    	value = (String) task.getVariable(flatName);
		    }
			
		  map.put(targetProperty, value);}
	}
	
	/**
	 * Returns a map of QName, Serializable which corresponds to the values in the
	 * supplied JSON object
	 * 
	 * @param json
	 * @return
	 */
	public Map<QName, Serializable> JSONToMap(JSONObject json) {
		
		Map<QName, Serializable> result = new HashMap<QName, Serializable>();
		Set set = json.entrySet();
		//Collection c = json.values();
		
		try {
		
			Iterator it = set.iterator();
			while( it.hasNext() ) {
				Map.Entry pair = (Map.Entry) it.next();
				QName qname = flatToQName(pair.getKey().toString());
				result.put(qname, pair.getValue().toString());
			}
			
		} catch (Exception e) {
			
			throw e;
			
		}
		
		return result;
	}
	
	public static void printVars(Map<String, Object> vars) {
		
		Iterator<Entry<String, Object>> it = vars.entrySet().iterator();
		while(it.hasNext()) {
		    @SuppressWarnings("rawtypes")
			Map.Entry pair = (Map.Entry) it.next();
			System.out.println(pair.getKey() + " = " + pair.getValue());
		}
	}
	
    public static void printQNameVars(Map<QName, Serializable> vars) {
		
		Iterator<Entry<QName, Serializable>> it = vars.entrySet().iterator();
		while(it.hasNext()) {
		    @SuppressWarnings("rawtypes")
			Map.Entry pair = (Map.Entry) it.next();
			System.out.println(pair.getKey() + " = " + pair.getValue());
		}
	}
    
    public static void copyToMap(Map<QName, Serializable> targetMap, QName targetKey, Map<QName, Serializable> sourceMap, QName sourceKey, Serializable nullValue) {
    	
    	if(sourceMap.containsKey(sourceKey) && null != sourceMap.get(sourceKey)) {
    	      targetMap.put(targetKey , sourceMap.get(sourceKey).toString());}
    	  	else {
    	  	  targetMap.put(targetKey , nullValue);	
    	  	}
    }
	
    /**
     * 
     * get a string representation of a map value, if map does not contain value return an empty string
     * 
     */
    public static String getStringValue(QName key, HashMap<QName, Serializable> map) {
    	
    	if(map.containsKey(key)) {
    		
    		return map.get(key).toString();
    		
    	} else {
    		
    		return "";
    	}
    }
}
