package fr.phyxit.id.restapi.http;

import com.sun.net.httpserver.HttpServer;
import fr.phyxit.id.SensorManager;
import fr.phyxit.id.restapi.servlets.SensorSetHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class HTTPRestServer {

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");


	private SensorManager sensorManager;
	private HttpServer server;

	private String address;
	private int port;

	public HTTPRestServer(SensorManager sensorManager, String address, int port) {
		this.sensorManager = sensorManager;
		this.address = address;
		this.port = port;
	}

	public boolean start() {
		try {
			ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
			server = HttpServer.create(new InetSocketAddress(address, port), 0);

			server.createContext("/sets", new SensorSetHandler(sensorManager));

			server.setExecutor(threadPoolExecutor);
			server.start();

			System.out.printf("[%s] HTTP RestAPI Server - Started at %s\n",
					LocalTime.now().format(DATE_TIME_FORMATTER),
					server.getAddress().toString());
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

		System.out.println("[%s] HTTP RestAPI Server - Stopped");
	}

}