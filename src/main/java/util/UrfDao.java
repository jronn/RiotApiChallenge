package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UrfDao {

	private final String DB_USERNAME = " ";
	private final String DB_PASSWORD = " ";
	private final String DB_HOST = " ";
	private final String DB_PORT = " ";
	private final String DB_NAME = " ";
	private final String DB_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME;
	
	private Connection con;
	private Statement stmt;
	
	public UrfDao() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		con = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
		con.setAutoCommit(false);
		stmt = con.createStatement();
	}
	
	public void commit() throws SQLException {
		con.commit();
	}
	
	public void rollback() throws SQLException {
		con.rollback();
	}

	public int getTotalGames() throws SQLException {	
		ResultSet rs = stmt.executeQuery("SELECT * FROM general");
		rs.first();
		return rs.getInt("noofgames");
	}
	
	/*
	 * Returns data of all champions in a formatted way ready to be displayed
	 */
	public List<FormattedChampionData> getFormattedChampionData() throws SQLException {
		
		ResultSet rs = stmt.executeQuery("SELECT * FROM champion c,general");
		List<FormattedChampionData> champDataList = new ArrayList();
		
		while(rs.next()) {
			FormattedChampionData champData = new FormattedChampionData(rs.getString("id"));
			
			champData.name = rs.getString("name");
			champData.winrate = Math.round((100*(double)rs.getInt("wins") / (rs.getInt("wins") + rs.getInt("losses"))));
			
			int seconds = (int) (Math.round(rs.getDouble("avgmatchduration"))%60);
			
			if(seconds < 10)
				champData.avgmatchduration = Math.round(rs.getDouble("avgmatchduration")/60 - 0.5) + ":0" + seconds;
			else
				champData.avgmatchduration = Math.round(rs.getDouble("avgmatchduration")/60 - 0.5) + ":" + seconds;
			
			if(rs.getInt("pentakills") != 0 && (rs.getInt("noOfGames")/rs.getInt("pentakills")) < 10000)
				champData.pentakills = Integer.toString(rs.getInt("noOfGames")/rs.getInt("pentakills"));
			else
				champData.pentakills = "10.000+";
			champData.percentagedamage = (double)Math.round(rs.getDouble("percentagedamage") * 100)/100;
			champData.percentagehealing = (double)Math.round(rs.getDouble("percentagehealing") * 100)/100;
			champData.avgkills = (double)Math.round(rs.getDouble("avgkills")*100)/100;
			champData.avgdeaths = (double)Math.round(rs.getDouble("avgdeaths")*100)/100;
			champData.avgassists = (double)Math.round(rs.getDouble("avgassists")*100)/100;
			champData.pickrate = (double)Math.round((100 * (double)rs.getInt("gamesplayed") / rs.getInt("noOfGames")) * 100) / 100;
			champData.banrate =(double) Math.round((100 * (double)rs.getInt("gamesbanned") / rs.getInt("noOfGames")) * 100) / 100;
			champData.totalgames = rs.getInt("noOfGames");
			champData.kda = (double)Math.round(100*(champData.avgkills + champData.avgassists) / champData.avgdeaths)/100;
			
			champDataList.add(champData);
		}
		
		return champDataList;
	}
	
	
	/*
	 * Returns data of a single champion in a format ready to be displayed based on given champ id
	 */	
	public FormattedChampionData getFormattedChampionData(String id) throws SQLException {
		
		ResultSet rs = stmt.executeQuery("SELECT * FROM champion c,general WHERE c.id='" + id + "'");
		FormattedChampionData champData = new FormattedChampionData(id);
		
		// If not already in db
		if (!rs.next()){
			stmt.executeUpdate("INSERT INTO champion (id) VALUES "
					+ "(" + id + ")");
			
			return new FormattedChampionData(id);
		}
		
		champData.name = rs.getString("name");
		champData.winrate = (int) Math.round((100*(double)rs.getInt("wins") / (rs.getInt("wins") + rs.getInt("losses"))));
		
		int seconds = (int) (Math.round(rs.getDouble("avgmatchduration"))%60);
		
		if(seconds < 10)
			champData.avgmatchduration = Math.round(rs.getDouble("avgmatchduration")/60 - 0.5) + ":0" + seconds;
		else
			champData.avgmatchduration = Math.round(rs.getDouble("avgmatchduration")/60 - 0.5) + ":" + seconds;
		
		if(rs.getInt("pentakills") != 0 && (rs.getInt("noOfGames")/rs.getInt("pentakills")) < 10000)
			champData.pentakills = Integer.toString(rs.getInt("noOfGames")/rs.getInt("pentakills"));
		else
			champData.pentakills = "10.000+";
		champData.percentagedamage = Math.round(rs.getDouble("percentagedamage") * 100)/100;
		champData.percentagehealing = Math.round(rs.getDouble("percentagehealing") * 100)/100;
		champData.avgkills = (double)Math.round(rs.getDouble("avgkills")*100)/100;
		champData.avgdeaths = (double)Math.round(rs.getDouble("avgdeaths")*100)/100;
		champData.avgassists = (double)Math.round(rs.getDouble("avgassists")*100)/100;
		champData.pickrate = Math.round((100 * rs.getInt("gamesplayed") / rs.getInt("noOfGames")) * 100) / 100;
		champData.banrate = Math.round((100 * rs.getInt("gamesbanned") / rs.getInt("noOfGames")) * 100) / 100;
		champData.totalgames = rs.getInt("noOfGames");
		champData.kda = (double)Math.round(100*(champData.avgkills + champData.avgassists) / champData.avgdeaths)/100;	
		
		return champData;
	}
	
	
	public ChampionData getChampionData(String id) throws SQLException {
		
		ResultSet rs = stmt.executeQuery("SELECT * FROM champion c,general WHERE c.id='" + id + "'");
		
		// If not already in db
		if (!rs.next()){
			stmt.executeUpdate("INSERT INTO champion VALUES "
					+ "(" + id + ",0,0,0,0,0,0,0,0,0,0,0)");
			
			return new ChampionData(id);
		}
		
		ChampionData champData = new ChampionData(id);
		champData.name = rs.getString("name");
		champData.wins = rs.getInt("wins");
		champData.losses = rs.getInt("losses");
		champData.avgmatchduration = rs.getDouble("avgmatchduration");
		champData.pentakills = rs.getInt("pentakills");
		champData.percentagedamage = rs.getDouble("percentagedamage");
		champData.percentagehealing = rs.getDouble("percentagehealing");
		champData.avgkills = rs.getDouble("avgkills");
		champData.avgdeaths = rs.getDouble("avgdeaths");
		champData.avgassists = rs.getDouble("avgassists");
		champData.gamesplayed = rs.getInt("gamesplayed");
		champData.gamesbanned = rs.getInt("gamesbanned");
		champData.totalgames = rs.getInt("noOfGames");
		
		return champData;
	}
	
	public void updateChampionData(ChampionData champData) throws SQLException {
		
		stmt.executeUpdate("UPDATE champion SET"
				+ " name=" + champData.name
				+ " wins=" + champData.wins
				+ ", losses=" + champData.losses
				+ ", avgmatchduration=" + champData.avgmatchduration
				+ ", pentakills=" + champData.pentakills
				+ ", percentagedamage=" + champData.percentagedamage
				+ ", percentagehealing=" + champData.percentagehealing
				+ ", avgkills=" + champData.avgkills
				+ ", avgdeaths=" + champData.avgdeaths
				+ ", avgassists=" + champData.avgassists
				+ ", gamesplayed=" + champData.gamesplayed
				+ ", gamesbanned=" + champData.gamesbanned
				+ " WHERE id='" + champData.id + "'");
	}
	
	public void incrementGamesAnalyzed() throws SQLException {
		int old = getTotalGames();
		stmt.executeUpdate("UPDATE general SET noofgames=" + (old+1));
	}
	
}
