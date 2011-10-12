
import java.util.ArrayList;
import java.util.HashMap;

import sugarcrm.ModuleSearchResults;
import sugarcrm.SugarCRMRest;

public class sugarRest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
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
		ModuleSearchResults modres = crm.searchByModule("foobar", modules, 0, 200);
		String[] keys = modres.keySet().toArray(new String[0]);
		for (int i = 0; i <= keys.length -1; i++) {
			System.out.printf("[DATA]: %s\n", keys[i]);
			ArrayList<HashMap<String, String>> rows = modres.get(keys[i]);
			
			for (int rowIndex = 0; rowIndex <= rows.size() -1; rowIndex++) {
				System.out.printf("[ROW %d]\n", rowIndex);
				HashMap<String, String> data = rows.get(rowIndex);
				String[] dkeys = data.keySet().toArray(new String[0]);
				for (int x = 0; x <= dkeys.length -1; x++) {
					System.out.printf("--)%s => %s\n", dkeys[x], data.get(dkeys[x]));
				}
			}
			
		}
		
		
	}
}
