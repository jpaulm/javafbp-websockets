JavaFBP-WebSockets
===

JavaFBP Support for WebSockets 


General
---

**Latest release: `javafbp-websockets-1.1.2`**.

This project comprises some components which support WebSockets for JavaFBP, plus a test case to illustrate their use.  The components are basically **@tootallnate**'s AutobahnServerTest code - see https://github.com/TooTallNate/Java-WebSocket - split into two JavaFBP components: `WebSocketReceive` and `WebSocketRespond`.

For more background information on JavaFBP, see the README on https://github.com/jpaulm/javafbp .

Web site for FBP: 
* http://www.jpaulmorrison.com/fbp/
 
JavaFBP Syntax and Component API:
* http://www.jpaulmorrison.com/fbp/jsyntax.htm

Prerequisites
---

This project requires JavaFBP to be installed - see https://github.com/jpaulm/javafbp .

It also requires Gradle for building (tested with version 2.0). You can download the corresponding package from the following URL: http://www.gradle.org

Eclipse IDE Integration
---

You can generate Eclipse project using the following mvn command:

    gradle eclipse

If you already created an Eclipse project you can run:

    gradle cleanEclipse Eclipse

You need to install a Gradle plugin for Eclipse as explained here:
https://github.com/spring-projects/eclipse-integration-gradle/
Then import a generated project in Eclipse, right (ctrl for OSX) click on the project in Eclipse -> Configure -> Convert to Gradle Project. After the conversion you can Right (ctrl for OSX) click on the project -> Gradle -> Task Quick Launcher and type `build`.

You should also make sure that the current Java JDK `tools.jar` file is in your project's `lib/` directory.

Building project from command line
---

Run `gradle build` in your JavaFBP-WebSockets directory - this will download `Java-WebSocket-1.3.0.jar`and `javafbp-4.1.0.jar` from Maven into the `lib/` directory.  This will also create a `javafbp-websockets-1.1.2.jar` file in the `build/libs` directory - this also contains a test network, called `TestWebSockets.java` and a couple of "chat" HTML files.

Running a test
----

This project has one test network, which runs as a server, communicating with the client, which is `chat1.html` or `chat2.html`. This test can either be run under Eclipse, or can be run from the command line.

Note: if your default browser gives you a message saying it does not support Websockets, try using Chrome.

You can run the command-line test Server code in com.jpmorrsn.fbp.websockets.networks.TestWebSockets by entering in the project directory

     java -cp "build/libs/javafbp-websockets-1.1.2.jar;lib/javafbp-4.1.0.jar;lib/java-websocket-1.3.0.jar" com.jpaulmorrison.fbp.examples.networks.TestWebSockets
    
(note the double quotes).

In *nix, replace the ; with :.

This will display the message `WebSocketServer starting` on the console.

There are two simple client scripts called `chat1.html` and `chat2.html` in `src/main/resources/scripts`, which will display the contents of any selected jar file.  To run the test:
- make sure server is running (`TestWebSockets`)
- download `chat1` or `chat2`
- open `chat1` or `chat2` with your favorite web browser 
- enter `complist` in the field prefixed with `Command`
- enter the file name of any jar file whose contents you wish to display, in the field prefixed with `Data`, e.g. `C:\Users\Paul\Documents\GitHub\javafbp-websockets\lib\javafbp-4.1.0.jar`
- click on `Send`. 

You should see all the entries in the selected jar file.  

Or enter `namelist` which will show three names on the user screen.

`chat1` and `chat2` are identical scripts provided to allow concurrent testing of multiple clients.

Eclipse
-------

If you need to rebuild the project under Eclipse, you will need to add `tools.jar` to the `Properties/Java Build Path/Libraries`.

If running this test under Eclipse, you can add `Java-WebSocket-1.3.0.jar` and `javafbp-4.1.0.jar` to Run/Debug Settings/Launch Configuration for `TestWebSockets`.

**OR**

Add these two jar files to the `Properties/Java Build Path/Libraries` for your project.

Closing down your test
---------

Now go back to the input form, and click on `Stop WS`, and the server should come down, terminating the Web Server.

At the end of the run, you should see:

    Run complete.  Time: x.xxx seconds
    Counts: C: 586, D: 588, S: 589, R (non-null): 592, DO: 0    or something similar)
    
where the counts are respectively: creates, normal drops, sends, non-null receives, and drops done by "drop oldest".  

Here is a diagram of this simple server network, together with the client, shown schematically:

![ClientServer](https://github.com/jpaulm/javafbp-websockets/blob/master/docs/ClientServer.png "Diagram of Client and Server Network")

The test application has now been modified to add a (substream-sensitive) Load Balancer process, and the Process and WebSocketRespond processes have been multiplexed.  The result looks like this:

![ClientServerMultiplex](https://github.com/jpaulm/javafbp-websockets/blob/master/docs/ClientServerMultiplex.png "Diagram of Client and Server Network")

Note that LoadBalance in JavaFBP has been updated to be sensitive to substreams - see https://github.com/jpaulm/javafbp/blob/master/src/main/java/com/jpaulmorrison/fbp/core/components/routing/LoadBalance.java .


