package com.nvp.alfresco.docx;

import java.util.HashMap;

import com.mwt.word.mapper.ServiceContractMapper;

public class MapperFactory {

	private static HashMap<String, String>  mapperList = null;
	
	private static void setupMapper() {
		
		mapperList = new HashMap<String, String>();
		mapperList.put(ServiceContractMapper.TARGET_NAMESPACE, ServiceContractMapper.class.getName());

	}
	
	public static IMapper getMapperForNamespaceURI(String namespaceURI) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		
		if (mapperList == null) { setupMapper(); }
		
		String className = mapperList.get(namespaceURI);
		@SuppressWarnings("unchecked")
		Class<Mapper> clazz = (Class<Mapper>) Class.forName(className);
		IMapper mapper = (IMapper) clazz.newInstance();
		return mapper;
	}
}
