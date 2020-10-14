/*
 * Copyright (C) J.P. Morrison, Enterprises, Ltd. 2009, 2012 All Rights Reserved. 
 */
package com.jpaulmorrison.fbp.components;


import org.java_websocket.WebSocket;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jpaulmorrison.fbp.components.WebSocketReceive.MyWebSocketServer;
import com.jpaulmorrison.fbp.core.engine.*;


@InPort("IN")
public class WebSocketRespond extends Component {

  private InputPort inport;

  /**
   * General component to receive sequence of substreams, and use them to return data to the client
   * 
   * Expected input is a series of substreams, each consisting of
   *  - open bracket
   *  - packet containing socket reference - Java Class WebSocket
   *  - 0 or more packets containing data string references
   *  - close bracket
   *  
   */
  
  @Override
  protected void execute() throws Exception {

	final Logger log = LoggerFactory.getLogger(WebSocketRespond.class);
    while (true) {
      Packet<?> lbr = inport.receive();
      if (lbr == null) {
        break;
      }
      drop(lbr);      
      Packet<?> p1 = inport.receive();
      WebSocket conn = (WebSocket) p1.getContent();      
      drop(p1);
      try {
      conn.send("@{");
      } catch(WebsocketNotConnectedException e) {
    	  log.warn(conn + " not connected");
      }
      log.info("@{");
      
      Packet<?> p2 = inport.receive();
      while (p2.getType() != Packet.CLOSE) {

          String message = (String) p2.getContent();
          try {
              conn.send(message);
              } catch(WebsocketNotConnectedException e) {
            	  log.warn(conn + " not connected");
              }  
          //log.trace(message);
          log.info(message);
          drop(p2);
      
          p2 = inport.receive();
      }
      
      drop(p2);
      try {
          conn.send("@}");
          } catch(WebsocketNotConnectedException e) {
        	  log.warn(conn + " not connected");
          }
      log.info("@}");

    }
    WebSocketReceive.MyWebSocketServer wss = (MyWebSocketServer) getGlobal("WebSocketServer");
    wss.stop();
  }

  /* (non-Javadoc)
   * @see com.jpaulmorrison.fbp.core.engine.Component#openPorts()
   */
  @Override
  protected void openPorts() {
    inport = openInput("IN");

  }

}
