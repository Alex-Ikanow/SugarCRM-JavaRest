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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;

public class SugarCRMRest {

	private String baseURL = "service/v2_1/rest.php";
	private String appName = "EvilKook";
	private boolean debug = false;
	private LoginData loginData = null;
	private ErrorData loginErrorData = null;
	private String sessionID = null;
	private ErrorData lastError = null;
	
	public SugarCRMRest(String serviceURL, String appName) {
		
		if (serviceURL.endsWith("/")) {
			serviceURL = serviceURL.substring(0,serviceURL.length() -2);
		}
		
		this.baseURL = String.format("%s/%s",serviceURL, this.baseURL);
		
		print("BASE: " + this.baseURL);
		
		if (appName != null && appName.length() > 0) {
			this.appName = appName;
		}
	}
	
	public ErrorData getLastError() {
		return this.lastError;
	}
	
	private void setLastError(ErrorData err) {
		this.lastError = err;
	}
	
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	public boolean Login(String username, String password) {
		boolean result = false;
		String restPath = String.format("%s?method=login&input_type=json&response_type=json&rest_data=", 
				this.baseURL);
		String strdata;
		String mdpass = this.genPasswordMD5(password);
		Gson json = new Gson();

		strdata = String.format("{"+
				"\"user_auth\":{"+
				"\"user_name\":\"%s\","+
				"\"password\":\"%s\","+
				"\"version\":4},"+
				"\"application_name\":\"evilkook\"}", username, mdpass);

		restPath += strdata;
		
		try {
			print("URL: "+restPath);
			URL url = new URL(restPath);
			HttpURLConnection http = (HttpURLConnection)url.openConnection();
			http.setRequestMethod("GET");
			http.setUseCaches(false);
			http.setDoInput(true);
			http.setDoOutput(true);
			InputStream in = http.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line = "";
			String tmp = "";

			while ((line = br.readLine()) != null) {
				tmp += line;
			}
			
			print("Login Response: "+ tmp);
			
			if (tmp.contains("Invalid Login")) {
				this.setLastError(json.fromJson(tmp, ErrorData.class));
				result = false;
				print("Login failed!");
			} else {
				this.loginData = json.fromJson(tmp, LoginData.class);
				result = true;
				this.sessionID = this.loginData.getData().get("id");
				print("Login success!");
				
				if (this.debug) {
					print("Response Data:");
					HashMap<String, String> resdata = this.loginData.getData();
					String[] keys = resdata.keySet().toArray(new String[0]);
					for (int i = 0; i <= keys.length -1; i ++) {
						String value = resdata.get(keys[i]);
						String msg = String.format("--)%s => %s", keys[i], value);
						print(msg);
					}
				}
			}
			
		} catch (Exception exp) {
			result = false;
			exp.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * 
	 * @return -1 on error else the team id.
	 */
	public int getUserTeamId() {
		int result = -1;
		String restPath = String.format("%s?method=get_user_team_id&input_type=json&response_type=json&rest_data=", this.baseURL);
		String strdata = "";
		
		strdata = String.format("{\"session\":\"%s\"}", this.sessionID);
		restPath += strdata;
		
		try {
			print("URL: "+restPath);
			URL url = new URL(restPath);
			HttpURLConnection http = (HttpURLConnection)url.openConnection();
			http.setRequestMethod("GET");
			http.setUseCaches(false);
			http.setDoInput(true);
			http.setDoOutput(true);
			InputStream in = http.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line = "";
			String tmp = "";

			while ((line = br.readLine()) != null) {
				tmp += line;
			}
			
			print("GetUserTeamID Response: "+ tmp);
			tmp = tmp.replaceAll("\"", "");
			
			if (tmp.contains("Invalid Login")) {
				result = -1;
				print("Error calling: get_user_team_id!");
				Gson json = new Gson();
				this.setLastError(json.fromJson(tmp, ErrorData.class));
			} else {
				result = Integer.valueOf(tmp);
			}
		} catch (Exception exp) {
			exp.printStackTrace();
			result = -1;
		}
		
		return result;
	}
	
	/**
	 * 
	 * @return String[] on success, or null on failure.
	 */
	public String[] getAvailableModules() {
		String[] result = null;
		String restPath = String.format("%s?method=get_available_modules&input_type=json&response_type=json&rest_data=", this.baseURL);
		String strdata = "";
		
		strdata = String.format("{\"session\":\"%s\"}", this.sessionID);
		restPath += strdata;
		
		try {
			print("URL: "+restPath);
			URL url = new URL(restPath);
			HttpURLConnection http = (HttpURLConnection)url.openConnection();
			http.setRequestMethod("GET");
			http.setUseCaches(false);
			http.setDoInput(true);
			http.setDoOutput(true);
			InputStream in = http.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line = "";
			String tmp = "";

			while ((line = br.readLine()) != null) {
				tmp += line;
			}
			
			print("GetAvailableModules Response: "+ tmp);
			
			if (tmp.contains("Invalid Login")) {
				print("Error calling GetAvailableModules!");
				result = null;
			} else {
				Gson json = new Gson();
				ModulesList data = json.fromJson(tmp, ModulesList.class);
				result = data.modules;
			}
			
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param moduleName
	 * @param query
	 * @param deleted
	 * @return -1 on error, else the entires count on success.
	 */
	public int getEntriesCount(String moduleName, String query, boolean deleted) {
		int result = -1;
		String restPath = String.format("%s?method=get_entries_count&input_type=json&response_type=json&rest_data=", this.baseURL);
		String strdata = "";
		int del = 0;
		
		if (deleted) {
			del = 1;
		} else {
			del = 0;
		}
		
		strdata = String.format("{\"session\":\"%s\",\"module_name\":\"%s\",\"query\":\"%s\",\"deleted\":\"%d\"}", 
				this.sessionID, moduleName, query, del);
		restPath += strdata;
		
		try {
			print("URL: "+restPath);
			URL url = new URL(restPath);
			HttpURLConnection http = (HttpURLConnection)url.openConnection();
			http.setRequestMethod("GET");
			http.setUseCaches(false);
			http.setDoInput(true);
			http.setDoOutput(true);
			InputStream in = http.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line = "";
			String tmp = "";

			while ((line = br.readLine()) != null) {
				tmp += line;
			}
			print("GetEntriesCount Response: "+ tmp);
			
			if (!tmp.contains("result_count")) {
				result = -1;
				Gson json = new Gson();
				this.setLastError(json.fromJson(tmp, ErrorData.class));
			} else {
				Gson json = new Gson();
				ResultCount data = json.fromJson(tmp, ResultCount.class);
				result = data.result_count;
			}
			
		} catch (Exception exp) {
			exp.printStackTrace();
			result = -1;
		}
	
		return result;
	}
	
	/*
	 * @param string $session			- Session ID returned by a previous call to login.
 * @param string $search_string 	- string to search
 * @param string[] $modules			- array of modules to query
 * @param int $offset				- a specified offset in the query
 * @param int $max_results			- max number of records to return
 * @return Array return_search_result 	- Array('Accounts' => array(array('name' => 'first_name', 'value' => 'John', 'name' => 'last_name', 'value' => 'Do')))
 * @exception 'SoapFault' -- The SOAP error, if any

    Method [  public method search_by_module ] {	
	*/
	
	public ModuleSearchResults searchByModule(String search, String[] modules, int offset, int maxResults) {
		String modstr = "";
		//HashMap<String, ArrayList<HashMap<String, String>>> result = new HashMap<String, ArrayList<HashMap<String,String>>>();
		ModuleSearchResults result = new ModuleSearchResults();
		
		for (int i = 0; i <= modules.length -1; i++) {
			modstr += String.format("\"%s\",", modules[i]);
		}
		modstr = modstr.substring(0, modstr.length() -2);
		modstr += "\"";
		String strdata = String.format("{\"session\":\"%s\",\"search_string\":\"%s\",\"modules\":[%s],\"offset\":\"%d\",\"max_results\":\"%d\"}", 
				this.sessionID, search, modstr, offset, maxResults);
		String restPath = String.format("%s?method=search_by_module&input_type=json&response_type=json&rest_data=%s",
				this.baseURL, strdata);
		
		try {
			print("URL: "+restPath);
			URL url = new URL(restPath);
			HttpURLConnection http = (HttpURLConnection)url.openConnection();
			http.setRequestMethod("GET");
			http.setUseCaches(false);
			http.setDoInput(true);
			http.setDoOutput(true);
			InputStream in = http.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line = "";
			String tmp = "";

			while ((line = br.readLine()) != null) {
				tmp += line;
			}
			print("SearchByModule Response: "+ tmp);
			
			if (!tmp.contains("entry_list")) {
				Gson json = new Gson();
				this.setLastError(json.fromJson(tmp, ErrorData.class));
				result = null;
			} else {
				JSONObject obj = new JSONObject(tmp);
				JSONArray list = obj.getJSONArray("entry_list");
				
				for (int i = 0; i <= list.length() -1; i++) {
					JSONObject modinfo = list.getJSONObject(i);
					String name = modinfo.getString("name");
					JSONArray records = modinfo.getJSONArray("records");
					System.out.printf("NAME: %s\n", name);
					ArrayList<HashMap<String, String>> recordList = new ArrayList<HashMap<String,String>>();
					
					for (int recIndex = 0; recIndex <= records.length() -1; recIndex++) {
						JSONObject currRec = records.getJSONObject(recIndex);
						Iterator<String> currIt = currRec.keys();
						HashMap<String, String> recordData = new HashMap<String, String>();
						
						while (currIt.hasNext()) {
							String key = currIt.next();
							JSONObject recInfo = currRec.getJSONObject(key);
							String v = recInfo.getString("value");
							System.out.printf("Key: %s => %s\n", key, v);
							recordData.put(key,v);
						}
						recordList.add(recordData);
						result.put(name, recordList);
					}
				}
			}
			
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		
		return result;
	}
	
	
	private String genPasswordMD5(String password) {
		String result = "";
		
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.reset();
			byte[] mdbytes = md5.digest(password.getBytes());

			for (int i = 0; i <= mdbytes.length-1; i++) {
				String t = Integer.toHexString(0xFF & mdbytes[i]);
				if (t.length() == 1) {
					t = String.format("0%s", t);
				}

				result += t;
			}
		} catch (Exception exp) {
			exp.printStackTrace();
			result = null;
		}
		
		print("Password MD5: " + result);
		return result;
	}
	
	private void print(String msg) {
		if (this.debug) {
			System.out.printf("[DEBUG]:%s\n", msg);
		}
	}
	
}
