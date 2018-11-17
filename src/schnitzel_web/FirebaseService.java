package schnitzel_web;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.ws.rs.*;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Path("/FirebaseService") 

public class FirebaseService
{  
	@GET @Path("/firebase") @Produces("text/plain")
	public String initializeFirebase()
	{
		String returned = "";
		FirebaseOptions options;		
		try
		{
			FileInputStream serviceAccount = new FileInputStream("schnitzeljagd-9a293-firebase-adminsdk-t2x2r-b2534d086e.json");
		
			options = new FirebaseOptions.Builder()
				    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
				    .setDatabaseUrl("https://schnitzeljagd-9a293.firebaseio.com/")
				    .build();
				FirebaseApp.initializeApp(options);
				
			returned= "firebase successfully initialized.";
			
		}
		catch (FileNotFoundException ex)
		{
			System.out.println("fnfe thrown");
			ex.printStackTrace();
			returned = ex.getStackTrace().toString();
		}
		catch (IOException e)
		{
			System.out.println("ioe thrown");
			e.printStackTrace();
			returned= e.getMessage();
		}
		return returned;
	}
	
	@GET @Path("/score/wins")@Produces("text/plain")
	public int getWins() {return Score.WINS;}
	     
	@GET @Path("/score/losses")@Produces("text/plain")
	public int getLosses() {return Score.LOSSES;}
	     
	@GET @Path("/score/ties")@Produces("text/plain")
	public int getTies() {return Score.TIES;}
	
	@POST @Path("/score/wins")@Produces("text/plain")
	public int increaseWins() { return Score.WINS+=10; }
	     
	@POST @Path("/score/ties")@Produces("text/plain")      
	public int increaseTies() { return Score.TIES+=10;}
	     
	@POST @Path("/score/losses")@Produces("text/plain")         
	public int increaseLosses() {return Score.LOSSES++;}
	
	@GET
	@Path("/score")
	@Produces("application/json")
	public String getScore() {
	   String pattern = 
	      "{ \"wins\":\"%s\", \"losses\":\"%s\", \"ties\": \"%s\"}";
	   return String.format(pattern,  Score.WINS, Score.LOSSES, Score.TIES );
	}
	 
	@PUT
	@Path("/score")
	@Produces("application/json")
	public String update(@QueryParam("wins") int wins, 
	                        @QueryParam("losses") int losses, 
	                        @QueryParam("ties")   int ties) {
	   Score.WINS   = wins;
	   Score.TIES   = ties;
	   Score.LOSSES = losses;
	   String pattern = 
	      "{ \"wins\":\"%s\", \"losses\":\"%s\", \"ties\": \"%s\"}";
	   return String.format(pattern,  Score.WINS, Score.LOSSES, Score.TIES );   
	}
}
