package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

public class RiotApi {
	private String key;
	
	public enum REGION { br, eune, euw, kr, lan, las, na, oce, ru, tr }
	
	
	public RiotApi(String key) {
		this.key = key;
	}
	
	
	/*
	 * Gets a list of match-ids from the URF game mode
	 */
	public String[] fetchMatchIds(REGION region) throws IOException {
		long time = (60 * 5 * (int)(System.currentTimeMillis() / (1000 * 60 * 5))) - 10 * 60;
		String url = "https://" + region + ".api.pvp.net/api/lol/" + region + "/v4.1/game/ids?beginDate=" + time + "&api_key=" + key;
		 
		String result = queryApi(url);
		String resultString = result.toString().replaceAll("\\s+", "").replaceAll("\\[", "").replaceAll("\\]", "");	

		return resultString.split(",");
	}
	
	
	/*
	 * Gets data of a match with specified match id 
	 */
	public String fetchMatch(String matchId, REGION region) throws IOException {
		String url = "https://" + region + ".api.pvp.net/api/lol/" + region + "/v2.2/match/" + matchId + "?api_key=" + key;
			 
		return queryApi(url);
	}
	
	
	private String queryApi(String url) throws IOException {
		
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url);

		StringBuffer result = new StringBuffer();

		HttpResponse response = client.execute(request);
				 
		if(response.getStatusLine().getStatusCode() != 200)
			throw new IOException("Could not retrieve data, error code: " + response.getStatusLine().getStatusCode());
		 
		BufferedReader rd = new BufferedReader(
		               new InputStreamReader(response.getEntity().getContent()));
		 
		result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}

		return result.toString();		
	}
}
