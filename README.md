JavaFBP-WebSockets
===

JavaFBP Support for WebSockets 


General
---

This project comprises some components which support WebSockets for JavaFBP, plus a test case to illustrate their use.  The components are basically **@tootallnate**'s AutobahnServerTest code, split into two JavaFBP components: WebSocketReceive and WebSocketRespond.

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


Building from command line
---

**Latest release: `javafbp-websockets-1.1.0`**.

For building the project simply run the following command:

    gradle build

As a result a `javafbp-websockets-1.1.0.jar` file will be created in the `build/libs` directory. This will include two core components, and an example network plus associated test component, and some HTML and JavaScript files.


Running a test
----

This project has one test network, which runs as a server, communicating with the client, which is `chat1.html` or `chat2.html`. This test can either be run under Eclipse, or can be run using the project jar file and the jar file for JavaFBP.  You will also need to add an additional jar file: `Java-WebSocket-1.3.0.jar`, available in the central Maven repository, to the Project/Properties/Java Build Path.  The current JavaFBP jar file is `javafbp-4.1.0.jar`, available in the latest release of JavaFBP on GitHub, so this assumes that you have built the JavaFBP project first, or have the jar file otherwise available.

Note: if your default browser gives you a message saying it does not support Websockets, try using Chrome.

You can now run the command-line test Server code in com.jpmorrsn.fbp.websockets.networks.TestWebSockets by entering in the project directory

     java -cp "build/libs/javafbp-websockets-1.1.0.jar;lib/javafbp-4.1.0.jar; lib/java-websocket-1.3.0.jar" com.jpaulmorrison.fbp.examples.networks.TestWebSockets
    
(note the double quotes).

This will display the message `WebSocketServer starting` on the console.

There are two simple client scripts called `chat1.html` and `chat2.html` in `com/jpaulmorrison/fbp/websockets/script`, which will display the contents of any selected jar file.  To run the test:
- make sure server is running (`TestWebSockets`)
- open `chat1` or `chat2` with your favorite web browser 
- enter `complist` in the field prefixed with `Command`
- enter the file name of the jar file whose contents you wish to display, in the field prefixed with `Data`
- click on `Send`. 

You should see all the entries in the selected jar file.  `chat1` and `chat2` are identical scripts provided to allow concurrent testing of multiple clients.

Eclipse
-------

If running this test under Eclipse, you can add `Java-WebSocket-1.3.0.jar` and `javafbp-4.1.0.jar` to Run/Debug Settings/Launch Configuration for `TestWebSockets`.

**OR**

Add these two jar files to the Properties/Java Build Path/Libraries for your project.

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




