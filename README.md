#Riot 2015 API Challenge entry
The app collects data of URF games and displays data relating to specific champions. It is developed with Angularjs with a Java backend for handling collection and access of data.

##Demo
Live demo can be found [here](https://apichallenge-greedy.rhcloud.com/)

##Deploy
To deploy the app, add your own API-key [here](src/main/java/datacollection/DataCollectTask.java) and uncomment the listener [here](src/main/webapp/WEB-INF/web.xml) to start a task collection data with a 5 minute interval (note: remote endpoints may no longer be valid after the challenge has ended).

Fill in empty database details [here](src/main/java/util/UrfDao.java) as appropriate to your own data source, and use the sql dump to fill the database with the data used in the demo.



