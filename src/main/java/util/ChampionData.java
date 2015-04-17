package util;

/*
 * Data class used in storing and retrieving data from db
 */
public class ChampionData {
	
	public String id;
	public String name;
	public int wins;
	public int losses;
	public double avgmatchduration; //specified in seconds
	public int pentakills;
	public double percentagedamage;
	public double percentagehealing;
	public double avgkills;
	public double avgdeaths;
	public double avgassists;
	public int gamesplayed;
	public int gamesbanned;
	public int totalgames;
	
	public ChampionData(String id) {
		this.id = id;
		
		name = "";
		wins = 0;
		losses = 0;
		avgmatchduration = 0;
		pentakills = 0;
		percentagedamage = 0;
		percentagehealing = 0;
		avgkills = 0;
		avgdeaths = 0;
		avgassists = 0;
		gamesplayed = 0;
		gamesbanned = 0;
		totalgames = 0;
	}
}
