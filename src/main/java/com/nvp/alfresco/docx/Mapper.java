package com.nvp.alfresco.docx;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.alfresco.service.namespace.QName;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import com.ibm.icu.text.SimpleDateFormat;
import com.mwt.word.mapper.WordMapperException;

public class Mapper implements IMapper{

	/**
	 * A map of XML elements (K) to arrays of Alfresco properties (V) 
	 */
	protected HashMap<javax.xml.namespace.QName, List<org.alfresco.service.namespace.QName>> mapping = null;
	
	protected static DOMImplementation getDOMImpl() throws ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		return db.getDOMImplementation();
	}
	
	protected String docAsString(Document doc) throws WordMapperException {
		
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer;
		
		try{
			transformer = tf.newTransformer();
			StringWriter writer = new StringWriter();
			transformer.transform( new DOMSource(doc), new StreamResult(writer));
			String xmlString = writer.getBuffer().toString();
			return xmlString;
			
		} catch (TransformerException e) {
			
			e.printStackTrace();
			throw new WordMapperException("WordMapperException");
			
		}

	}

	@Override
	public String toXML(Map<QName, Serializable> props) throws WordMapperException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<QName, Serializable> fromXML(String xmlString) throws WordMapperException {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected String getStringValue(Map<QName, Serializable> props, QName key){
		
		String result;
		if(props.containsKey(key)){
			
			Serializable s = props.get(key);
			if(s != null) {
				result = s.toString();
			} else {
				result = null;
			}
			
		} else {
			result = null;
		}
		return result;
	}
	
	protected String getDateValue(Map<QName, Serializable> props, QName key) {
		
		String result;
		SimpleDateFormat dt = new SimpleDateFormat("dd/mm/yyyy");
		
		if(props.containsKey(key)){
			
			Serializable s = props.get(key);
			if(s != null) {
				result = dt.format((Date) s);
			} else {
				result = null;
			}
			
		} else {
			result = null;
		}
		return result;
	}
	
	public HashMap<javax.xml.namespace.QName, List<org.alfresco.service.namespace.QName>> getMap() {
		return this.mapping;
	}
}
