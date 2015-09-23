/*
 * Copyright (C) J.P. Morrison, Enterprises, Ltd. 2009, 2012 All Rights Reserved. 
 */
package com.jpmorrsn.fbp.examples.components;

//import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.tree.DefaultMutableTreeNode;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;

/**
 * Sample web socket request processor
 * 
 * This is a non-looper processing one substream per activation
 * 
 * Expected input is a substream, consisting of - open bracket - packet
 * containing socket reference - Java Class WebSocket - packet containing data
 * reference - should be a string, colon, blank, 'namelist' - close bracket
 * 
 * Generated output is a substream, conforming to requirements of
 * WebSocketRespond - in the case of this component, this is - open bracket -
 * packet containing socket reference - Java Class WebSocket - 0 or more packets
 * containing data string references - close bracket
 * 
 */
@ComponentDescription("Simple request processing")
@OutPort("OUT")
@InPort("IN")
public class WebSocketSimProc extends Component {

	static final String copyright = "Copyright 2007, 2014, J. Paul Morrison.  At your option, you may copy, "
			+ "distribute, or make derivative works under the terms of the Clarified Artistic License, "
			+ "based on the Everything Development Company's Artistic License.  A document describing "
			+ "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
			+ "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

	private InputPort inport, jarport;

	private OutputPort outport;

	/*
	 * Make sure that the substream comes out of a single port of a single
	 * process, all together...
	 */

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	protected void execute() {

		// Packet q = jarport.receive();
		// String jarfilename = (String) q.getContent();
		// drop(q);
		// jarport.close();

		LinkedList l = new LinkedList();

		Packet lbr = inport.receive(); // open bracket
		Packet p1 = inport.receive(); // contains Connection
		Packet p2 = inport.receive();
		while (p2.getType() != Packet.CLOSE) {
			String message = (String) p2.getContent();
			l.add(message);
			drop(p2);
			p2 = inport.receive();
		}
		drop(p2);

		String s = (String) l.get(0);
		int i = s.indexOf(":");
		String t = s.substring(0, i);

		if (s.endsWith("namelist")) {
			/*
			 * try { sleep(j * 500); } catch (InterruptedException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); }
			 */
			outport.send(lbr);

			outport.send(p1); // contains Connection
			outport.send(create(t + " Joe Fresh"));
			outport.send(create(t + " Aunt Jemima"));
			outport.send(create(t + " Frankie Tomatto's"));

			outport.send(create(Packet.CLOSE, ""));
		} else

			if (s.endsWith("complist")) {

			outport.send(lbr);

			outport.send(p1); // contains Connection

			s = (String) l.get(1);
			i = s.indexOf(":");
			String jarfilename = s.substring(i + 1);

			Enumeration<?> entries;

			DefaultMutableTreeNode top = new DefaultMutableTreeNode();
			DefaultMutableTreeNode next;

			try {
				JarFile jarFile = new JarFile(jarfilename);

				entries = jarFile.entries();

				while (entries.hasMoreElements()) {
					JarEntry entry = (JarEntry) entries.nextElement();
					// System.out.println(entry);
					outport.send(create(t + " " + entry));

					if (!(entry.isDirectory())) {
						s = entry.getName();
						if (s.toLowerCase().endsWith(".class")) {

							next = top;
							DefaultMutableTreeNode child;
							while (true) {
								i = s.indexOf("/");

								if (i == -1) {
									child = new DefaultMutableTreeNode(s);
									next.add(child);
									break;
								} else {
									String u = s.substring(0, i);
									if (null == (child = findChild(next, u))) {
										child = new DefaultMutableTreeNode(u);
										next.add(child);
									}
									s = s.substring(i + 1);
									next = child;
								}
							}
						}
					}
				}
				jarFile.close();

			} catch (IOException e) {
				e.printStackTrace();
			} finally {

			}
			outport.send(create(Packet.CLOSE, ""));
		} else {
			outport.send(lbr);

			outport.send(p1);
			outport.send(create("unknown keyword"));

			outport.send(create(Packet.CLOSE, ""));
		}

	}

	@SuppressWarnings("unchecked")
	private DefaultMutableTreeNode findChild(DefaultMutableTreeNode current,
			String t) {
		if (current == null)
			return null;
		Enumeration<DefaultMutableTreeNode> e = current.children();
		while (e.hasMoreElements()) {
			DefaultMutableTreeNode node = (e.nextElement());
			Object obj = node.getUserObject();
			if (t.equals((String) obj))
				return node;
		}
		return null;
	}

	@Override
	protected void openPorts() {

		inport = openInput("IN");
		// jarport = openInput("JARFILE");

		outport = openOutput("OUT");

	}
}
