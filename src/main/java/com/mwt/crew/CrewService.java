package com.mwt.crew;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.executer.MailActionExecuter;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.site.SiteModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.MutableAuthenticationService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.security.PersonService.PersonInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang.RandomStringUtils;
import org.alfresco.repo.security.authentication.PasswordGenerator;

import com.mwt.crew.CrewServiceException;
import com.mwt.contract.ContractServiceException;
import com.mwt.contract.model.ContractDocumentModel;
import com.mwt.crew.model.CrewModel;
import com.nvp.util.DocUtil;

public class CrewService {

	private ServiceRegistry registry;
	
    public void setServiceRegistry(ServiceRegistry registry) {
        this.registry = registry;
    }

    /**
     * 
     * Searches all the Alfresco users and returns the first 10 users with the email match
     * 
     * @param email
     * @return
     */
    public Set<NodeRef> getGlobalMembersByEmail(String email) throws CrewServiceException {
    	
    	Set<NodeRef> people = null;
    	try {
    	
    		PersonService personService = this.registry.getPersonService();
        	people = personService.getPeopleFilteredByProperty( ContentModel.PROP_EMAIL, email, 10);
        	
    	} catch (Exception e) {
    		
    		throw new CrewServiceException("Error whilst searching for crew member by email [" + email + "]." + e.getMessage(), e);
    		
    	}
    	
    	return people;
    }
    
