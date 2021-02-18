package com.openfin.demo;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.locks.LockSupport;

import org.json.JSONObject;

import com.openfin.desktop.Application;
import com.openfin.desktop.DesktopConnection;
import com.openfin.desktop.DesktopException;
import com.openfin.desktop.DesktopIOException;
import com.openfin.desktop.DesktopStateListener;
import com.openfin.desktop.OpenFinRuntime;
import com.openfin.desktop.RuntimeConfiguration;
import com.openfin.desktop.channel.Channel;
import com.openfin.desktop.channel.ChannelProvider;

public class OpenFinJavaJsChannelDemo {

	private DesktopConnection desktopConnection;

	public OpenFinJavaJsChannelDemo() {
		this.launchRuntime();
	}

	private void launchRuntime() {
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					Thread currentThread = Thread.currentThread();
					System.out.println("Launching OpenFin runtime......");
					RuntimeConfiguration config = new RuntimeConfiguration();
					config.setRuntimeVersion("stable");
					desktopConnection = new DesktopConnection("OpenFinJavaJsChannelDemoApplication");
					try {
						desktopConnection.connect(config, new DesktopStateListener() {

							@Override
							public void onReady() {
								System.out.println("OpenFin runtime ready......");
								OpenFinJavaJsChannelDemo.this.initChannel();
							}

							@Override
							public void onClose(String error) {
								System.out.println("OpenFin desktopConnection disconnected.....");
								LockSupport.unpark(currentThread);
							}

							@Override
							public void onError(String reason) {
							}

							@Override
							public void onMessage(String message) {
							}

							@Override
							public void onOutgoingMessage(String message) {
							}
						}, 60);
					}
					catch (DesktopIOException | IOException e) {
						e.printStackTrace();
					}
				}
				catch (DesktopException e) {
					e.printStackTrace();
				}
				finally {
					System.out.println("OpenFin launcher thread sleeps.");
					LockSupport.park();
					System.out.println("OpenFin launcher thread exits.");
				}
			}
		};
		t.start();
	}

	private void initChannel() {
		System.out.println("Creating ChannelProvider......");
		Channel c = this.desktopConnection.getChannel("OpenFinJavaJsChannelDemo");
		c.create((provider) -> {
			System.out.println("ChannelProvider created......");

			provider.register("GetJavaTime", (action, payload, senderIdentity) -> {
				long currentTimeMillis = System.currentTimeMillis();
				JSONObject result = new JSONObject();
				result.put("currentTimeMillis", currentTimeMillis);
				System.out.println("ChannelClient dispatched GetJavaTime, returning: " + result.toString());
				return result;
			});

			provider.register("KillJavaApp", (action, payload, senderIdentity) -> {
				try {
					System.out.println("ChannelClient dispatched KillJavaApp, disconnecting......");
					this.desktopConnection.disconnect();
				}
				catch (DesktopException e) {
					e.printStackTrace();
				}
				return new JSONObject();
			});
			
			
			try {
				desktopConnection.getInterApplicationBus().publish("JavaAppReady", new JSONObject());
			}
			catch (DesktopException e) {
				e.printStackTrace();
			}
			
			launchJsApp();
			
		});
	}
	
	private void launchJsApp() {
		Application jsApp = Application.wrap("openfin_js_app", this.desktopConnection);
		jsApp.isRunning(running-> {
			if (!running) {
				System.out.println("OpenFin JavaScript App NOT running, starting it......");
				try {
					Application.createFromManifest("http://localhost:8080/OpenFinJs/app.json", app -> {
						try {
							app.run();
						}
						catch (DesktopException e) {
							e.printStackTrace();
						}
					}, null, this.desktopConnection);
				}
				catch (DesktopException e) {
					e.printStackTrace();
				}
			}
			else {
				System.out.println("OpenFin JavaScript App is already running......");
			}
		}, null);
	}

	public static void main(String[] args) throws Exception {
		new OpenFinJavaJsChannelDemo();
	}
}
