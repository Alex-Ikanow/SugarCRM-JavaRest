
import java.util.HashMap;
import sugarcrm.SugarCRMRest;

public class sugarRest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HashMap<String, Object> data = new HashMap<String, Object>();
		String path = "http://localhost/sugar";
		String pass = "admin";
		
		SugarCRMRest crm = new SugarCRMRest(path, "evilkook");
		crm.setDebug(true);
		boolean err = crm.Login("admin", "admin");
		System.out.printf("(*)Login Status: %s\n", err);
		int teamid = crm.getUserTeamId();
		System.out.printf("(*)Team Id: %d\n", teamid);
		String[] modules = crm.getAvailableModules();
		
		/*
		for (int i = 0; i <= modules.length -1; i++) {
			crm.getEntriesCount(modules[i], "", true);
		}
		*/
		crm.searchByModule("", modules, 0, 200);
		
	}
}
