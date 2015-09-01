package com.xxx.transaction.tranfser.rest;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;

import com.xxx.transaction.tranfser.TradingSystem;
import com.xxx.transaction.tranfser.TransferFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.resource.Resource;

import com.sun.jersey.spi.container.servlet.ServletContainer;
import com.xxx.transaction.tranfser.Daemon;
import org.mortbay.thread.QueuedThreadPool;


public class TransferServer implements Runnable {
	
	static final Log LOG = LogFactory.getLog(TransferServer.class);
	
	private Daemon daemon = null;
	
	private Server server = null; 
	
	public void join() throws InterruptedException{
		if(server != null)
			server.join(); 
	}
	
	@Override
	public void run() {
		try {

            // first, we try to start up the trading system.
            TradingSystem tradingSystem = new TradingSystem();
            TransferFactory.setTransfer(tradingSystem);
			
			ServletHolder sh = new ServletHolder(ServletContainer.class);
			sh.setInitParameter(
					"com.sun.jersey.config.property.resourceConfigClass",
					ResourceConfig.class.getCanonicalName());
			
			server = new Server();
			int port = 8080;
			Connector connector = new SelectChannelConnector();
			connector.setPort(port);
			connector.setHost("0.0.0.0");
			connector.setMaxIdleTime(60 * 1000); 
			connector.setLowResourceMaxIdleTime(60 * 1000); 
//			connector.setHeaderBufferSize(1024);
//	        connector.setRequestBufferSize(2048);
//	        connector.setResponseBufferSize(4096);

			server.addConnector(connector);

			WebAppContext webAppContext = new WebAppContext();
			final String appDir = getWebAppsPath("jar");
			//final String appDir = "src/main/java/webapps";
			webAppContext.setContextPath("/");
			webAppContext.setBaseResource(Resource.newResource(appDir));
            webAppContext.setAttribute("name", "value");
			server.addHandler(webAppContext);

			int maxThreads = 256;
			int minThreads = 4;
			QueuedThreadPool threadPool = new QueuedThreadPool(maxThreads);
			threadPool.setMinThreads(minThreads);
			server.setThreadPool(threadPool);

			server.setSendServerVersion(false);
			server.setSendDateHeader(false);
			server.setStopAtShutdown(true);
			// set up context

			// start server
			server.start();
		} catch(Exception e ) {
			e.printStackTrace(); 
		}
	}
	
	private String getWebAppsPath(String type) throws IOException {
		ClassLoader loader = this.getClass().getClassLoader();
		String class_file = (new StringBuilder()).append(
				this.getClass().getName().replaceAll("\\.", "/")).append(
				".class").toString();
		URL url = loader.getResource(class_file);
		String toReturn = "";
		if (type.equals(url.getProtocol())) {
			toReturn = url.toString();
			toReturn = toReturn.replaceAll("\\+", "%2B");
			toReturn = URLDecoder.decode(toReturn, "UTF-8");
			toReturn = toReturn.replaceAll("!.*$", "!/webapps");
		} else {
			toReturn = loader.getResource("webapps").toString();
		}
		return toReturn;
	}
	
	public static void startServer(String avg[])throws Exception {
		
		TransferServer server = new TransferServer();
		server.daemon = new Daemon(server); 
		server.daemon.start();
		
		server.daemon.join();
	}
	

	public static void main(String args[])throws Exception {
		startServer(args); 
		LOG.info("ccc"); 
	}

}
