/*
 * Copyright (C) J.P. Morrison, Enterprises, Ltd. 2009, 2020, All Rights Reserved. 
 */
package com.jpaulmorrison.fbp.components;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * General component to receive sequence of data chunks from a web socket and convert them
 *  into a series of substreams suitable for processing by custom components  
 * 
 * Generated output is a series of substreams, each consisting of
 *  - open bracket
 *  - packet containing socket reference - Java Class WebSocket
 *  - packet containing data string reference
 *  - close bracket
 *  
 *  The port number is passed in via an IIP associated with input port PORT
 *  
 *  This component is long-running; at startup, it starts a WebSocketServer thread
 *  
 *  Every 1/2 second the WebSocketServer checks to see if a 'kill' message has been received 
 *   by WebSocketreceive - if it has, the WebSocketServer is closed down. (This may need tweaking!)
 *   
 *  'Kill' is signalled by the client sending the character string '@kill' 
 *  
 *  Logging now handled by slf4j
 *  
 *  See https://github.com/TooTallNate/Java-WebSocket
 */

import java.net.InetSocketAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.KeyStore;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.WebSocketServerFactory;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.*;
import org.java_websocket.server.DefaultSSLWebSocketServerFactory;
import org.java_websocket.server.DefaultWebSocketServerFactory;
import org.java_websocket.server.WebSocketServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jpaulmorrison.fbp.core.engine.*;

@InPorts({ @InPort("PORT"), @InPort(value = "OPT", optional = true) })
@OutPort("OUT")
public class WebSocketReceive extends Component /* WebSocketServer */ {

	private InputPort portPort;
	private InputPort optPort;
	private OutputPort outPort;
	AtomicBoolean killsw;
	// LinkedList<Packet<?>> ll = null;
	HashMap<WebSocket, LinkedList<Packet<?>>> hm = null;

	Component comp = null;  //  JavaFBP Component
	MyWebSocketServer test = null;
	WebSocketServerFactory wsf = null;
	//WebSocketServerFactory wsf2 = null;

