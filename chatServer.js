var vertx = require('vertx');
var console = require('vertx/console');

var rm = new vertx.RouteMatcher();
rm.get('/',function(req){
	req.response.sendFile('index.html');
});

rm.get('/jquery.js',function(req){
	req.response.sendFile('jquery.js')
});

var webserver = vertx.createHttpServer();
var sockets = [];

webserver.websocketHandler(function(ws){
	//Speichere alle Chatclients in einem Array
	sockets.push(ws);
	ws.dataHandler(function(buffer){

		sockets.forEach(function (socket){
			socket.writeTextFrame(buffer.toString());
		});
	});
	ws.endHandler(function(buffer){
		//Entferne Chatclient falls er die Verbindung schließt
		sockets.splice(sockets.indexOf(ws),1);
	})
	
});

webserver.requestHandler(rm).listen(3000);
console.log("Vertx-Chat-Server laeuft auf port 3000");
