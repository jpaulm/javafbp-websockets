/*
 * Copyright (C) J.P. Morrison, Enterprises, Ltd. 2009, 2014 All Rights Reserved. 
 */
package com.jpaulmorrison.fbp.components;


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
 */

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
//import org.java_websocket.drafts.Draft_10;
//import org.java_websocket.drafts.Draft_17;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.exceptions.InvalidHandshakeException;
import org.java_websocket.framing.CloseFrame;

import org.java_websocket.handshake.*;
import org.java_websocket.server.WebSocketServer;

import com.jpaulmorrison.fbp.core.engine.*;

@InPort("PORT")
@OutPort("OUT")
public class WebSocketReceive extends Component {

  private InputPort portPort;
  private OutputPort outPort;
  AtomicBoolean killsw;
  //LinkedList<Packet<?>> ll = null;
  HashMap <WebSocket, LinkedList<Packet<?>>> hm = null;

  /* (non-Javadoc)
   * @see com.jpaulmorrison.fbp.core.engine.Component#execute()
   */
  @Override
  protected void execute() throws Exception {

    //putGlobal("killsw", new Boolean(false));
    // WebSocketImpl.DEBUG = true;   // Replaced by slf4j stuff!   
    killsw = new AtomicBoolean();
    
    //ll = new LinkedList<Packet<?>>(); 
    hm = new HashMap <WebSocket, LinkedList<Packet<?>>>();
    
    Packet<?> p = portPort.receive();
    Integer i = (Integer) p.getContent();
    int port = i.intValue();
    drop (p);
    portPort.close();

    //WebSocketServer wss = new MyWebSocketServer(port, new Draft_10());
    WebSocketServer wss = new MyWebSocketServer(port);
    // Draft 17 - Hybi 17/RFC 6455 and is currently supported by Chrome16+ and IE10.
    // Draft 10 -  Hybi 10. This draft is supported by Chrome15 and Firefox6-9.

    //wss.setReuseAddress(true);
    //wss.stop();
    System.out.println("WebSocketServer starting");
    putGlobal("WebSocketServer", wss);
    
    wss.start();
    
    while (true) {

      try {
        sleep(500); // sleep for 1/2 sec
      } catch (InterruptedException e) {
        e.printStackTrace();
        outPort.close();
        wss.stop();
        // handle the exception...        
        return;
      }
      //Boolean killsw = (Boolean) getGlobal("killsw");
      if (killsw.get()) {
    	// see also http://stackoverflow.com/questions/4812686/closing-websocket-correctly-html5-javascript
    	outPort.close();
        wss.stop();
        return;
      }
    }
  }

  /* (non-Javadoc)
   * @see com.jpaulmorrison.fbp.core.engine.Component#openPorts()
   */
  @Override
  protected void openPorts() {

	portPort = openInput("PORT");
    outPort = openOutput("OUT");

  }  

  class MyWebSocketServer extends WebSocketServer {

    //private static int counter = 0;

    Component comp = null;
    
    //public MyWebSocketServer(final int port, final Draft d) throws UnknownHostException {
     //   super(new InetSocketAddress(port), Collections.singletonList(d));

     // }

    public MyWebSocketServer(final int port) throws UnknownHostException {
      super(new InetSocketAddress(port));

    }

    
	//public MyWebSocketServer(final InetSocketAddress address, final Draft d) {
     // super(address, Collections.singletonList(d));
    //}

    @Override
    public void onOpen(final WebSocket conn, final ClientHandshake handshake) {
    	System.out.println("onOpen");
		}
    
    public void onStart(){
    	System.out.println("onStart");
    }

    
	public ServerHandshakeBuilder onWebsocketHandshakeReceivedAsServer(
				WebSocket conn, Draft draft, ClientHandshake request)
				throws InvalidDataException {
			ServerHandshakeBuilder resp = new HandshakeImpl1Server();
			System.out.println("onWebsocketHandshakeReceivedAsServer");
			resp.setHttpStatusMessage("HTTP/1.1 101 Switching Protocols\r\n");

			String val = request.getFieldValue("sec-websocket-protocol");
			if (!(val.equals(""))) {
				resp.put("sec-websocket-protocol", val);
			}  // experimental
				
				try {
					resp = (ServerHandshakeBuilder) draft
							.postProcessHandshakeResponseAsServer(request, resp);
				} catch (InvalidHandshakeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			//}
			return resp;
		}
    
    @Override
    public void onClose(final WebSocket conn, final int code, final String reason, final boolean remote) {
      //System.out.println("closed " + code + " " + reason + " " + remote);
      System.out.println( "Connection closed (" + code + ") by " + ( remote ? "remote peer" : "us" ) );
    }

    @Override
    public void onError(final WebSocket conn, final Exception ex) {
    	if (ex instanceof ClosedByInterruptException)
    		return;
      System.out.println("Error:");
      ex.printStackTrace();
    }

    /*
	 * Make sure that the substream comes out of a single port of a single process, all together...
	 */
    
		@SuppressWarnings("rawtypes")
		@Override
		public void onMessage(final WebSocket conn, final String message) {

			//WebSocketReceive wsr = (WebSocketReceive) comp;
			//OutputPort outPort = wsr.getOutport();
			LinkedList<Packet<?>> ll = hm.get(conn);
			if (ll == null) {
				ll = new LinkedList<Packet<?>>();
				hm.put(conn, ll);
			}
			
			System.out.println(message);
			if (message.equals("@kill")) {
				//putGlobal("killsw", new Boolean(true));
				killsw.set(true);
				return;
			}
			
			if (message.equals("@close")) {
				conn.close(CloseFrame.NORMAL, "Close message");
				return;
			} 
			
			if (message.equals("@{")) {
				ll = hm.get(conn); 
				ll.clear();
				Packet lbr = comp.create(Packet.OPEN, "pdata");	
				ll.add(lbr);
				//outPort.send(lbr);
				Packet p1 = comp.create(conn);
				ll.add(p1);
				//outPort.send(p1); // conn
				return;
			}
			
			if (message.equals("@}")) {
				Packet rbr = comp.create(Packet.CLOSE, "pdata");
				ll.add(rbr);
				//outPort.send(rbr);
				for (Packet<?> p : ll) 
					outPort.send(p); 
				return;
			}
			
			Packet p2 = comp.create(message);
			ll.add(p2);
			//outPort.send(p2);
							
			}

		 

    @Override
    public void onMessage(final WebSocket conn, final ByteBuffer blob) {
    	System.out.println(blob);
      conn.send(blob);
    }
    
    //public void onWebsocketMessageFragment(final WebSocket conn, final Framedata frame) {
    //	System.out.println(frame);
    //  FrameBuilder builder = (FrameBuilder) frame;
    //  builder.setTransferemasked(false);
    //  conn.sendFrame(frame);
    //}

    @Override
    public void start() {
      
      comp = (Component) Thread.currentThread();

      new Thread(this).start();
    }

    

  }

}
