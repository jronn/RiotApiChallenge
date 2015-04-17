package datacollection;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class DataCollectTaskManager implements ServletContextListener {
	
	private ScheduledExecutorService scheduler;
	
	public void contextInitialized(ServletContextEvent event) {		
	    scheduler = Executors.newSingleThreadScheduledExecutor();
	    scheduler.scheduleAtFixedRate(new DataCollectTask(), 5, 5, TimeUnit.MINUTES);
	}
	
    public void contextDestroyed(ServletContextEvent event) {
        scheduler.shutdownNow();
    }
}
