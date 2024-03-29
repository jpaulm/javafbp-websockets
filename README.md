JavaFBP-WebSockets
===

### JavaFBP Support for WebSockets 

This project uses [JavaFBP](https://github.com/jpaulm/javafbp) for the server side, HTML5 and JavaScript for the clients.  It also uses modules from **@tootallnate**'s Java-WebSocket GitHub project - see the [Java-WebSocket](https://github.com/TooTallNate/Java-WebSocket) web page.  Thanks, @tootallnate !

General web site on Flow-Based Programming: https://jpaulm.github.io/fbp/ .


General
---

**Latest release: `javafbp-websockets-1.3.3`** 

The jar file can be obtained from `build/libs/`, Releases and Maven.   The new release should be up on Maven shortly (there is a period when the shield shows the new release number, but it is not yet up on Maven).  The Maven shields are below.

[![Maven Central](https://img.shields.io/maven-central/v/com.jpaulmorrison/javafbp-websockets.svg?label=JavaFBP-WebSockets)](https://search.maven.org/search?q=g:%22com.jpaulmorrison%22%20AND%20a:%22javafbp-websockets%22)  [![Maven Central](https://img.shields.io/maven-central/v/com.jpaulmorrison/javafbp.svg?label=JavaFBP)](https://search.maven.org/search?q=g:%22com.jpaulmorrison%22%20AND%20a:%22javafbp%22) [![Maven Central](https://img.shields.io/maven-central/v/ch.qos.logback/logback-classic.svg?label=LogBack-Classic)](https://search.maven.org/search?q=g:%22ch.qos.logback%22%20AND%20a:%22logback-classic%22) 
 
 

This project comprises some components which support WebSockets for JavaFBP, plus a test case to illustrate their use.  The components are based on the "AutobahnServerTest" code on the [Java-WebSocket](https://github.com/TooTallNate/Java-WebSocket) web page - split into two JavaFBP components: `WebSocketReceive` and `WebSocketRespond`. 

The key concept here is that of FBP "substreams", where each substream is delimited by special Information Packets (IPs): `open bracket` and `close bracket`.  The first IP of each substream (right after the `open bracket`) provides the context information, including an indication of which client sent it.  This is also used to direct the output to the correct client.

**Note:** I have included two client scripts: `chat1.html` (using `ws:`) and `chat2.html` (using `wss:`), in `...\GitHub\javafbp-websockets\src\main\resources\scripts` - see below: **Running a test**. 

The underlying software [Java-WebSocket](https://github.com/TooTallNate/Java-WebSocket) does not support WS and WSS on the same run, so you have to specify which option you are running - TLS or non-TLS (plain text) - by means of a run-time argument - see below. For Test Status, see below.

<!-- To run these clients, use Chrome, create a self-signed certificate called `keystore.jks`, indicate that it is a **trusted** certificate (for Windows use MMC), and store it in `c:\Users\<user>\AppData\Local\JavaFBP-WebSockets\security` (Windows, of course).

The `keystore.jks` file can be generated using this command from a `CMD` window:

`keytool -genkey -keyalg RSA -validity 3650 -keystore "keystore.jks" -storepass "storepassword" -keypass "keypassword" -alias "default" -dname "CN=127.0.0.1, OU=MyOrgUnit, O=MyOrg, L=MyCity, S=MyRegion, C=MyCountry"`, substituting as appropriate in `-dname`, and copying the resulting `keystore.jks` file to `c:\Users\<user>\AppData\Local\JavaFBP-WebSockets\security`. -->

The Chrome command `chrome://flags/#allow-insecure-localhost` (Enabled) indicates to Chrome that the security certificate is invalid - if you figure out how to create a valid security certifcate, please let me know (in Issue #35)!  

For video on interactive systems, with demo of JavaFBP-WebSockets, click on https://youtu.be/IvTAexROKSA .

For more background information on JavaFBP, see the README on https://github.com/jpaulm/javafbp .

Web site for FBP: 
* https://jpaulm.github.io/fbp/index.html
 
JavaFBP Syntax and Component API:
* http://jpaulm.github.io/fbp/jsyntax.htm

Prerequisites
---

This project requires JavaFBP and Java-WebSocket jar files to execute. The latest jar file for JavaFBP-WebSockets now contains these jar files, plus the recently added SLF4J and LogBack folders (used by Java-WebSocket for logging). The Renovate package keeps track of the current releases of these packages, so the JavaFBP-WebSockets jar file will automatically be kept up to date.

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

For those of you new to Flow-Based Programming, I should stress that this server network is illustrative only - this network is just intended to illustrate the concepts, although there *are* two prewritten, pretested components, which you will use to build your own JavaFBP-WebSockets server:  `WebSocketReceive` and `WebSocketRespond`.  This network also contains a single simple "processing" component - https://github.com/jpaulm/javafbp-websockets/blob/master/src/main/java/com/jpaulmorrison/fbp/examples/components/WebSocketSimProc.java - included to show how your processing logic will handle `open` and `close bracket` IPs, and of course to give you something to run!  Just for fun, I have coded two paths in this component: one which just generates 3 IPs, and one which unpacks a Java `jar` file, based on an incoming "command" string. 

The current server network also includes a random delay (from 0 to 2 seconds) in the receive-respond loop to simulate some processing.

To run this test, download the jar file - then position the current directory to your `javafbp-websockets` directory.

Now in DOS, run these commands:
    
- plain text: `java -jar build\libs\javafbp-websockets-x.y.z.jar`

OR

- TLS:        `java -jar build\libs\javafbp-websockets-x.y.z.jar tls`

    
where `x.y.z` is the current version number

<!--In *nix, replace the ; with :.-->

This will display the message `WebSocketServer starting` on the console, followed by `server started successfully`.

This particular test case supports two client HTML5 scripts called `chat1.html` (using `ws:`) and `chat2.html` (using `wss:`) in `src/main/resources/scripts`.  These scripts in turn support two commands which can be sent over to the server logic - other values will be rejected:

- `complist` will cause the server to display the contents of any selected jar file (specified in the `Data` field), and
- `namelist` will cause the server to just output 3 names of restaurants in my neighbourhood!

Locate the script you want (`chat1` or `chat2`) in `src\main\resources\scripts', and open with Chrome.  Then enter `complist` or `namelist`.  Input and output will apear on the console.   When you are done, click on `Stop WS`.

Test Status of latest release
---

- Port number 9003 was used previously - however it now seems to be used by PID 4 - so we are now using port no. 8887 in the test (as suggested by `Java-WebSocket`).

- For some reason, this won't run directly from the `.jar` file, but `java -jar build/libs/javafbp-websockets-1.3.3.jar` works!

- These scripts apparently have to be submitted from the File Explorer - they do not seem to work when submitted from the Eclipse file list.  

- Client script `chat1` works with Firefox and Chrome; 
 
- Client script `chat2` works with Chrome browser, provided `chrome://flags/#allow-insecure-localhost` is enabled for Chrome. 

Sample Run Output
---

Let's say you select `chat1.html` and Open With Chrome.  At this point you should see something like:
![chat1](https://github.com/jpaulm/javafbp-websockets/blob/master/docs/Screen.png "Initial output of chat1")

Fig. 1.

- enter `namelist` in the field prefixed with `Command`
- click on `Send`. 

You should see a list of names of 3 restaurants(!), as follows:

![output](https://github.com/jpaulm/javafbp-websockets/blob/master/docs/Output.png "Run output")

Fig. 2.

(`Server` and `Client1` have been prepended to the output to show visually where the data comes from and which client the data has to be sent back to.)

<!--Note: if your browser gives you a message saying it does not support Websockets, try using Chrome. -->

On your DOS console, or in Eclipse, you will see some trace output, depending on the setting you choose for `SLF4J` (see below).

Now click on `Stop WS`, and the application will close down.

You can click on `Send` multiple times, before clicking on `Stop WS`.

LogBack Logging
-------
Some information will be logged on the console - this uses the Apache LogBack tool, via the `slf4j` interface.  If you want to change the logging level, change the Root level value in `logback.xml` in the `classpath`:

    <?xml version="1.0" encoding="UTF-8"?>
    <Configuration status="WARN">
      <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
          <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
        </Console>
      </Appenders>
      <Loggers>
        <Root level="info">
          <AppenderRef ref="Console"/>
        </Root>
      </Loggers>
    </Configuration>
    
Copy this into your `build.libs` folder, or wherever your `jar` file is..    
    
JavaFBP tracing
------
Since this application uses JavaFBP, JavaFBP tracing is controlled by the `JavaFBPProperties.xml` file in your `<user>` directory.


Closing down your test
---------

Go back to the input form, and click on `Stop WS`, and the server will come down, terminating the Web Server.

At the end of the run, you should see something along these lines:

    Run complete.  Time: x.xxx seconds
    Counts: C: 586, D: 588, S: 589, R (non-null): 592, DO: 0    
    
where the counts are respectively: creates, normal drops, sends, non-null receives, and drops done by "drop oldest".  

Some diagrams
---

Here is a diagram of this simple server network, together with the client, shown schematically. **Note: This is not a true DrawFBP network**, as the left-hand side is just a schematic, but the idea is to give you an overview...

![ClientServer](https://github.com/jpaulm/javafbp-websockets/blob/master/docs/ClientServer.png "Diagram of Client and Server Network")

Fig. 3.

The `Process` block is shown using the "subnet" notation to suggest that any collection of processes can go here, provided they accept "substreams" from `Receive`, and send out "substreams" acceptable to `Respond`.

This is the pattern used in the example code on GitHub: https://github.com/jpaulm/javafbp-websockets/blob/master/src/main/java/com/jpaulmorrison/fbp/examples/components/WebSocketSimProc.java .

We next modify the test application to add a (substream-sensitive) Load Balancer process, and the Process and WebSocketRespond processes have been multiplexed.  The result looks like this:

![ClientServerMultiplex](https://github.com/jpaulm/javafbp-websockets/blob/master/docs/ClientServerMultiplex.png "Diagram of Client and Server Network")

Fig. 4.

Note that LoadBalance in JavaFBP has been updated to be sensitive to substreams - see [LoadBalance](https://github.com/jpaulm/javafbp/blob/master/src/main/java/com/jpaulmorrison/fbp/core/components/routing/LoadBalance.java) .

There is also a video on [YouTube](https://youtu.be/IvTAexROKSA) . 

Finally, I thought I would include a schematic of the server side of a Brokerage package I worked on a few years ago...  

![Brokerage App Schematic](https://jpaulm.github.io/fbp/EB2a.jpg "Schematic for Server Side of Brokerage Application (expand blocks, add caches, multiplexing)")

Fig. 5.

It is of interest because it shows additional paths through the network, but to make a full-fledged app, it will need caches, subnets to be expanded, and parts can be multiplexed for performance.  The processes called "Back-end" communicate with offline (usually batch) functions, so their resulting outputs have to be coordinated with the requests sent to them.  

The client side is represented only by the little stick figures - obviously they do not result in any code being generated (in *this* network)...


Constructing a server program.
---

As you can see from Fig. 3. above, the server code is basically a U-shape, with a `Receive` block at the top or start, and a `Respond` block at the bottom or end.  

If this were control flow, you might think that each process is invoked and then returns.  Instead, in FBP, each process is a separate machine that *keeps running* as long as it is fed requests from the client or clients - and all the processes comprising the "U" run concurrently! `Receive` doesn't just run once and then terminate - it sits and waits for data to arrive from WebSockets, passes it on, and goes back to waiting patiently.  Same for `Respond`: its job is to wait for data to come from the processing logic, send it to WebSockets and go back to waiting - same for any other blocks on the server side.  Different mental image - I just wanted to stress that!

As described above, communication within the server is mediated by what are called "substreams", where each substream is delimited by special Information Packets (IPs): `open bracket` and `close bracket`. The first IP of each substream provides the context information, including an indication of which client sent it.  This means that any processes within the server have to "understand" substreams.  Of course, between the `Receive` and `Respond`, you can have any pattern of processes and subnets that accepts a substream and outputs another one!

There is an example of what a single substream processor might look like in https://github.com/jpaulm/javafbp-websockets/blob/master/src/main/java/com/jpaulmorrison/fbp/examples/components/WebSocketSimProc.java  in this GitHub repo. For your purposes you can ignore the logic following `if (s.endsWith("complist"))` ...  (The commented out `sleep` after `if (s.endsWith("namelist"))` was just inserted to do some performance testing.)  The `WebSocketSimProc.java` component receives all the non-bracket IPs of a single substream and adds them to a linked list.  On receiving the `close bracket`, it then does whatever processing is appropriate (perhaps based on information in the first IP after the `open bracket`), and outputs the output substream.
