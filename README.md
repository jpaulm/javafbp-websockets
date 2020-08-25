JavaFBP-WebSockets
===

JavaFBP Support for WebSockets 


General
---

**Latest release: `javafbp-websockets-1.2.4`** 

The jar file can be obtained from `build/libs/`, and the latest Release;  it will be come available on Maven shortly:

[![Maven Central](https://img.shields.io/maven-central/v/com.jpaulmorrison/javafbp-websockets.svg?label=JavaFBP-WebSockets)](https://search.maven.org/search?q=g:%22com.jpaulmorrison%22%20AND%20a:%22javafbp-websockets%22)

This project comprises some components which support WebSockets for JavaFBP, plus a test case to illustrate their use.  The components are basically **@tootallnate**'s AutobahnServerTest code - see the [Java-WebSocket](https://github.com/TooTallNate/Java-WebSocket) web page - split into two JavaFBP components: `WebSocketReceive` and `WebSocketRespond`. 

Some minor checking has been added to the source for this project, but it has not been published to Maven.

The key concept here is that of "substreams", where each substream is delimited by special Information Packets (IPs): `open bracket` and `close bracket`.  The first IP of each substream provides the context information, including an indication of which client sent it.

For video on interactive systems, with demo of JavaFBP-WebSockets, click on https://youtu.be/IvTAexROKSA .

For more background information on JavaFBP, see the README on https://github.com/jpaulm/javafbp .

Web site for FBP: 
* https://jpaulm.github.io/fbp/index.html
 
JavaFBP Syntax and Component API:
* http://jpaulm.github.io/fbp/jsyntax.htm

Prerequisites
---

This project requires JavaFBP and Java-WebSocket to execute. The latest jar file for JavaFBP-WebSockets now contains these jar files, plus the recently added SLF4J folder (used by Java-WebSocket for logging). The Renovate package keeps track of the current releases of these packages, so the JavaFBP-WebSockets jar file will be automatically be kept up to date.

This project used Gradle for (re)building.

<!--Eclipse IDE Integration
---

You can generate an Eclipse project by creating a new folder, doing a `cd` to it, and using the following command:

    gradle eclipse

If you already created an Eclipse project you can run:

    gradle cleanEclipse Eclipse

-->

Running a test
----
This project has one test network, which runs as a server, communicating with an HTML5 client, which is `chat1.html` and/or `chat2.html`. This test can either be run under Eclipse, or can be run from the command line.

For those of you new to Flow-Based Programming, I should stress that this server network is illustrative only - this network is just intended to illustrate the concepts, although there *are* two prewritten, pretested components, which you will use to build your own JavaFBP-WebSockets server:  `WebSocketReceive` and `WebSocketRespond`.  This network also contains a single simple "processing" component - https://github.com/jpaulm/javafbp-websockets/blob/master/src/main/java/com/jpaulmorrison/fbp/examples/components/WebSocketSimProc.java - included to show how your processing logic will handle `open` and `close bracket` IPs, and of course to give you something to run!  Just for fun, I have coded two paths in this component: one which just generates 3 IPs, and one with unpacks a Java `jar` file, based on an incoming "command" string. 

<!--*Two HTML5 scripts* (`chat1` *and* `chat2`) *are provided to allow the software to be tested using multiple concurrent users.*-->

Unpack this project into a folder on your disk.  You can then run the command-line test Server code in the project directory by entering
    
     java -cp "build/libs/javafbp-websockets-x.y.z.jar"  com.jpaulmorrison.fbp.examples.networks.TestWebSockets
    
where `x.y.z` is the current version number (note the double quotes).

In *nix, replace the ; with :.

This will display the message `WebSocketServer starting` on the console.

Alternatively, select Run or Debug under Eclipse.

The client HTML5 scripts called `chat1.html` and `chat2.html` in `src/main/resources/scripts` support two commands which are sent over to the server logic:

- `complist` will cause the server to display the contents of any selected jar file (specified in the `Data` field), and
- `namelist` will cause the server to just output 3 names of restaurants in my neighbourhood!

To run the test:
- start `TestWebSockets`
- open `src/main/resources/scripts/chat1.html` and/or `src/main/resources/scripts/chat2.html` with your favorite web browser 

Eclipse does not allow selecting the browser for the `chatx` scripts, *within* Eclipse, so you need to go into Windows File Explorer, and select a web browser.

There are two `chatx` scripts to allow you to test multiple concurrent users. Let's say you select `chat1.html`:

At this point you should see something like:
![chat1](https://github.com/jpaulm/javafbp-websockets/blob/master/docs/Screen.png "Initial output of chat1")

Fig. 1.

- enter `namelist` in the field prefixed with `Command`
- click on `Send`. 

You should see a list of names of 3 restaurants(!), as follows:

![output](https://github.com/jpaulm/javafbp-websockets/blob/master/docs/Output.png "Run output")

Fig. 2.

(`Server` and `Client1` have been prepended to the output to show visually where the data comes from and which client the data has to be sent back to.)

Note: if your browser gives you a message saying it does not support Websockets, try using Chrome.

On your DOS console, or in Eclipse, you will see some trace output, depending on the setting you choose for `SLF4J` (see below).

Now click on `Stop WS`, and the application will close down.

You can click on `Send` multiple times, before clicking on `Stop WS`.

Some information will be logged on the console - this uses the `SLF4J` tool (http://www.slf4j.org/).  If you want to change the logging level, change the `defaultLogLevel` value in `src\main\resources\simplelogger.properties` .

For some information on how to construct your server program, see the last section of this web page.

Logging
----

Logging parameters can be specified in `simplelogger.properties`, which can be found in `src/main/resources`.

Closing down your test
---------

Go back to the input form, and click on `Stop WS`, and the server will come down, terminating the Web Server.

At the end of the run, you should see something along these lines:

    Run complete.  Time: x.xxx seconds
    Counts: C: 586, D: 588, S: 589, R (non-null): 592, DO: 0    
    
where the counts are respectively: creates, normal drops, sends, non-null receives, and drops done by "drop oldest".  

Some diagrams
---

Here is a diagram of this simple server network, together with the client, shown schematically:

![ClientServer](https://github.com/jpaulm/javafbp-websockets/blob/master/docs/ClientServer.png "Diagram of Client and Server Network")

Fig. 3.

The `Process` block is shown using the "subnet" notation to suggest that any collection of processes can go here, provided they accept "substreams" from `Receive`, and send out "substreams" acceptable to `Respond`.

This is the pattern used in the example code on GitHub: https://github.com/jpaulm/javafbp-websockets/blob/master/src/main/java/com/jpaulmorrison/fbp/examples/components/WebSocketSimProc.java .

We next modify the test application to add a (substream-sensitive) Load Balancer process, and the Process and WebSocketRespond processes have been multiplexed.  The result looks like this:

![ClientServerMultiplex](https://github.com/jpaulm/javafbp-websockets/blob/master/docs/ClientServerMultiplex.png "Diagram of Client and Server Network")

Fig. 4.

Note that LoadBalance in JavaFBP has been updated to be sensitive to substreams - see [LoadBalance](https://github.com/jpaulm/javafbp/blob/master/src/main/java/com/jpaulmorrison/fbp/core/components/routing/LoadBalance.java) .

There is also a video on [YouTube](https://youtu.be/IvTAexROKSA) . 


Constructing a server program.
---

As you can see from Fig. 3. above, the server code is basically a U-shape, with a `Receive` block at the top or start, and a `Respond` block at the bottom or end.  

If this were control flow, you might think that each process is invoked and then returns.  Instead, in FBP, each process is a separate machine that **keeps running** as long as it is fed requests from the client or clients - and all the processes comprising the "U" run concurrently! `Receive` doesn't just run once and then terminate - it sits and waits for data to arrive from WebSockets, passes it on, and goes back to waiting patiently.  Same for `Respond`: its job is to wait for data to come from the processing logic, send it to WebSockets and go back to waiting - same for any other blocks on the server side.  Different mental image - I just wanted to stress that!

As described above, communication within the server is mediated by what are called "substreams", where each substream is delimited by special Information Packets (IPs): `open bracket` and `close bracket`. The first IP of each substream provides the context information, including an indication of which client sent it.  This means that any processes within the server have to "understand" substreams.  Of course, between the `Receive` and `Respond`, you can have any pattern of processes and subnets that accepts a substream and outputs another one!

There is an example of what a single substream processor might look like in https://github.com/jpaulm/javafbp-websockets/blob/master/src/main/java/com/jpaulmorrison/fbp/examples/components/WebSocketSimProc.java  in this GitHub repo. For your purposes you can ignore the logic following `if (s.endsWith("complist"))` ...  (The commented out `sleep` after `if (s.endsWith("namelist"))` was just inserted to do some performance testing.)  The `WebSocketSimProc.java` component receives all the non-bracket IPs of a single substream and adds them to a linked list.  On receiving the `close bracket`, it then does whatever processing is appropriate (perhaps based on information in the first IP after the `open bracket`), and outputs the output substream.

Give it a try!


