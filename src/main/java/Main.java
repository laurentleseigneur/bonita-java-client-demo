import org.bonitasoft.web.client.BonitaClient;
import org.bonitasoft.web.client.model.ProcessDefinition;
import org.bonitasoft.web.client.model.Session;
import org.bonitasoft.web.client.services.policies.ApplicationImportPolicy;
import org.bonitasoft.web.client.services.policies.OrganizationImportPolicy;
import org.bonitasoft.web.client.services.policies.ProcessImportPolicy;
import org.bonitasoft.web.client.services.policies.ProfileImportPolicy;

import java.io.File;
import java.util.List;

public class Main {

	public static void main(String[] args) throws Exception {
		String bonitaUrl = args[0];
		String username = args[1];
		String password = args[2];

		File organizationFile = getFileFromArgs(args, "organization");
		File profilesFile = getFileFromArgs(args, "profiles");
		File bdmFile = getFileFromArgs(args, "bdmFile");
		File accessControlFile = getFileFromArgs(args, "accessControl");
		File pageZipFile = getFileFromArgs(args, "pageZip");
		File applicationFile = getFileFromArgs(args, "application");
		File processFile = getFileFromArgs(args, "process");

		OrganizationImportPolicy organizationImportPolicy = getOrganizationImportPolicy();
		ProfileImportPolicy profileImportPolicy = getProfileImportPolicy();
		ApplicationImportPolicy applicationImportPolicy = getApplicationImportPolicy();
		ProcessImportPolicy processImportPolicy = getProcessImportPolicy();

		// Create a client
		BonitaClient client = BonitaClient.builder(bonitaUrl).build();

		// Login
		Session session = client.login(username, password);

		// Log Bonita server version
		log("Bonita version:" + session.getVersion());

		//Organization
		client.users().importOrganization(organizationFile, organizationImportPolicy);

		//Profiles
		client.users().importProfiles(profilesFile, profileImportPolicy);

		//BDM
		client.bdm().importBDM(bdmFile);

		// BdmAccessControl
		client.bdm().importBdmAccessControl(accessControlFile);

		// RestApiExtensions
		// Pages
		// Layouts
		// Themes
		client.applications().importPage(pageZipFile);

		// Applications
		client.applications().importApplications(applicationFile, applicationImportPolicy);

		// Processes
		client.processes().importProcess(processFile, processImportPolicy);

		// checks
		List<ProcessDefinition> processDefinitions = client.processes().searchProcesses(0, 20);

		// Logout when done
		client.logout();

	}

	private static File getFileFromArgs(String[] args, String argName) {
		return new File("some file from args");
	}

	private static ProcessImportPolicy getProcessImportPolicy() {
		return ProcessImportPolicy.FAIL_ON_DUPLICATES;
	}

	private static ProfileImportPolicy getProfileImportPolicy() {
		return ProfileImportPolicy.IGNORE_IF_ANY_EXISTS;
	}

	private static ApplicationImportPolicy getApplicationImportPolicy() {
		return ApplicationImportPolicy.FAIL_ON_DUPLICATES;
	}

	private static OrganizationImportPolicy getOrganizationImportPolicy() {
		return OrganizationImportPolicy.FAIL_ON_DUPLICATES;
	}

	private static void log(String message) {
		System.out.println(message);
	}

}
