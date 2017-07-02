/*
 * Copyright (C) J.P. Morrison, Enterprises, Ltd. 2009, 2014 All Rights Reserved. 
 */
package com.jpaulmorrison.fbp.examples.networks;

/* 
 * Useful info:  http://stackoverflow.com/questions/18900187/processing-how-to-send-data-through-websockets-to-javascript-application
 * 
 * This project requires java_websocket.jar - obtainable from Maven java-websocket 
 */


import com.jpaulmorrison.fbp.core.components.routing.LoadBalance;
import com.jpaulmorrison.fbp.components.WebSocketReceive;
import com.jpaulmorrison.fbp.components.WebSocketRespond;
import com.jpaulmorrison.fbp.core.engine.Network;
import com.jpaulmorrison.fbp.examples.components.WebSocketSimProc;


/**
 * This network uses the JavaFBP WebSocketReceive and WebSocketRespond components - these are basically 
 * TooTallNate's AutobahnServerTest code, split into two processes -
 * not to be confused with TestSockets, which uses WriteToSocket and ReadFromSocket! 
 */

public class TestWebSockets extends Network {

  @Override
  protected void define() {

    component("WSRcv", WebSocketReceive.class);
    component("LBal", LoadBalance.class);
    component("Process0", WebSocketSimProc.class);
    component("WSRsp0", WebSocketRespond.class);
    //component("Process1", WebSocketSimProc.class);
    //component("WSRsp1", WebSocketRespond.class);
    //component("Process2", WebSocketSimProc.class);
    //component("WSRsp2", WebSocketRespond.class);
    
    initialize(new Integer(9003), "WSRcv.PORT");

    connect("WSRcv.OUT", "LBal.IN", 4);    
    connect("LBal.OUT[0]", "Process0.IN", 4);
    connect("Process0.OUT", "WSRsp0.IN", 4);   
    //connect("LBal.OUT[1]", "Process1.IN", 4);
    //connect("Process1.OUT", "WSRsp1.IN", 4); 
    //connect("LBal.OUT[2]", "Process2.IN", 4);
    //connect("Process2.OUT", "WSRsp2.IN", 4); 
    
  }

  public static void main(final String[] argv) throws Exception {
    Network net = new TestWebSockets();
    //net.runTimeReqd = false;
    net.go();
  }

}
