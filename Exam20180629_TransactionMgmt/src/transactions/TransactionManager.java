package transactions;
import java.util.*;
import java.util.stream.Collectors;

//import static java.util.stream.Collectors.*;
//import static java.util.Comparator.*;

public class TransactionManager {
	public enum Type{REQUEST,OFFER};
	private Map<String,Region> regionsMap= new HashMap<>();
	private Map<String,Carrier> carriersMap= new HashMap<>();
	private Map<String,RequestOffer> roMap= new HashMap<>();
	private Map<String,Transaction> transactionsMap= new HashMap<>();
//R1
	public List<String> addRegion(String regionName, String... placeNames) { 
		regionsMap.put(regionName, new Region(regionName, placeNames));
		return regionsMap.get(regionName).getPlacesSet().stream().sorted().collect(Collectors.toList());
	}
	
	public List<String> addCarrier(String carrierName, String... regionNames) { 
		Carrier newCarrier= new Carrier(carrierName);
		Arrays.asList(regionNames).forEach(rname->newCarrier.addRegion(regionsMap.get(rname)));
		carriersMap.put(carrierName,newCarrier);
		return newCarrier.getRegionsSet().stream().map(Region::getName).sorted().collect(Collectors.toList());
	}
	
	public List<String> getCarriersForRegion(String regionName) { 
		return carriersMap.values().stream().filter(carrier->carrier.getRegionsSet().stream().map(Region::getName).collect(Collectors.toList()).contains(regionName)).map(Carrier::getName).collect(Collectors.toList());
	}
	
//R2
	public void addRequest(String requestId, String placeName, String productId) 
			throws TMException {
	}
	
	public void addOffer(String offerId, String placeName, String productId) 
			throws TMException {
	}
	

//R3
	public void addTransaction(String transactionId, String carrierName, String requestId, String offerId) 
			throws TMException {
	}
	
	public boolean evaluateTransaction(String transactionId, int score) {
		return false;
	}
	
//R4
	public SortedMap<Long, List<String>> deliveryRegionsPerNT() {
		return new TreeMap<Long, List<String>>();
	}
	
	public SortedMap<String, Integer> scorePerCarrier(int minimumScore) {
		return new TreeMap<String, Integer>();
	}
	
	public SortedMap<String, Long> nTPerProduct() {
		return new TreeMap<String, Long>();
	}
	
	
}

