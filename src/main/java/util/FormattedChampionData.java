package util;

/*
 * Formatted data ready to be printed by a view
 */
public class FormattedChampionData {
	
	public String id;
	public String name;
	public double winrate;
	public String avgmatchduration; 
	public String pentakills;
	public double percentagedamage;
	public double percentagehealing;
	public double avgkills;
	public double avgdeaths;
	public double avgassists;
	public double kda;
	public double pickrate;
	public double banrate;
	public int totalgames;
	
	public FormattedChampionData(String id) {
		this.id = id;
		
		name = "";
		avgmatchduration = "";
		pentakills = "";
		percentagedamage = 0;
		percentagehealing = 0;
		avgkills = 0;
		avgdeaths = 0;
		avgassists = 0;
		pickrate = 0;
		banrate = 0;
		totalgames = 0;
		kda = 0;
	}
}
