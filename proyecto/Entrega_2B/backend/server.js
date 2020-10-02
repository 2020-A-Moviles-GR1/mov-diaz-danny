
const SocketServer = require('websocket').server
const http = require('http')
const config = require('./config')
const express = require('express')
const app = express();
const cors = require('cors')
const log = require('./utils').log

const url_websocket =  'ws://'+config.ip+':'+config.port_socket+'/'
app.use(cors())



app.get("/demo", function(req, res){
	res.sendFile('C://wamp64/www/mov-gestures/backend/public_html/demo.html');
})

app.listen(config.port_express, function(){
    log("APP-EXPRESS", "La aplicación está escuchando en http://" + config.ip + ':' + config.port_express);
});


app.use(express.static("./public_html"));

const server = http.createServer((req, res) => {
		log("HTTP-SERVER", 'Received request for ' + req.url);
		res.writeHead(404);
		res.end();
})

server.listen(config.port_socket, ()=>{
    log("HTTP-SERVER", "Listening on port " + config.port_socket + "... Web socket listening in the same port")
})


const wsServer = new SocketServer({
	httpServer:server,
	autoAcceptConnections: false
	})

function originIsAllowed(origin) {
  // put logic here to detect whether the specified origin is allowed.
  return true;
}


const connections = []
const web_connections = []
const web_rooms_associated = []
const web_app_tunnel = {}  // las llaves son los rooms porque son únicos


const CONTEXT_WEB_SOCKET_SERVER = 'WEBSOCKET_SERVER'

wsServer.on('request', (req) => {
	log(CONTEXT_WEB_SOCKET_SERVER, 'Connection in accepted from ' + req.remoteAddress);

	if (!originIsAllowed(req.origin)) {
      req.reject();
      log(CONTEXT_WEB_SOCKET_SERVER, 'Connection from origin ' + req.origin + ' rejected.');
      return;
    }



    var connection = req.accept(config.protocol, req.origin);  // req.accept('echo-protocol', req.origin); req.accept();
    connections.push(connection)

    connection.on('message', (msg) => {
			var type = msg.type


			if(type == 'utf8'){

				var data = JSON.parse(msg.utf8Data)

				// se valida la sala
				if(data.id == 1){
					const split_data = data.data.split("#")
					const user = split_data[0]
					const data_room = parseInt(split_data[1])

					var myresponse = {}

					if(web_rooms_associated.includes(data_room)){
						// el room si existe
						for(var i=0; i< web_rooms_associated.length; i++){
							const room = web_rooms_associated[i]
							if (room == data_room){
								web_app_tunnel[data_room] = {
									index_web_connection: i,
									index_app_connection: connections.indexOf(connection)
								}
								break

							}
						}

						myresponse = {
							id_message: 1,
							server_message: "Room validada " + req.remoteAddress,
							error: "null",
							metadata: "1",  // la petición inicial enviada
							data: [
								data_room, 
								web_app_tunnel[data_room]
							]
						}

					}else{
						myresponse = {
							id_message: -1,
							server_message: "Room no existe " + req.remoteAddress,
							error: "Room no encontrada",
							metadata: "1",
							data: [
								data
							]
						}
					}

					sendResponseToActualConnection(connection, myresponse)

				}

				// mensaje desde la app que modifica la web
				// data format: "user=user_test#id_function=15#room=203 => user_test#15#203"
				else if(data.id == 2){
					const split_data = data.data.split("#")
					const user = split_data[0]
					const id_function = parseInt(split_data[1])
					const data_room = parseInt(split_data[2])

					const keys = Object.keys(web_app_tunnel)
					
					
					var connection_found = false;
					
					for(var i=0; i<keys.length; i++){
						const key = keys[i]
						// console.log(web_app_tunnel)
						// console.log(key)
						const index = web_app_tunnel[key]["index_app_connection"]

						if(connections[index] == connection){
							// tunnel found
							myresponse = {
								id_message: 102,
								server_message: ""+user,
								error: "null",
								metadata: ""+data_room,
								data: split_data
							}

							sendResponseToActualConnection(
								web_connections[web_app_tunnel[key]["index_web_connection"]],
								myresponse
							)
							
							connection_found = true;

							break;
						}
					}
					
					
					var id_msg = 1;
					var srv_msg = "Envio correcto a la WEB";
					if(!connection_found){
						id_msg = -1
						srv_msg = "No hay un puente entre la APP y la WEB";
					}
					
					const myresponse_to_app = {
								id_message: id_msg,
								server_message: srv_msg,
								error: "null",
								metadata: "2",
								data: split_data
							}
					
					sendResponseToActualConnection(
								connection,
								myresponse_to_app
							)
					
				}
				// solo una vez se recibe este mensaje al conectarse
				else if(data.id == 1001){
					

					const room = Math.round(Math.random() *1000)
					while(web_rooms_associated.includes(room)){
						room = Math.round(Math.random() * 9999)
					}

					web_connections.push(connection)
					web_rooms_associated.push(room)

					var myresponse = {
						id_message: 1001,
						server_message: "Room generada: " + req.remoteAddress,
						error: "null",
						metadata: ""+room,
						data: [
							data
						]
					}
					sendResponseToActualConnection(connection, myresponse)
				}





			}
			else{
				log(CONTEXT_WEB_SOCKET_SERVER, "MessageReceived, " + msg.utf8Data)
				sendResponseToActualConnection(connection, msg.utf8Data)
			}

    })

    connection.on('close', (resCode, des) => {
        log(CONTEXT_WEB_SOCKET_SERVER, 'connection closed ' + resCode + ' ' + des)
        connections.splice(connections.indexOf(connection), 1)

				if(web_connections.includes(connection)){
					// console.log(web_app_tunnel, web_rooms_associated)


					const web_index = web_connections.indexOf(connection)
					web_connections.splice(web_index, 1)
					const room_key = web_rooms_associated[web_index]

					if(web_app_tunnel[room_key] != undefined){
						delete web_app_tunnel[room_key]
					}
					web_rooms_associated.splice(web_index, 1)

					log(CONTEXT_WEB_SOCKET_SERVER, "Removida la conexión WEB y rooms asociadas")

					// console.log(web_app_tunnel, web_rooms_associated)
				}
    })

})



function sendResponseToActualConnection(connection, myresponse){
	connections.forEach(element => {
		if (element == connection){
			element.sendUTF(JSON.stringify(myresponse))
			log(CONTEXT_WEB_SOCKET_SERVER, "Sending=> " + JSON.stringify(myresponse))
		}
	})
}
