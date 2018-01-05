package com.mwt.contract.activiti;

import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.alfresco.repo.site.SiteModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.invitation.Invitation;
import org.alfresco.service.cmr.invitation.InvitationService;
import org.alfresco.service.cmr.invitation.NominatedInvitation;
import org.alfresco.service.namespace.QName;

import com.mwt.activiti.AbstractAlfrescoListener;
import com.mwt.contract.model.ContractDocumentModel;
import com.nvp.util.MapperUtil;


public class ContractInvitation extends AbstractAlfrescoListener implements JavaDelegate {

	private static String ACCEPT_INVITE_PATH = "/page/accept-invite";
	private static String REJECT_INVITE_PATH = "/page/reject-invite";
	private static String INVITATION_TYPE = "NOMINATED";
	private static String INVITEE_ROLE_NAME = "SiteCollaborator";
	
	private static MapperUtil mapperUtil = null;
	
	private ServiceRegistry registry = null;
	
	public void setRegistry(ServiceRegistry registry) {
	
		this.registry = registry;
		mapperUtil.setNamespaceService(registry.getNamespaceService());
		
	} 
	
	public MapperUtil getMapper(){
		
		if(ContractInvitation.mapperUtil == null) { 
			ContractInvitation.mapperUtil = new MapperUtil();
			ContractInvitation.mapperUtil.setNamespaceService(getServiceRegistry().getNamespaceService());
		}
		return ContractInvitation.mapperUtil;
		
	}
	
	@Override
	public void execute(DelegateExecution arg0) throws Exception {
		
		String firstName = "";
		String lastName = "";
		String newEmail = "";
		String siteShortName = "";
		
		try {
			
		Map varLocal = arg0.getVariablesLocal();
		Map var = arg0.getVariables();
 		MapperUtil mapper = this.getMapper();
 		
		firstName = (String) arg0.getVariable("contract_supplierFirstName");
		lastName = (String) arg0.getVariable("contract_supplierLastName");
		newEmail = (String) arg0.getVariable("contract_supplierEmail");
		siteShortName = (String) arg0.getVariable("site");
		
		InvitationService invitationService = getServiceRegistry().getInvitationService();
		NominatedInvitation invitation = invitationService.inviteNominated(firstName, lastName, newEmail,
				                                                           Invitation.ResourceType.WEB_SITE,
				                                                           siteShortName,
				                                                           INVITEE_ROLE_NAME, 
				                                                           ACCEPT_INVITE_PATH, 
				                                                           REJECT_INVITE_PATH);


		String userName = invitation.getInviteeUserName();
		arg0.setVariable(mapper.qNameToFlat(ContractDocumentModel.QN_SUPPLIER), userName);
		
		System.out.print("Invited new candidate to site");
		
		} catch (Exception e) {
			
			System.out.print("Error inviting candidate to site [" + siteShortName + "] [" + firstName + "] [" + lastName + "] [" + newEmail + "]");
			System.out.print(e);
		}


		
	}
	
	  
}