    /**
     * 
     * Create a new user account
     * 
     * 
     * @param firstName
     * @param lastName
     * @param email
     * @return returns a map of the person nodeId and the new password
     * 
     */
	public Map<String, String> createNewStandbyMember(final String firstName, final String lastName, final String email) throws CrewServiceException {

		NodeRef person = null;
		final MutableAuthenticationService authenticationService = this.registry.getAuthenticationService();
		final PersonService personService = this.registry.getPersonService();
		
		
		/**
		 * 
		 * check account does not already exist
		 * 
		 */
		if(authenticationService.authenticationExists(email) == true) {
		  throw new CrewServiceException("Cannot create a new account for this member, account " + email + " already exists", null);	
		}

		final String password = getRandomPassword();
		try {
			
			/**
			 * 
			 * create new user account, run as system
			 * 
			 */
			person = (NodeRef) AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
	                                       public Object doWork() throws Exception {

	                                    	   
	                                    	   	                                    	   
	                                    	   authenticationService.createAuthentication(email, password.toCharArray());

	                                    	   Map user = new HashMap();
	                                    	   user.put(ContentModel.PROP_USERNAME, email);
	                                    	   user.put(ContentModel.PROP_FIRSTNAME, firstName);
	                                    	   user.put(ContentModel.PROP_LASTNAME,  lastName);
	                                    	   user.put(ContentModel.PROP_EMAIL, email);
	                                    	   user.put(ContentModel.PROP_JOBTITLE, "jobTitle");

	                                    	   return personService.createPerson(user);

	                                       }
			});

		} catch ( Exception e) {
			
			throw new CrewServiceException("Error creating new account for supplier " ,e);
		
		}
		
        Map<String, String> result = new HashMap<String, String>();
        result.put("nodeId", person.getId()); 
        result.put("password", password);
        return result;
        
	}
	
	/**
	 * Sends an email to the provided email recipient inviting them to log in and review the job
	 * 
	 * @param username
	 * @param firstName
	 * @param lastName
	 * @param jobTitle
	 * @param productionName
	 * @param email
	 * @throws CrewServiceException
	 * 
	 */
	public void sendJobNotificationNewSupplier(String username, String password, String firstName, String lastName, String jobTitle, String productionName, String recipient, String from) throws CrewServiceException {
		
		try {
			
			ActionService actionService = this.registry.getActionService();
		    Action mailAction = actionService.createAction(MailActionExecuter.NAME);

		    final String subject = "New job invitation from " + productionName;
		    
		    /**
		     * temporary content until we use template
		     * 
		     */
		    StringBuilder sb = new StringBuilder();
		    sb.append("Dear ");
		    sb.append(firstName);
		    sb.append("\n\r");
		    sb.append("You have been sent this email because you have been offered the position of ");
		    sb.append(jobTitle);
		    sb.append(" in ");
		    sb.append(productionName);
		    sb.append("\n\r");
		    sb.append("To review the job details please log in to the production portal using the temporary credentials below. \n\r");
		    sb.append("http://localhost:3333/login \n\r");
		    sb.append("Username:");
		    sb.append(username);
		    sb.append("\n\r");
		    sb.append("Password: ");
		    sb.append(password);
		    
            /**
            // Build our model
            Map<String, Serializable> model = new HashMap<String, Serializable>(8, 1.0f);
            model.put("resourceName", siteName);
            model.put("resourceType", resourceType);
            model.put("inviteeRole", role);
            model.put("reviewComments", reviewComments);
            model.put("reviewer", reviewer);
            model.put("inviteeUserName", invitee);

            // Process the template
            // Note - because we use a classpath template, rather than a Data Dictionary
            // one, we can't have the MailActionExecutor do the template for us
            String emailMsg = templateService.processTemplate("freemarker", REJECT_TEMPLATE, model);
            */
		    
		    mailAction.setParameterValue(MailActionExecuter.PARAM_SUBJECT, subject);
		    mailAction.setParameterValue(MailActionExecuter.PARAM_TO, recipient);
		    mailAction.setParameterValue(MailActionExecuter.PARAM_FROM, from);
		    mailAction.setParameterValue(MailActionExecuter.PARAM_TEXT, sb.toString());

		    actionService.executeAction(mailAction, null);
		    
		} catch (Exception e) {
			
			e.printStackTrace();
			throw new CrewServiceException("Error creating new account for supplier " ,e);
			
		}
	}
	
	/**
     * 
     * Adds a new crew member to a site, this consists of
     * - adding the person to the site
     * - creating a crew folder for the member
     * 
     * @param userName
     * @param siteShortName
     * 
     */
	public void addNewCrewToSite(String userName, String siteShortName) throws CrewServiceException {
		
		/**
		 * 
		 * add the person to the site
		 * 
		 */
		
		
		/**
		 * 
		 * add the person to the crew site groups
		 * 
		 */
		
		
		/**
		 * 
		 * create the folder for the crew member
		 * 
		 */
		createCrewFolder(userName, siteShortName);
		
	}

	/**
	 * 
	 * Returns a noderef for the crew members folder in a specific site
	 * 
	 * @param userName
	 * @param siteShortName
	 * @return
	 * @throws CrewServiceException
	 */
	public NodeRef getSiteCrewMemberFolder(String userName, String siteShortName) throws CrewServiceException {
		
		String folderName = getFolderNameForMember(userName);
		NodeRef crewContainer = getSiteCrewContainer(siteShortName);
        
		NodeRef crewMemberFolder = null;
        
		try {
			
			List<String> paths = new ArrayList<String>(1);
			paths.add(folderName);
			FileInfo fileInfo = this.registry.getFileFolderService().resolveNamePath(crewContainer, paths);
			if (!fileInfo.isFolder())
			{
				throw new CrewServiceException("ERROR: looking for Crew container in site " + siteShortName + ", but the item is not a foolder", null);
			}
			crewMemberFolder = fileInfo.getNodeRef();

		} catch( FileNotFoundException fnfe) {

			crewMemberFolder = createCrewFolder(userName, siteShortName);
		
		}
		
		return crewMemberFolder;
		
	}
	
	public void moveDocumentToCrewFolder(String userName, NodeRef sourceContent, String siteShortName) throws CrewServiceException {
		
		NodeRef crewFolder = this.getSiteCrewMemberFolder(userName, siteShortName);
		Map<QName, Serializable> sourceProperties = this.registry.getNodeService().getProperties(sourceContent);
		String name = null;
		if(sourceProperties.containsKey(ContentModel.PROP_NAME)) {
			name = sourceProperties.get(ContentModel.PROP_NAME).toString();
		} else {
			name = "Crew Document";
		}
		
		try {
		
			DocUtil.createDocumentFromOriginalWithProperties(name, sourceContent, crewFolder, this.registry);
		
		} catch (Exception e) {
		
			CrewServiceException cse = new CrewServiceException("Exception whilst copying file into crew member folder, " + e.getMessage(), e );
			cse.printStackTrace();
			throw cse;
			
		}
		
		
	}
	
	public void makeLinkFromCrewFolder(String crewMember, NodeRef targetContent, String siteShortName) {
		
	}

	/**
	 * 
	 * Create a new folder for the crew member in the document library
	 * 
	 * @param personId
	 * @param siteShortName
	 * @throws CrewServiceException
     *
	 */
	private NodeRef createCrewFolder(String userName, String siteShortName) throws CrewServiceException {
		
		NodeRef crewFolder = null;
		NodeRef crewContainer = getSiteCrewContainer(siteShortName);
		
		/**
		 * 
		 * create the folder
		 * 
		 */
		try {
		
			String folderName = getFolderNameForMember(userName);
			FileFolderService fileFolderService = this.registry.getFileFolderService();
			crewFolder = fileFolderService.create(crewContainer, folderName, ContentModel.TYPE_FOLDER).getNodeRef();

			System.out.println("INFO: new crow folder created [" + folderName + "] and nodeRef [" + crewFolder.toString() + "]" );
			
			// TODO - set folder permissions
			
		} catch (Exception e) {
			
			// NO OP
			
		} 
			
		return crewFolder;
		
	}
	
	/**
	 * 
	 * Obtain the NodeRef for the Contracts container for a specific site.
	 * If the container does not exist it is created in the DocumentLibrary
	 * 
	 * @param siteShortName shortName of the site we want the contracts container for
	 * @return NodeRef of the Contracts container
	 * @throws ContractServiceException 
	 */
	private NodeRef getSiteCrewContainer(String siteShortName) throws CrewServiceException {

		SiteService siteService = registry.getSiteService();
		FileFolderService fileFolderService = registry.getFileFolderService();
		NodeService nodeService = registry.getNodeService();
		SearchService searchService = registry.getSearchService();

		NodeRef crewContainer = null;
		NodeRef docLibContainer = siteService.getContainer(siteShortName, SiteService.DOCUMENT_LIBRARY);
		if(docLibContainer == null) {
			throw new CrewServiceException("No document library in this site " + siteShortName, null); }

		try {

			List<String> paths = new ArrayList<String>(1);
			paths.add(CrewModel.CREW_CONTAINER_NAME);
			FileInfo fileInfo = fileFolderService.resolveNamePath(docLibContainer, paths);
			if (!fileInfo.isFolder())
			{
				throw new CrewServiceException("ERROR: looking for Crew container in site " + siteShortName + ", but the item is not a foolder", null);
			}
			crewContainer = fileInfo.getNodeRef();

		} catch( FileNotFoundException fnfe) {

			/*
			 * 
			 * we are here because we didn't find a contracts container so create one
			 * 
			 */
			crewContainer = fileFolderService.create(docLibContainer, 
					CrewModel.CREW_CONTAINER_NAME, 
					CrewModel.QN_CREW_CONTAINER_TYPE).getNodeRef();

			Map<QName, Serializable> containerProperties = new HashMap<QName, Serializable>();
			containerProperties.put(SiteModel.PROP_COMPONENT_ID, CrewModel.CREW_CONTAINER_ID);
			nodeService.addAspect(crewContainer , SiteModel.ASPECT_SITE_CONTAINER, containerProperties);


		} catch( Exception e) {

			CrewServiceException cse = new CrewServiceException("ERROR: very unexpected thing happened whilst searching for contracts container, " + e.getMessage(), null);
			cse.printStackTrace();
			throw cse;

		}

		return crewContainer;
	}

	/**
	 * Creates the new crew folder under the documentLibrary folder
	 * 
	 * @throws CrewServiceException
	 */
	public NodeRef createCrewFolder(String siteShortName) throws CrewServiceException {
		
		SiteService siteService = registry.getSiteService();
		FileFolderService fileFolderService = registry.getFileFolderService();
		NodeService nodeService = registry.getNodeService();
		
		NodeRef crewContainer = null;
		try {
			
			NodeRef docLibContainer = siteService.getContainer(siteShortName, SiteService.DOCUMENT_LIBRARY);
			crewContainer = fileFolderService.create(docLibContainer, 
					CrewModel.CREW_CONTAINER_NAME, 
					CrewModel.QN_CREW_CONTAINER_TYPE).getNodeRef();
	
			Map<QName, Serializable> containerProperties = new HashMap<QName, Serializable>();
			containerProperties.put(SiteModel.PROP_COMPONENT_ID, CrewModel.CREW_CONTAINER_ID);
			nodeService.addAspect(crewContainer , SiteModel.ASPECT_SITE_CONTAINER, containerProperties);
		
		} catch (Exception e) {

			CrewServiceException cse = new CrewServiceException("ERROR: very unexpected thing happened whilst creaeting crew container in site  [" + siteShortName + "], " + e.getMessage(), null);
			cse.printStackTrace();
			throw cse;
		}
		
		return crewContainer;
	}
	
	private String getFolderNameForMember(String userName) throws CrewServiceException {
		
		PersonService personService = this.registry.getPersonService();
		NodeRef personNode = personService.getPerson(userName);
		PersonInfo personInfo = personService.getPerson(personNode);
		
		StringBuilder folderName = new StringBuilder();
		folderName.append(personInfo.getFirstName());
		folderName.append(" ");
		folderName.append(personInfo.getLastName());
		
		return folderName.toString();
	}
	
	/**
	 * 
	 * returns a random password
	 * 
	 */
	public String getRandomPassword() {
		
		// PasswordGenerator gen = new org.alfresco.repo.security.authentication.BasicPasswordGenerator();
		// gen.setPasswordLength(8);
		
		return RandomStringUtils.randomAlphanumeric(8);
		
	}
}
