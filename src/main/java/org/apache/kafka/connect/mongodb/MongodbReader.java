package org.apache.kafka.connect.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.CursorType;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import org.apache.kafka.connect.errors.ConnectException;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.BSONTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Andrea Patelli
 */
public class MongodbReader {
    private static final Logger log = LoggerFactory.getLogger(MongodbReader.class);

    protected ConcurrentLinkedQueue<Document> messages;

    private List<String> dbs;
    private String host;
    private Integer port;
    private Map<Map<String, String>, Map<String, Object>> start;

    public MongodbReader(String host, Integer port, List<String> dbs, Map<Map<String, String>, Map<String, Object>> start) {
        this.host = host;
        this.port = port;
        this.dbs = new ArrayList<>(0);
        this.dbs.addAll(dbs);
        this.start = start;
    }

    public void run() {
        for(String db: dbs) {
            String start = (String)this.start.get(Collections.singletonMap("mongodb", db)).get(db);
            DatabaseReader reader = new DatabaseReader(host, port, db, start, messages);
            new Thread(reader).start();
        }
    }
}