package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import util.UrfDao;

import com.google.gson.Gson;

/*
 * Returns data collected regarding champions in a json format based on given id. No id returns all champion data
 */
public class ChampionDataServlet extends HttpServlet{
	
	public void init() {
		CacheManager.getInstance().addCache("champdataCache");
		
		Cache cache = CacheManager.getInstance().getCache("champdataCache");
		CacheConfiguration config = cache.getCacheConfiguration();
		// 2 months ttl
		config.setTimeToLiveSeconds(5184000);
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("application/json");
		
		String champId = request.getParameter("id");

		try {
			PrintWriter out = response.getWriter();
			
			Gson gson = new Gson();
			UrfDao dao = new UrfDao();
			
			if(champId == null) {
				Element elt = CacheManager.getInstance().getCache("champdataCache").get("all");
				if(elt != null) {
					out.println(elt.getObjectValue());	
					return;
				}
				
				String data = gson.toJson(dao.getFormattedChampionData());
				CacheManager.getInstance().getCache("champdataCache").put(new Element("all", data));
				out.print(data);
				return;
			}
			
			// Check cache
			Element elt = CacheManager.getInstance().getCache("champdataCache").get(champId);
			if(elt != null) {
				out.println(elt.getObjectValue());	
				return;
			}					
			
			String data = gson.toJson(dao.getFormattedChampionData(champId));
			CacheManager.getInstance().getCache("champdataCache").put(new Element(champId, data));
			out.println(data);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
}