	//@Override
	protected void execute() throws Exception {
		boolean wssOpt = false;
		
		comp = this;

		killsw = new AtomicBoolean();

		hm = new HashMap<WebSocket, LinkedList<Packet<?>>>();

		Packet<?> p = portPort.receive();
		Integer i = (Integer) p.getContent();
		int port = i.intValue();
		drop(p);
		portPort.close();

		p = optPort.receive();
		if (p != null) {
			if (p.getContent().equals("TLS"))
				wssOpt = true;
			drop(p);
		}
		optPort.close();

		InetSocketAddress isa = new InetSocketAddress("localhost", port);
		test = new MyWebSocketServer(isa, new Draft_6455());
		
		System.out.println("WebSocketServer starting");
		putGlobal("WebSocketServer", test);
		
        		
		try {
			if (wssOpt) {
				// load up the key store
				String STORETYPE = "JKS";
				// String KEYSTORE = Paths.get("src", "main", "resources", "keystore.jks")
				// .toString();
				String KEYSTORE = "c:\\Users\\" + System.getProperty("user.name")
						+ "\\Appdata\\Local\\JavaFBP-WebSockets\\security\\keystore.jks";

				// System.out.println(KEYSTORE);
				String STOREPASSWORD = "storepassword";
				String KEYPASSWORD = "keypassword";

				KeyStore ks = KeyStore.getInstance(STORETYPE);
				File kf = new File(KEYSTORE);
				ks.load(new FileInputStream(kf), STOREPASSWORD.toCharArray());

				KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
				// KeyManagerFactory kmf =
				// KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
				kmf.init(ks, KEYPASSWORD.toCharArray());
				// TrustManagerFactory tmf =
				// TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
				TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");

				tmf.init(ks);

				SSLContext sslContext = null;
				sslContext = SSLContext.getInstance("TLS");
				// sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
				sslContext.init(kmf.getKeyManagers(), null, null);

				// SSLParameters sslParameters = new SSLParameters(); // This is all we need
				// sslParameters.setNeedClientAuth(true); ??????????

				// SSLEngine engine = sslContext.createSSLEngine();
				// List<String> ciphers = new
				// ArrayList<String>(Arrays.asList(engine.getEnabledCipherSuites()));
				// ciphers.remove("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256");
				// List<String> protocols = new
				// ArrayList<String>(Arrays.asList(engine.getEnabledProtocols()));
				// protocols.remove("SSLv3");
				wsf = new DefaultSSLWebSocketServerFactory(sslContext);
				//wsf2 = new DefaultWebSocketServerFactory();

				test.setWebSocketFactory(wsf);
			}

			test.setConnectionLostTimeout(0);
			// test.run();
			test.start();
			while (true) {

				try {
					sleep(500); // sleep for 1/2 sec
				} catch (InterruptedException e) {
					e.printStackTrace();
					outPort.close();
					test.stop();
					// handle the exception...
					return;
				}
				// Boolean killsw = (Boolean) getGlobal("killsw");
				if (killsw.get()) {
					// see also
					// http://stackoverflow.com/questions/4812686/closing-websocket-correctly-html5-javascript
					outPort.close();
					// System.out.println("Closed 'outPort'");
					test.stop();
					return;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		//test.setConnectionLostTimeout(0);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jpaulmorrison.fbp.core.engine.Component#openPorts()
	 */
	@Override
	protected void openPorts() {

		portPort = openInput("PORT");
		optPort = openInput("OPT");
		outPort = openOutput("OUT");

	}

	class MyWebSocketServer  extends WebSocketServer {

		// private static int counter = 0;

		// Component comp = null;

		final Logger log = LoggerFactory.getLogger(WebSocketReceive.class);
	  
		// public MyWebSocketServer(final int port, final Draft d) throws
		// UnknownHostException {
		// super(new InetSocketAddress(port), Collections.singletonList(d));
		// }

		//public MyWebSocketServer(final int port) throws UnknownHostException {
		// super(new InetSocketAddress(port));
		// }

		public MyWebSocketServer(final InetSocketAddress address, final Draft d) {
			super(address, Collections.singletonList(d));
		}

		public MyWebSocketServer(final InetSocketAddress address) {
			super(address);
		}

		
		@Override
		public void onWebsocketHandshakeReceivedAsClient(WebSocket conn, ClientHandshake request,
				ServerHandshake response) throws InvalidDataException {
			// To overwrite
			System.out.println(request + ": " + response);
		}
		
		@Override
		public ServerHandshakeBuilder onWebsocketHandshakeReceivedAsServer(WebSocket conn, Draft draft,
				ClientHandshake request) throws InvalidDataException {
			ServerHandshakeBuilder builder = super
			        .onWebsocketHandshakeReceivedAsServer(conn, draft, request);
			//System.out.println(request);
			Collection<WebSocket> conns = getConnections();
			
			for (WebSocket ws : conns) {
				 String s = ws.getResourceDescriptor();
			}
			//test.setWebSocketFactory(wsf2);
			
			//ServerHandshakeBuilder builder = super.onWebsocketHandshakeReceivedAsServer(conn, draft, request);
			return new HandshakeImpl1Server(); 
			//return builder;
		}
 
		 
		@Override
		public void onOpen(WebSocket conn, ClientHandshake handshake) {
			conn.send("Welcome to the server!"); // This method sends a message to the new client
			String s = handshake.getResourceDescriptor();
			broadcast("new connection: " + s); // This method sends a message to all
																				// clients connected
			System.out.println("new connection to " + conn.getRemoteSocketAddress());
		}

		@Override
		public void onClose(WebSocket conn, int code, String reason, boolean remote) {
			System.out.println("closed " + conn.getRemoteSocketAddress() + " with exit code " + code
					+ " additional info: " + reason);
		}

		@Override
		public void onError(WebSocket conn, Exception ex) {
			System.err.println("an error occurred on connection " + conn.getRemoteSocketAddress() + ":" + ex);
		}

		@Override
		public void onStart() {
			System.out.println("server started successfully");
			
		}
		
		/*
		public void doDecode(WebSocketImpl ws, ByteBuffer buf) throws InterruptedException {
	        try {
	          ws.decode(buf);
	        } catch (Exception e) {
	          log.error("Error while reading from remote connection", e);
	        } finally {
	          pushBuffer(buf);
	        }
	      } 
 
		public void pushBuffer(ByteBuffer buf) throws InterruptedException {
		    if (buffers.size() > queuesize.intValue()) {
		      return;
		    }
		    buffers.put(buf);
		  }
		*/
		//
		// Make sure that the substream comes out of a single port of a single process,
		// all together...
		//

		@SuppressWarnings("rawtypes")
		@Override
		public void onMessage(final WebSocket conn, final String message) {

			// WebSocketReceive wsr = (WebSocketReceive) comp;
			// OutputPort outPort = wsr.getOutport();
			LinkedList<Packet<?>> ll = hm.get(conn);
			if (ll == null) {
				ll = new LinkedList<Packet<?>>();
				hm.put(conn, ll);
			}

			System.out.println(message);
			if (message.equals("@kill")) {
				// putGlobal("killsw", new Boolean(true));
				killsw.set(true);
				conn.close(CloseFrame.NORMAL, "Stop WebServer");
				outPort.close();
				return;
			}

			if (message.equals("@close")) {
				conn.close(CloseFrame.NORMAL, "Close message");
				outPort.close();
				return;
			}

			if (message.equals("@{")) {
				ll = hm.get(conn);
				ll.clear();
				Packet lbr = comp.create(Packet.OPEN, "pdata");
				ll.add(lbr);
				// outPort.send(lbr);
				Packet p1 = comp.create(conn);
				ll.add(p1);
				// outPort.send(p1); // conn
				return;
			}

			if (message.equals("@}")) {
				Packet rbr = comp.create(Packet.CLOSE, "pdata");
				ll.add(rbr);
				// outPort.send(rbr);
				for (Packet<?> p : ll)
					outPort.send(p);
				return;
			}

			Packet p2 = comp.create(message);
			ll.add(p2);
			// outPort.send(p2);

		}
 
	}

}
