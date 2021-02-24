# OpenFin Java and JavaScript Applications communication using IAB and Channel API. 

### What does this demo do?

1. The Java Application (JavaApp) starts the OpenFin JavaScript Application (JsApp) if it's not already running.
2. The JavaApp is a ChannelProvider, which responds to "GetJavaTime" and "KillJavaApp" actions.
3. The JsApp is a ChannelClient, which dispatches actions "GetJavaTime" to get "currentTimeMillis" and "KillJavaApp" to terminate the JavaApp.

### How does JavaApp know if the JsApp is running?

By using OpenFin Java Adapter API [Application.isRunning()](https://github.com/weiteho/OpenFinJavaJsDemo/blob/main/src/main/java/com/openfin/demo/OpenFinJavaJsChannelDemo.java#L122)

### How does JsApp know if the JavaApp is ready?

By connecting to the Channel [without waiting](https://github.com/weiteho/OpenFinJavaJsDemo/blob/main/OpenFinJs/my-app.html#L37), if it fails, then the JavaApp is not ready. If the JavaApp is not ready, then it [listens to the specified IAB topic "JavaAppReady"](https://github.com/weiteho/OpenFinJavaJsDemo/blob/main/OpenFinJs/my-app.html#L31), which the JavaApp will publish a message to [when it's ready](https://github.com/weiteho/OpenFinJavaJsDemo/blob/main/src/main/java/com/openfin/demo/OpenFinJavaJsChannelDemo.java#L109).

### How to build?

1. install Maven
2. clone this repository 
3. run command: mvn clean install
4. the whole demo app will be zipped in ./target directory

### How to run?

unzip generated zip file then...

#### Start http server at port 8080

1. open cmd window and cd to current directory
2. http-server -p 8080

#### Run demo app

1. open cmd window and cd to current directory
2. java -jar OpenFinJavaJsApps-1.0-SNAPSHOT.jar

## License
MIT

The code in this repository is covered by the included license.

However, if you run this code, it may call on the OpenFin RVM or OpenFin Runtime, which are covered by OpenFin’s Developer, Community, and Enterprise licenses. You can learn more about OpenFin licensing at the links listed below or just email us at support@openfin.co with questions.

https://openfin.co/developer-agreement/ <br/>
https://openfin.co/licensing/

## Support
Please enter an issue in the repo for any questions or problems. Alternatively, please contact us at support@openfin.co 
