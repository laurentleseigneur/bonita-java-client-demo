import com.bonitasoft.engine.api.APIClient;
import com.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.api.ApiAccessType;
import org.bonitasoft.engine.api.ApplicationAPI;
import org.bonitasoft.engine.bpm.bar.BusinessArchive;
import org.bonitasoft.engine.bpm.bar.BusinessArchiveFactory;
import org.bonitasoft.engine.bpm.bar.InvalidBusinessArchiveFormatException;
import org.bonitasoft.engine.bpm.process.ProcessDefinition;
import org.bonitasoft.engine.bpm.process.ProcessDeployException;
import org.bonitasoft.engine.bpm.process.ProcessDeploymentInfo;
import org.bonitasoft.engine.bpm.process.ProcessEnablementException;
import org.bonitasoft.engine.business.application.Application;
import org.bonitasoft.engine.exception.AlreadyExistsException;
import org.bonitasoft.engine.exception.SearchException;
import org.bonitasoft.engine.platform.LoginException;
import org.bonitasoft.engine.search.SearchOptionsBuilder;
import org.bonitasoft.engine.search.SearchResult;
import org.bonitasoft.engine.util.APITypeManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {
	public static void main(String[] args) throws Exception{
		String bonitaUrl = args[0];
		String pathToJarFile = args[1];

		System.out.println("bonitaUrl:" + bonitaUrl);
		System.out.println("pathToJarFile:" + pathToJarFile);

		// Let's set the connection settings to use HTTP on the already running Bonita runtime:
		Map<String, String> settings = new HashMap<String, String>();
		settings.put("server.url", bonitaUrl);
		settings.put("application.name", "bonita");
		APITypeManager.setAPITypeAndParams(ApiAccessType.HTTP, settings);

		final APIClient apiClient = new APIClient();
		apiClient.login("walter.bates", "bpm");
		final ProcessAPI processAPI = apiClient.getProcessAPI();

		//display deployed processes
		SearchResult<ProcessDeploymentInfo> processDeploymentInfoSearchResult = processAPI.searchProcessDeploymentInfos(new SearchOptionsBuilder(0, 10).done());
		for (ProcessDeploymentInfo processDeploymentInfo : processDeploymentInfoSearchResult.getResult()) {
			System.out.println("found process:" + processDeploymentInfo.getName() + " in version:" + processDeploymentInfo.getVersion());
		}

		//display deployed living application
		ApplicationAPI applicationAPI = apiClient.getApplicationAPI();
		SearchResult<Application> applicationSearchResult = applicationAPI.searchApplications(new SearchOptionsBuilder(0, 10).done());
		for (Application application : applicationSearchResult.getResult()) {
			System.out.println("found deployed application:" + application.getDisplayName() + " with token:" + application.getToken());
		}

		//ensure this process name/version is not yet deployed on target platform
		final BusinessArchive businessArchive = BusinessArchiveFactory.readBusinessArchive(new File(pathToJarFile));
		final ProcessDefinition processDefinition = processAPI.deployAndEnableProcess(businessArchive);
		System.out.println("deployed new version with id:"+ processDefinition.getId());
		System.out.println("deployed new version with name:"+ processDefinition.getName());
		System.out.println("deployed new version with version:"+ processDefinition.getVersion());


	}

}
