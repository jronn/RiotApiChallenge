package datacollection;

import java.io.IOException;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import util.ChampionData;
import util.RiotApi;
import util.RiotApi.REGION;
import util.UrfDao;

public class DataCollectTask implements Runnable {
	
	private RiotApi riotApi;
	private UrfDao dao;
	private final String API_KEY = " ";
	
	public void run() {
		
		try {
			riotApi = new RiotApi(API_KEY);
			dao = new UrfDao();			
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		for(REGION r : REGION.values()) {
			try {
				collectData(r);
				try {
				    Thread.sleep(20000); // Sleep for 20 sec due to api rate limits
				} catch(InterruptedException ex) {
				    Thread.currentThread().interrupt();
				}
			} catch(Exception e) {
				System.out.println("Could not retrieve data for region: " + r + ", " + e.getMessage());
				try {
					dao.rollback();
				} catch (SQLException e1) {
					System.out.println("Failed to do rollback, " + e.getMessage());
				}
			}
		}
	}
	
	/*
	 * Collects data from specified region and stores it in db
	 */
	private void collectData(REGION region) throws JSONException, IOException, SQLException {
		
		String[] matchIds = riotApi.fetchMatchIds(region);
		
		for(String matchId : matchIds) {
			
			JSONObject matchData = new JSONObject(riotApi.fetchMatch(matchId, region));				
			JSONArray participants = matchData.getJSONArray("participants");
			
			int matchDuration = (matchData.getInt("matchDuration") / 100) * 60 + matchData.getInt("matchDuration") % 100;
			double totalDamage = 0;
			double totalHealing = 0;
			
			// get team wide total dmg/healing
			for(int i = 0; i < participants.length(); i++) {
				totalDamage += participants.getJSONObject(i).getJSONObject("stats").getLong("totalDamageDealtToChampions");
				totalHealing += participants.getJSONObject(i).getJSONObject("stats").getLong("totalHeal");
			}
			
			// for each player
			for(int i = 0; i < participants.length(); i++) {
				JSONObject participant = participants.getJSONObject(i);
				JSONObject stats = participant.getJSONObject("stats");
				
				String champId = participant.getString("championId");
				ChampionData champData = dao.getChampionData(champId);
				
				if(stats.getBoolean("winner"))
					champData.wins++;
				else
					champData.losses++;
				
				champData.avgmatchduration = ((champData.avgmatchduration * champData.gamesplayed) + matchDuration) / (champData.gamesplayed+1);
				champData.pentakills += stats.getInt("pentaKills");					
				champData.percentagedamage = ((champData.percentagedamage * champData.gamesplayed) + 100*(stats.getLong("totalDamageDealtToChampions")/totalDamage)) / (champData.gamesplayed+1);
				champData.percentagehealing = ((champData.percentagehealing * champData.gamesplayed) + 100*(stats.getLong("totalHeal")/totalHealing)) / (champData.gamesplayed+1);
				champData.avgkills = ((champData.avgkills * champData.gamesplayed) + stats.getInt("kills")) / (champData.gamesplayed+1);
				champData.avgdeaths = ((champData.avgdeaths * champData.gamesplayed) + stats.getInt("deaths")) / (champData.gamesplayed+1);
				champData.avgassists = ((champData.avgassists * champData.gamesplayed) + stats.getInt("assists")) / (champData.gamesplayed+1);
				champData.gamesplayed++;
				
				dao.updateChampionData(champData);
			}
			
			// for each team
			JSONArray teams = matchData.getJSONArray("teams");
			for(int i = 0; i < teams.length(); i++) {
				JSONArray bans = teams.getJSONObject(i).getJSONArray("bans");
				
				// for each ban
				for(int j = 0; j < bans.length(); j++) {
					bans.getJSONObject(j).getString("championId");
					ChampionData champData = dao.getChampionData(bans.getJSONObject(j).getString("championId"));
					champData.gamesbanned++;
					dao.updateChampionData(champData);
				}
			}
			
			dao.incrementGamesAnalyzed();
			dao.commit();
		}
	}
	
}
