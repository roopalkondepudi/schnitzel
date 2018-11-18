package schnitzel_web;

import java.util.ArrayList;
import java.util.List;

public class Hunt
{

	  public List<Checkpoint> checkpointList;
	  public int curr_item = 0;
	  
	  public Hunt()
	  {
		  checkpointList = new ArrayList<Checkpoint>();
	  }
	  
	  public List<Checkpoint> getCheckpointList()
	  {
		  return checkpointList;
	  }
	  
	  public void setCheckpointList(List<Checkpoint> new_hunt_list)
	  {
		  checkpointList = new_hunt_list;
	  }
	  
	  public void addCheckpoint(Checkpoint checkpoint)
	  {
		  checkpointList.add(checkpoint);
	  }
	  
	  public Checkpoint nextItem()
	  {
		  
		  if(curr_item < checkpointList.size() - 1)
		  {
			  curr_item += 1;
			  return checkpointList.get(curr_item);
		  }
		  return null;
	  }
}