package edu.cuw.jacmic.GetMeGifs;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import com.gargoylesoftware.css.parser.CSSErrorHandler;
import com.gargoylesoftware.css.parser.CSSException;
import com.gargoylesoftware.css.parser.CSSParseException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.IncorrectnessListener;
import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener;

public class Downloader {

	public static void showProgress(int decile)
	{	
		System.out.printf("\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b");
		for(int i = 0; i < 30; i++)
		{
			if(i < decile) {
				System.out.printf("%c", (char )(9601 + (i / 4)));
			} else {
				System.out.printf(" ");
			}
		}
	}
	
	public static void downloadImages(String[] args) {
		ArrayList<String> URLs = Downloader.getURLS(Downloader.getPageSource(Downloader.formSearchURL(args)));
		
		String directory = AnimeGifDownloader.outputDirectory;
		String name = "";
				
		for(int i = 0; i < args.length - 1; i++)
		{
			name += args[i];
		}
				
		name += args[args.length - 1];
		
		System.out.println("\ndownloading " + URLs.size() + " gifs with query " + name + "...");
		
		//makeDirectory(directory);
		
		int decile = URLs.size() / 30;
		
		decile = (decile == 0 ? 1 : decile);
		
		for(int i = 0; i < URLs.size(); i++)
		{
			//System.out.printf("%d: downloading %s\n", i, URLs.get(i));
			
			if(i % decile == 0)
			{
				showProgress(i / decile);
			}
			
			saveFile(URLs.get(i), directory + "/" + name + i + ".gif");
		}
	}
	
	public static void makeDirectory(String path)
	{
		try {
			Files.createDirectory(Paths.get(path));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void saveFile(String sourceURL, String path) {
		try {
			URL website = new URL(sourceURL);
		    HttpURLConnection con = (HttpURLConnection)website.openConnection();
			ReadableByteChannel rbc = Channels.newChannel(con.getInputStream());
			FileOutputStream fos = new FileOutputStream(path);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
		}
	}
	
	public static String getPageSource(String url)
	{		
		WebClient webClient = new WebClient();
		HtmlPage page;
		String pageAsXml = "";
		try {
			webClient.setCssErrorHandler(new CSSErrorHandler() {
				public void warning(CSSParseException exception) throws CSSException {}
				public void fatalError(CSSParseException exception) throws CSSException {}
				public void error(CSSParseException exception) throws CSSException {}
			});
			webClient.setJavaScriptErrorListener(new JavaScriptErrorListener() {
				public void warn(String message, String sourceName, int line, String lineSource, int lineOffset) {}
				public void timeoutError(HtmlPage page, long allowedTime, long executionTime) {}
				public void scriptException(HtmlPage page, ScriptException scriptException) {}
				public void malformedScriptURL(HtmlPage page, String url, MalformedURLException malformedURLException) {}
				public void loadScriptError(HtmlPage page, URL scriptUrl, Exception exception) {}
			});
			webClient.setIncorrectnessListener(new IncorrectnessListener() {
				public void notify(String message, Object origin) {}
			});
			page = webClient.getPage(url);
			pageAsXml = page.asXml();
			webClient.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}

		return pageAsXml;
	}
	
	public static ArrayList<String> getURLS(String source) {
		
		ArrayList<String> links = new ArrayList<String>();
		
		long comparison = 0;
		long HTTP = 0x2268747470L;
		
		for(int i = 0; i < source.length(); i++)
		{
			if((comparison ^ HTTP) == 0)
			{
				// we found a url
				int quoteIndex = i;
				
				for(int j = i; j < source.length(); j++)
				{
					if(source.charAt(j) == '"')
					{
						quoteIndex = j;
						comparison = 0;
						break;
					}
				}
				
				links.add(source.substring(i - 4, quoteIndex));
				i = quoteIndex;
			} else {
				comparison = 0xFFFFFFFFFFL & ((comparison << 8) | source.charAt(i));
			}
		}
		
		int max = links.size();
		ArrayList<String> rv = new ArrayList<String>();
		
		for(int i = 0; i < max; i++) {
			if(links.get(i).endsWith(".gif") && !links.get(i).contains("giphy")) {
				rv.add(links.get(i));
			}
		}
		
		return rv;
	}
	
	public static String formSearchURL(String[] keywords) {
		String url = "https://www.google.com/search?q=";
		
		for (int i = 0; i < keywords.length - 1; i++) {
			url += keywords[i] + "+";
		}
		
		url += keywords[keywords.length - 1] + "&tbm=isch";
		
		return url;
	}
	
}
