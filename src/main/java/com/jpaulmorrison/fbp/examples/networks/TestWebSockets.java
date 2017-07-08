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
import com.jpaulmorrison.fbp.core.components.routing.RandomDelay;
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

		int multiplexNo = 6;
		component("WSRcv", WebSocketReceive.class);
		component("LBal", LoadBalance.class);
		for (int i = 0; i < multiplexNo; i++) {
			component("Process" + i, WebSocketSimProc.class);
			component("WSRsp" + i, WebSocketRespond.class);
			component("RD" + i, RandomDelay.class);
		}

		initialize(new Integer(9003), "WSRcv.PORT");

		connect("WSRcv.OUT", "LBal.IN", 4);
		for (int i = 0; i < multiplexNo; i++) {
			connect("LBal.OUT[" + i + "]", "RD" + i + ".IN", 4);
			connect("RD" + i + ".OUT", "Process" + i + ".IN", 4);
			connect("Process" + i + ".OUT", "WSRsp" + i + ".IN", 4);
		}

	}

  public static void main(final String[] argv) throws Exception {
    Network net = new TestWebSockets();
    //net.runTimeReqd = false;
    net.go();
  }

}
