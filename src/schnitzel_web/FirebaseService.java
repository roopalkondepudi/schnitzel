package schnitzel_web;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
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
	String allCheckpointInfo = "";
	
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
				System.out.println("There was a problem in getting the profile information.");
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
	public void addUser(@QueryParam("name") String name, @QueryParam("photo") String photo)
	{			
		User user = new User();
		user.setName(name);
		user.setPhoto(photo);

		DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users/");
		Map<String, Object> userUpdates = new HashMap<>();
		userUpdates.put(user.getName(), user);
		usersRef.updateChildrenAsync(userUpdates);
	}

	@GET @Path("/huntInfo")@Produces("text/plain")
	public String getAllCheckpoints(String huntname)
	{		
		// read user class from database
		final CountDownLatch done = new CountDownLatch(1);

		DatabaseReference ref = FirebaseDatabase.getInstance().getReference("hunts/" + huntname + "/checkpoints");
		
		ref.addListenerForSingleValueEvent((ValueEventListener) new ValueEventListener()
		{
			  @Override
			  public void onDataChange(DataSnapshot snapshot)
			  {
				  for (DataSnapshot postSnapshot: snapshot.getChildren())
				  {
					  Checkpoint checkpoint = postSnapshot.getValue(Checkpoint.class);
			          allCheckpointInfo += checkpoint.receiveCheckpointInfo() +"| ";
			      }
			      done.countDown();
			  }

			  @Override
			  public void onCancelled(DatabaseError error)
			  {
				  System.out.println("There was a problem in getting the checkpoints.");
			  }
		});
			
		try {
			done.await(); //it will wait till the response is received from firebase.
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
		return allCheckpointInfo;
	}
	
	@PUT @Path("/huntInfo/addCheckpoint")@Produces("text/plain")
	public String addTestCheckpoint()
	{
		DatabaseReference db = FirebaseDatabase.getInstance().getReference();
		
		DatabaseReference checkpoint_ref = db.child("hunts");
		Checkpoint h1 = new Checkpoint();
		h1.setLat(1.0);
		h1.setLon(2.0);
		h1.setThreshold(2.4);
		h1.setProgress(43.64);
		h1.setHint("1");
		
		Checkpoint h2 = new Checkpoint();
		h2.setLat(4.2);
		h2.setLon(2.5);
		h2.setThreshold(2.9);
		h2.setProgress(4.4);
		h2.setHint("2");
		
		Checkpoint h3 = new Checkpoint();
		h3.setLat(4.67);
		h3.setLon(9.0);
		h3.setThreshold(86.64);
		h3.setProgress(3.5);
		h3.setHint("3");
		
		ArrayList<Checkpoint> checkpoints = new ArrayList<Checkpoint>();
		checkpoints.add(h1);
		checkpoints.add(h2);
		checkpoints.add(h3);
		
		Hunt hunt = new Hunt();
		hunt.setCheckpointList(checkpoints);		
		
		Map<String, Hunt> hunt_map = new HashMap<>();
		hunt_map.put("test_hunt", hunt);
		checkpoint_ref.setValueAsync(hunt_map);	
		
		String ret = checkpoints.get(0).getHint() + ", " + checkpoints.get(1).getHint() + ", " + checkpoints.get(2).getHint();
		return ret;
	}
	     
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
