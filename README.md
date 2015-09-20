JavaFBP-WebSockets
===

JavaFBP Support for WebSockets 


General
---

This project comprises some components which support WebSockets for JavaFBP, plus a test case to illustrate their use.  The components are basically @tootallnate's AutobahnServerTest code, split into two JavaFBP components: WebSocketReceive and WebSocketRespond.

For more background information on JavaFBP, see the README on https://github.com/jpaulm/javafbp .

Web sites for FBP: 
* http://www.jpaulmorrison.com/fbp/
* https://github.com/flowbased/flowbased.org/wiki
 
JavaFBP Syntax and Component API:
* http://www.jpaulmorrison.com/fbp/jsyntax.htm

Prerequisites
---


The project requires Gradle for building (tested with version 2.0). You can download the corresponding package from the following URL: 
http://www.gradle.org

Windows and Linux users should follow the installation instructions on the Maven website (URL provided above).

OSX users (using Brew, http://brew.sh) can install Maven by executing the following command:

    brew install gradle


Eclipse IDE Integration
---

You can generate Eclipse project using the following mvn command:

    gradle eclipse

If you already created an Eclipse project you can run:

    gradle cleanEclipse Eclipse

You need to install a Gradle plugin for Eclipse as explain here:
https://github.com/spring-projects/eclipse-integration-gradle/
Then import a generated project in Eclipse, right (ctrl for OSX) click on the project in Eclipse -> Configure -> Convert to Gradle Project. After the conversion you can Right (ctrl for OSX) click on the project -> Gradle -> Task Quick Launcher and type `build`.


Building from command line
---

**Latest release: `javafbp-websockets-1.0.2`**.

For building the project simply run the following command:

    gradle build

As a result a `javafbp-websockets-1.0.2.jar` file will be created in the `build/libs` directory. This will include two core components, and an example network plus associated test component, and some HTML and JavaScript files.


Running a test
----


This uses an additional jar file: `Java-WebSocket-1.3.0.jar`, available in the central Maven repository, and also requires the `javafbp-3.0.2.jar` file, available in `javafbp/build/libs`, provided that you have built the JavaFBP project first, or have the jar file otherwise available.

Note: if your default browser gives you a message saying it does not support Websockets, use Chrome.

You can now run the command-line test Server code in com.jpmorrsn.fbp.websockets.networks.TestWebSockets by entering in the project directory

     java -cp "build/libs/javafbp-websockets-1.0.2.jar;../javafbp/build/libs/javafbp-3.0.2.jar;
     lib/java-websocket-1.3.0.jar" com.jpmorrsn.fbp.examples.networks.TestWebSockets
    
(note the double quotes).

This will display the message `WebSocketServer starting` on the console.

There is a very simple client script called `chat1` in `com/jpmorrsn/fbp/websockets/script`: 
- open `chat1` or `chat2` with your favorite web browser while server is running
- enter `complist` in input field (just below the output container)
- click on `Send`. 

You should see all the entries in the JavaFBP jar file, whose location is given in the network definition.  `chat1` and `chat2` are identical scripts provided to allow concurrent testing of multiple clients.

If running this test under Eclipse, you can add `Java-WebSocket-1.3.0.jar` and `javafbp-3.0.2.jar` to Run/Debug Settings/Launch Configuration for `TestWebSockets`.

Now go back to the input form, and click on `Stop WS`, and the server should come down, terminating the Web Server.

At the end of the run, you should see:

    Run complete.  Time: x.xxx seconds
    Counts: C: 586, D: 588, S: 589, R (non-null): 592, DO: 0    or something similar)
    
where the counts are respectively: creates, normal drops, sends, non-null receives, and drops done by "drop oldest".  

Here is a diagram of this simple server network, together with the client, shown schematically:

![ClientServer](https://github.com/jpaulm/javafbp-websockets/blob/master/docs/ClientServer.png "Diagram of Client and Server Network")

