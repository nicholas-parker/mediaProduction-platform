package com.nvp.alfresco.docx;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.service.namespace.QName;

import com.mwt.word.mapper.WordMapperException;

public interface IMapper {

	public String toXML(Map<QName, Serializable> props) throws WordMapperException;
	public Map<QName, Serializable> fromXML(String xmlString)throws WordMapperException;
	public HashMap<javax.xml.namespace.QName, List<org.alfresco.service.namespace.QName>> getMap();
	
}
