package pl.mjedynak;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.utils.IOUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {

    private static final Logger LOG = LoggerFactory.getLogger(Server.class);

    private static final int USER_THREADS = 30;
    private static final int HTTP_CLIENT_THREADS = 30;
    private static final int HTTP_CLIENT_THREADS_PER_ROUTE = 10;


    public static void main(String[] args) throws URISyntaxException, IOException {
        DefaultHttpClient defaultHttpClient = getDefaultHttpClient();

        ExecutorService executorService = Executors.newFixedThreadPool(USER_THREADS);
        for (int i = 0; i < USER_THREADS; i++) {
            executorService.submit(() -> {
                try {
                    URI uri = new URI("http://localhost:4567/hello?id=" + Thread.currentThread().getName());
                    HttpUriRequest get = new HttpGet(uri);
                    CloseableHttpResponse httpResponse = defaultHttpClient.execute(get);
                    LOG.info(IOUtils.toString(httpResponse.getEntity().getContent()));
                } catch (Exception e) {
                    LOG.error(e.toString());
                }
            });
        }
        executorService.shutdown();
    }

    private static DefaultHttpClient getDefaultHttpClient() {
        PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager();
        connectionManager.setMaxTotal(HTTP_CLIENT_THREADS);
        connectionManager.setDefaultMaxPerRoute(HTTP_CLIENT_THREADS_PER_ROUTE);
        HttpParams params = new BasicHttpParams();
        return new DefaultHttpClient(connectionManager, params);
    }
}
