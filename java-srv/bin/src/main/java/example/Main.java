package example;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.InfluxDBIOException;
import org.influxdb.dto.*;
import org.influxdb.impl.InfluxDBResultMapper;

import java.util.*;
import java.util.concurrent.TimeUnit;

import example.model.Dog;

public class Main {
	private static InfluxDB connectDatabase() {

        // Connect to database assumed on localhost with default credentials.
        return  InfluxDBFactory.connect("http://db:8086", "admin", "admin");

    }

    private static boolean pingServer(InfluxDB influxDB) {
        try {
            // Ping and check for version string
            Pong response = influxDB.ping();
            if (response.getVersion().equalsIgnoreCase("unknown")) {
                System.out.println("Error pinging server.");
                return false;
            } else {
                System.out.println("Database version: " + response.getVersion());
                return true;
            }
        } catch (InfluxDBIOException idbo) {
            System.out.println("Exception while pinging database: " + idbo);
            return false;
        }
    }

    private static List<Dog> getPoints(InfluxDB connection, String query, String databaseName) {

        // Run the query
        Query queryObject = new Query(query, databaseName);
        QueryResult queryResult = connection.query(queryObject);

        // Map it
        InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
        return resultMapper.toPOJO(queryResult, Dog.class);
    }

    private static void createDatabase(InfluxDB db, String databaseName){
        db.createDatabase(databaseName);
        
        // Need a retention policy before we can proceed
        db.createRetentionPolicy("defaultPolicy", databaseName, "30d", 1, true);

        // Since we are doing a batch thread, we need to set this as a default
        db.setRetentionPolicy("defaultPolicy");
    }

    private static void addData(InfluxDB db, String databaseName, List<Dog> dogs){
        // Enable batch mode
        db.enableBatch(10, 10, TimeUnit.MILLISECONDS);

        for(Dog dog : dogs){
            Point point = Point.measurement(databaseName)
                    .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                    .addField("id", dog.getId())
                    .addField("breed", dog.getBreed())
                    .addField("color", dog.getColor())
                    .build();

            // db.write(databaseName, "defaultPolicy", point);
            db.write(0, point);
            try {
                Thread.sleep(2);
            } catch(InterruptedException e){}
        }
        try {
            Thread.sleep(10);
        } catch(InterruptedException e){}
        // db.disableBatch();
    }

	public static void main(String[] args) {
		InfluxDB db = connectDatabase();

		if(pingServer(db)) {
            final String databaseName = "animal";
			final String statement = "select * from " + databaseName + ".dog";
			System.out.println(statement);
            createDatabase(db, databaseName);
            List<Dog> list = new ArrayList<Dog>() {{
                add(new Dog(1L, "Am Bulldog", "White"));
                add(new Dog(2L, "Foxhound", "Red"));
                add(new Dog(3L, "Gr Shepard", "Black"));
            }};
            addData(db, databaseName, list);
            List<Dog> dogs = getPoints(db, statement, databaseName);
            //dogs.forEach(x -> System.out.print(x.toString()+"\n"));
            Iterator<Dog> iterator = dogs.iterator();
 
            while (iterator.hasNext()) {
                Dog dog = iterator.next();
                System.out.println(dog.toString());
            }
            db.close();
		}
	}
}
