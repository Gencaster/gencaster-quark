GenCasterMessage {
	*addressRemoteAction {
		^"/remote/action";
	}

	*addressBeacon {
		^"/beacon";
	}

	*addressAcknowledge {
		^"/acknowledge";
	}

	*response {|uuid, status, returnValue|
		^GenCasterMessage.eventToList((
			\uuid: uuid,
			\status: status,
			\returnValue: returnValue,
		));
	}

	*remoteAction {|action, password, cmd, target=nil|
		^GenCasterMessage.eventToList((
			\protocol_version: "0.1",
			\action: action,
			\password: password,
			\cmd: cmd,
			\target: target,
		));
	}

	*beacon {|
		name,
		synthPort,
		langPort,
		janusOutPort,
		janusInPort,
		janusOutRoom,
		janusInRoom,
		janusPublicIp,
		useInput,
		oscBackendHost,
		oscBackendPort
	|
		^GenCasterMessage.eventToList((
			\name: name,
			\synth_port: synthPort,
			\lang_port: langPort,
			\janus_out_port: janusOutPort,
			\janus_in_port: janusInPort,
			\janus_out_room: janusOutRoom,
			\janus_in_room: janusInRoom,
			\janus_public_ip: janusPublicIp,
			\use_input: useInput,
			\osc_backend_host: oscBackendHost,
			\osc_backend_port: oscBackendPort,
		));
	}

	*eventToList {|event|
		var list = [];
		event.pairsDo({|k, v|
			list = list ++ [k];
			list = list ++ [v];
		});
		^list;
	}
}

GenCasterClient {
	var <name;
	var <netClient;
	var <password;

	*new {|name, netClient, password|
		^super.newCopyArgs(name, netClient, password).init;
	}

	init {}

	send {|cmd, action=\code|
		netClient.sendMsg(GenCasterMessage.addressRemoteAction, *GenCasterMessage.remoteAction(
			action: action,
			password: password,
			cmd: cmd,
			target: name
		));
	}

	speak{|text|
		this.send(cmd: text, action: \speak);
	}
}

GenCaster {
	classvar activeClients;

	var <hostname;
	var <port;
	var password;

	// own variables
	var <clients;
	var <netClient;
	var <servers;

	*initClass {
		activeClients = ();
	}


	*new {|hostname="195.201.163.94", port=7000, password="demo"|
		^super.newCopyArgs(hostname, port, password).init;
	}

	init {
		netClient = NetAddr(hostname, port);
		clients = ();
		(0..15).do({|i|
			clients[i] = GenCasterClient(i, netClient, password);
		});
	}

	sendAll {|code, action=\code|
		"send to all: '%'".format(code).postln;
		netClient.sendMsg(GenCasterMessage.addressRemoteAction, *GenCasterMessage.remoteAction(
			action: action,
			password: password,
			cmd: code,
		));
	}

	speakAll {|text|
		this.sendAll(code: text, action: \speak);
	}

	at {|k|
		^clients[k];
	}

	activate {|...ks|
		var targets = ks.collect({|k| this.at(k)});
		var interp;
		var fun = {|code|
			"send to '%': '%'".format(targets, code).postln;
			targets.do({|target|
				target.send(code);
			});
		};

		this.clear;
		interp = thisProcess.interpreter;
		interp.codeDump = interp.codeDump.addFunc(fun);
	}

	broadcast {
		var interp, fun;
		this.clear;
		interp = thisProcess.interpreter;

		fun = {|code|
			var msg;
			"send to all: '%'".format(code).postln;
			netClient.sendMsg(GenCasterMessage.addressRemoteAction, *GenCasterMessage.remoteAction(
				action: \code,
				password: password,
				cmd: code,
			));
		};
		interp.codeDump = interp.codeDump.addFunc(fun);
	}

	clear {
		var interp = thisProcess.interpreter;
		interp.codeDump = nil;
	}
}
