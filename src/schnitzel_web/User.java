package schnitzel_web;

import java.util.ArrayList;
import java.util.List;

public class User
{
	
	public String name;
	public List<Integer> activities;
	public String photo;
	public List<User> friends;
	// In a real app, would use user IDs, but for smaller purposes have
	// used unique User objects instead of IDs to identify users
	
	public User()
	{
		activities = new ArrayList<Integer>();
		friends = new ArrayList<User>();
	}
	
	public void addActivity(Integer ckpt_cleared) {
		activities.add(ckpt_cleared);
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getPhoto()
	{
		return photo;
	}
	
	public List<User> getFriends() {
		return friends;
	}
	
	public List<Integer> getActivities() {
		return activities;
	}
	
	public void setFriends(List<User> newfriends) {
		friends = newfriends;
	}
	
	public void setActivities(List<Integer> newacts) {
		activities = newacts;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void setPhoto(String photo)
	{
		this.photo = photo;
	}

	public void addFriend(User user2) {
		friends.add(user2);
	}
}
