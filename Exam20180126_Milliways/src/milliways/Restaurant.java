package milliways;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Restaurant {
	private Map<String,Race> racesMap= new HashMap<>();
	private Map<Integer,Hall> hallsMap= new HashMap<>();
    public Restaurant() {
	}
	
	public Race defineRace(String name) throws MilliwaysException{
		if(racesMap.containsKey(name)) throw new MilliwaysException();
		Race newRace= new Race(name);
		racesMap.put(name, newRace);
	    return newRace;
	}
	
	public Party createParty() {
	    return new Party();
	}
	
	public Hall defineHall(int id) throws MilliwaysException{
	    return null;
	}

	public List<Hall> getHallList() {
		return null;
	}

	public Hall seat(Party party, Hall hall) throws MilliwaysException {
        return null;
	}

	public Hall seat(Party party) throws MilliwaysException {
        return null;
	}

	public Map<Race, Integer> statComposition() {
        return null;
	}

	public List<String> statFacility() {
        return null;
	}
	
	public Map<Integer,List<Integer>> statHalls() {
        return null;
	}

}
