package schnitzel_web;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.ws.rs.*;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

@Path("/FirebaseService") 

public class FirebaseService
{  
	String profileInfo = "";
	@POST @Path("/firebase")@Produces("text/plain")
	public String initializeFirebase()
	{
		FirebaseOptions options;
		try
		{
			FileInputStream serviceAccount = new FileInputStream("schnitzeljagd-9a293-firebase-adminsdk-t2x2r-b2534d086e.json");
		
			options = new FirebaseOptions.Builder()
				    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
				    .setDatabaseUrl("https://schnitzeljagd-9a293.firebaseio.com/")
				    .build();
				FirebaseApp.initializeApp(options);
			return "firebase initialization success!";
		}
		catch (FileNotFoundException ex)
		{
			System.out.println("fnfe thrown");
			ex.printStackTrace();
		}
		catch (IOException e)
		{
			System.out.println("ioe thrown");
			e.printStackTrace();
		}
		return "firebase initialization failure.";
	}

	@GET @Path("/user")@Produces("text/plain")
	public String getProfileInfo(@QueryParam("name") String username)
	{
		// read user class from database
		DatabaseReference ref = FirebaseDatabase.getInstance()
			    .getReference("users/" + username);
		
		final CountDownLatch done = new CountDownLatch(1);
		ref.addListenerForSingleValueEvent((ValueEventListener) new ValueEventListener()
		{
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				User new_user = dataSnapshot.getValue(User.class);
				profileInfo = new_user.getName() + ", " + new_user.getPhoto();
		        done.countDown();
			}

			@Override
			public void onCancelled(DatabaseError error) {
				System.out.println("There was a problem");
			}
		});
		
		try {
			done.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return profileInfo;
	}
	
	@PUT @Path("/adduser")@Produces("text/plain")
	public String addUser(@QueryParam("username") String name, @QueryParam("userphoto") String photo)
	{
		User user = new User();
		user.setName(name);
		user.setPhoto(photo);
		
		DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users/");
		Map<String, Object> userUpdates = new HashMap<>();
		userUpdates.put(user.getName(), user);
		usersRef.updateChildrenAsync(userUpdates);
		
		return "user added.";
	}

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
