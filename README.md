JavaFBP-WebSockets
===

JavaFBP Support for WebSockets 


General
---

**Latest release: `javafbp-websockets-1.2.1`** 

This project comprises some components which support WebSockets for JavaFBP, plus a test case to illustrate their use.  The components are basically **@tootallnate**'s AutobahnServerTest code - see https://github.com/TooTallNate/Java-WebSocket - split into two JavaFBP components: `WebSocketReceive` and `WebSocketRespond`.

Promoted to Maven, July 12, 2017.  To locate, do http://search.maven.org/#search%7Cga%7C1%7Cjavafbp-websockets .

For video on interactive systems, with demo of JavaFBP-WebSockets, click on https://youtu.be/IvTAexROKSA .

For more background information on JavaFBP, see the README on https://github.com/jpaulm/javafbp .

Web site for FBP: 
* http://www.jpaulmorrison.com/fbp/
 
JavaFBP Syntax and Component API:
* http://www.jpaulmorrison.com/fbp/jsyntax.htm

Prerequisites
---

This project requires JavaFBP and Java-WebSocket to execute. Recent jar files for these projects will be zipped into the JavaFBP-WebSockets jar file, and will also be in the `lib` directory.

It also requires Gradle for (re)building (tested with version 2.0). You can download the corresponding package from the following URL: http://www.gradle.org

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
Run `git init` to create the `.git` directory.

Run `git clone https://github.com/jpaulm/javafbp-websockets.git`

Run `gradle build` in your JavaFBP-WebSockets directory - this will create a `javafbp-websockets-1.2.0.jar` file in the `build/libs` directory - this also contains a test network, called `TestWebSockets.java`, the two prerequisite jar files, and a couple of "chat" HTML5 scripts.  This only has to be done once.

Running a test
----
This project has one test network, which runs as a server, communicating with the client, which is `chat1.html` and/or `chat2.html`. This test can either be run under Eclipse, or can be run from the command line.

*Two HTML5 scripts are provided to allow the software to be tested using multiple concurrent users.*

Note: if your default browser gives you a message saying it does not support Websockets, try using Chrome.

You can run the command-line test Server code in com.jpmorrsn.fbp.websockets.networks.TestWebSockets by entering in the project directory
    
     java -cp "build/libs/javafbp-websockets-1.2.1.jar"  com.jpaulmorrison.fbp.examples.networks.TestWebSockets
    
(note the double quotes).

In *nix, replace the ; with :.

This will display the message `WebSocketServer starting` on the console.

There are two simple, almost identical, client HTML5 scripts called `chat1.html` and `chat2.html` in `src/main/resources/scripts`, which support two commands:

- `complist` will display the contents of any selected jar file (specified in the `Data` field), and
- `namelist` which just outputs 3 names.

To run the test:
- start `TestWebSockets`
- open `chat1` and/or `chat2` with your favorite web browser 
- enter `complist` in the field prefixed with `Command`
- enter the file name of any jar file whose contents you wish to display, in the field prefixed with `Data`, e.g. `C:\Users\Paul\Documents\GitHub\javafbp-websockets\lib\javafbp-4.1.0.jar`
- click on `Send`. 

You should see all the entries in the selected jar file.  

**OR** 

Enter `namelist` in the `Command` field, which will show three names on the user screen.

Eclipse
-------

To run or rebuild the project under Eclipse, you will need to add the JavaFBP and Java-WebSocket jar files in the `lib` directory to the `Properties/Java Build Path/Libraries` using the `Add JARs` function.

To rebuild the project under Eclipse, you will also need to add `tools.jar` from your current Java JDK. 


You may have to do a trivial edit (e.g. add a blank) to the `chat1.html` and `chat2.html` files after downloading them - see issue #4.


Closing down your test
---------

Go back to the input form, and click on `Stop WS`, and the server should come down, terminating the Web Server.

At the end of the run, you should see:

    Run complete.  Time: x.xxx seconds
    Counts: C: 586, D: 588, S: 589, R (non-null): 592, DO: 0    or something similar)
    
where the counts are respectively: creates, normal drops, sends, non-null receives, and drops done by "drop oldest".  

Here is a diagram of this simple server network, together with the client, shown schematically:

![ClientServer](https://github.com/jpaulm/javafbp-websockets/blob/master/docs/ClientServer.png "Diagram of Client and Server Network")

The test application has now been modified to add a (substream-sensitive) Load Balancer process, and the Process and WebSocketRespond processes have been multiplexed.  The result looks like this:

![ClientServerMultiplex](https://github.com/jpaulm/javafbp-websockets/blob/master/docs/ClientServerMultiplex.png "Diagram of Client and Server Network")

Note that LoadBalance in JavaFBP has been updated to be sensitive to substreams - see https://github.com/jpaulm/javafbp/blob/master/src/main/java/com/jpaulmorrison/fbp/core/components/routing/LoadBalance.java .


