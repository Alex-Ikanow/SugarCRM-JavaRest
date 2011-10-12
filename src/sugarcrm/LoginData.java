/*
Copyright 2011 SugarCRM Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License. 
You may may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0 
   
Unless required by applicable law or agreed to in writing, software 
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
Please see the License for the specific language governing permissions and 
limitations under the License.
*/

package sugarcrm;

import java.util.HashMap;

public class LoginData {
	private String id;
	private String module_name;
	private HashMap<String, HashMap<String, String>> name_value_list;
	
	public HashMap<String, String> getData() {
		HashMap<String, String> result = new HashMap<String, String>();
		String[] keys = this.name_value_list.keySet().toArray(new String[0]);
		
		for (int i = 0; i <= keys.length -1; i++) {
			HashMap<String, String> tmp = this.name_value_list.get(keys[i]);
			String value = tmp.get("value");
			result.put(keys[i], value);
		}
		
		result.put("id", this.id);
		result.put("module_name", this.module_name);
		
		return result;
	}
	
}
