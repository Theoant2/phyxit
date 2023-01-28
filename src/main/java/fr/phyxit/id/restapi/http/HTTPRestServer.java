package fr.phyxit.id.restapi.http;

import com.sun.net.httpserver.HttpServer;
import fr.phyxit.id.SensorManager;
import fr.phyxit.id.restapi.servlets.SensorSetHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class HTTPRestServer {

	public static final int PORT = 27078;

	private SensorManager sensorManager;
	private HttpServer server;

	public HTTPRestServer(SensorManager sensorManager) {
		this.sensorManager = sensorManager;
	}

	public boolean start() {
		try {
			ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
			server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);

			server.createContext("/sets", new SensorSetHandler(sensorManager));

			server.setExecutor(threadPoolExecutor);
			server.start();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void stop() {
		if (server == null) return;
		server.stop(0);
		server = null;
	}

}