package pl.mjedynak;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import static spark.Spark.get;
import static spark.SparkBase.threadPool;

public class Server {

    private static final Logger LOG = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {
        int maxThreads = 40;
        threadPool(maxThreads);

        get("/hello", (req, res) ->
        {
            TimeUnit.SECONDS.sleep(1);
            LOG.info(req.queryParams("id"));
            return req.queryParams("id");
        });
    }
}
