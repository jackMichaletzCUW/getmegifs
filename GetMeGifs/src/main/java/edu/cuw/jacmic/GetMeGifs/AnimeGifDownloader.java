package edu.cuw.jacmic.GetMeGifs;
import java.awt.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class AnimeGifDownloader {
	
	public static String outputDirectory = "";
	public static String queriesFile = "";
	
	public static void main(String[] args)
	{
		/*System.out.println(System.getProperty("user.dir"));
		//System.out.println(Downloader.formSearchURL(args));
		
		for(String url : Downloader.getURLS(Downloader.getPageSource(Downloader.formSearchURL(args)))) {
			//if(url.contains(".gif")) {
				System.out.println(url);
			//}
		}*/
		
		queriesFile = args[0];
		outputDirectory = args[1];
		
		ArrayList<String> queriesList = new ArrayList<String>();
		
		try {
			queriesList = new ArrayList<String>(Files.readAllLines(Paths.get(queriesFile)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//System.out.println(Downloader.getPageSource(Downloader.formSearchURL(queriesList.get(0).split(" "))));
		
		for(String query : queriesList)
		{
			Downloader.downloadImages(query.split(" "));
		}
		
		System.out.println();
		System.out.println();
	}
	
}
