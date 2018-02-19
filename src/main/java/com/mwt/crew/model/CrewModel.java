package com.mwt.crew.model; 
  
import org.alfresco.service.namespace.QName;

public class CrewModel {
	
	public static String namespaceURI = "http://nvp.com/model/mediaProduction/1.0";
    public static String containerName = "crewContainer";
    
	public static String CREW_CONTAINER_NAME = "Crew";
	public static String CREW_CONTAINER_ID = "crewContainer";
	public static QName QN_CREW_CONTAINER_TYPE = QName.createQName(namespaceURI, containerName);
	
}
