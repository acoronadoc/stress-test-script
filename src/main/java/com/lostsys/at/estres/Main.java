package com.lostsys.at.estres;

import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class Main {
	private static Vector<Hashtable<String,String>> report=new Vector<Hashtable<String,String>>();
	
	private static String proxyHost=null;
	private static int proxyPort=8080;
	private static int repeats=10;
	private static String url="http://www.google.com";

	public static void main(String[] args) {
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
		loadArgs( args );
		
		for (int i=0; i<repeats; i++) {
			RequestThread rt=new RequestThread(url);
			rt.start();
			}
		
		while (report.size()<repeats) {
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (Exception ex) { ex.printStackTrace(); }
			}
		
		printReport();
		}

	public static void loadArgs(String[] args) {
		
		String prev="";
		for ( int i=0; i<args.length; i++ ) {
			if ( prev.equals("-url") ) url=args[i];
			else if ( prev.equals("-repeat") ) repeats=Integer.parseInt( args[i] );
			else if ( prev.equals("-proxyHost") ) proxyHost=args[i];
			else if ( prev.equals("-proxyPort") ) proxyPort=Integer.parseInt( args[i] );
			
			prev=args[i];
			}
		
		System.out.println("PARAMS");
		System.out.println("======");
		System.out.println("-url "+url);
		System.out.println("-repeat "+repeats);
		if ( proxyHost!=null ) {
			System.out.println("-proxyHost "+proxyHost);
			System.out.println("-proxyPort "+proxyPort);				
			}
		System.out.println("");				
		}
	
	public static void printReport() {
		long ttime=0;
		long mintime=0;
		long maxtime=0;
		
		for ( Hashtable<String,String> reg:report ) {
			long time=Long.parseLong( reg.get("time") );
			
			ttime+=time;
			if ( mintime==0 || mintime>time ) mintime=time;
			if ( maxtime<time ) maxtime=time;
			}
		
		System.out.println("REPORT");
		System.out.println("======");
		System.out.println( "Total requests: "+report.size() );
		System.out.println( "Average time: "+(ttime/report.size())+"ms." );
		System.out.println( "Min time: "+(mintime)+"ms." );
		System.out.println( "Max time: "+(maxtime)+"ms." );
		System.out.println("");						
		}
	
	public static void addReport(String url,long start,long stop,long time) {
		Hashtable<String,String> reg=new Hashtable<String,String>();
		
		reg.put("url", url);
		reg.put("start", ""+start);
		reg.put("stop", ""+stop);
		reg.put("time", ""+time);
		
		report.add(reg);
		}

	public static void checkURL(String url) {
		WebClient webClient = new WebClient( BrowserVersion.CHROME ); 
		
		if (proxyHost!=null) webClient = new WebClient( BrowserVersion.CHROME, proxyHost, proxyPort );

		// Configuramos opciones
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setCssEnabled(true);
		webClient.getOptions().setJavaScriptEnabled(true);

		try {
			// Cargamos página web
			HtmlPage page1 = webClient.getPage( url );
		} catch (Exception ex) { ex.printStackTrace(); }
					
		webClient.close();
		}
}

class RequestThread extends Thread {
	private String url="";
	
	public RequestThread(String url) {
		this.url=url;	
		}
	
    public void run(){
		long start=new Date().getTime();
		Main.checkURL(url);
		long stop=new Date().getTime();
		long time=stop-start;
		
		Main.addReport(url,start,stop,time);
    	}
  }
