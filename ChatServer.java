import java.util.ArrayList;
import java.util.List;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.http.ServerWebSocket;
import org.vertx.java.platform.Verticle;

public class ChatServer extends Verticle {
	public void start() {
		RouteMatcher rm = new RouteMatcher();

		rm.get("/", new Handler<HttpServerRequest>() {
			public void handle(HttpServerRequest req) {
				req.response().sendFile("index.html");
			}
		});

		rm.get("/jquery.js", new Handler<HttpServerRequest>() {
			public void handle(HttpServerRequest req) {
				req.response().sendFile("jquery.js");
			}
		});

		HttpServer webServer = vertx.createHttpServer();
		final List<ServerWebSocket> sockets = new ArrayList<ServerWebSocket>();
		webServer.websocketHandler(new Handler<ServerWebSocket>() {
			public void handle(final ServerWebSocket ws) {
				//Speichere alle Chatclients in einer Liste
				sockets.add(ws);
				ws.dataHandler(new Handler<Buffer>() {
					public void handle(Buffer data) {
						for (ServerWebSocket socket : sockets) {
							socket.writeTextFrame(data.toString()); // back
						}
					}
				});

				ws.endHandler(new Handler<Void>() {
					public void handle(Void arg0) {
						//Entferne Chatclient falls er die Verbindung schließt
						sockets.remove(ws);
					}
				});

			}
		});
		
		webServer.requestHandler(rm).listen(3000);
		System.out.print("Vertx-Chat-Server laeuft auf port 3000");
	}
}
