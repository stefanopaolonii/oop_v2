package discounts;
import java.util.*;
import java.util.stream.Collectors;

public class Discounts {
	private Map<Integer,String> cardsMap= new HashMap<>();
	private int cardCounter=0;
	private Map<String,Product> productsMap= new HashMap<>();
	private Map<String,Category> categoriesMap= new HashMap<>();
	private Map<Integer,Purchase> purchasesMap= new HashMap<>();
	private int purchaseCounter=0;
	
	//R1
	public int issueCard(String name) {
		cardCounter++;
		cardsMap.put(cardCounter, name);
	    return cardCounter;
	}
	
    public String cardHolder(int cardN) {
        return cardsMap.get(cardN);
    }
    

	public int nOfCards() {
	       return cardsMap.size();

	}
	
	//R2
	public void addProduct(String categoryId, String productId, double price) 
			throws DiscountsException {
		if(productsMap.containsKey(productId)) throw new DiscountsException();
		if(!categoriesMap.containsKey(categoryId)) categoriesMap.put(categoryId, new Category(categoryId));
		productsMap.put(productId, new Product(productId, categoriesMap.get(categoryId), price));
	}
	
	public double getPrice(String productId) 
			throws DiscountsException {
		if(!productsMap.containsKey(productId)) throw new DiscountsException();
        return productsMap.get(productId).getPrice();
	}

	public int getAveragePrice(String categoryId) throws DiscountsException {
		Set<Product> searchedporProducts=productsMap.values().stream().filter(product->product.getCategory().getId().equals(categoryId)).collect(Collectors.toSet());
		if(searchedporProducts.isEmpty()) throw new DiscountsException();
        return (int) searchedporProducts.stream().mapToDouble(Product::getPrice).average().orElse(0);
	}
	
	//R3
	public void setDiscount(String categoryId, int percentage) throws DiscountsException {
		if(!categoriesMap.containsKey(categoryId)) throw new DiscountsException();
		if(percentage<0 || percentage>50) throw new DiscountsException();
		categoriesMap.get(categoryId).setDiscount(percentage);
	}

	public int getDiscount(String categoryId) {
		if(!categoriesMap.containsKey(categoryId)) return -1;
        return categoriesMap.get(categoryId).getDiscount();
	}

	//R4
	public int addPurchase(int cardId, String... items) throws DiscountsException {
		for(String item:items){
			String[] parts=item.split(":");
			if(!productsMap.containsKey(parts[0])) throw new DiscountsException();
		}
		purchaseCounter++;
		Purchase newPurchase= new Purchase(purchaseCounter, cardId);
		Arrays.asList(items).forEach(item->{String[] parts=item.split(":"); newPurchase.addProduct(productsMap.get(parts[0]),Integer.parseInt(parts[1]));});
        return purchaseCounter;
	}

	public int addPurchase(String... items) throws DiscountsException {
        return addPurchase(0,items);
	}
	
	public double getAmount(int purchaseCode) {
		if(!purchasesMap.containsKey(purchaseCode)) return -1.0;
        return purchasesMap.get(purchaseCode).getTotal()-purchasesMap.get(purchaseCode).getDiscount();
	}
	
	public double getDiscount(int purchaseCode)  {
		if(!purchasesMap.containsKey(purchaseCode)) return -1.0;
        return purchasesMap.get(purchaseCode).getDiscount();
	}
	
	public int getNofUnits(int purchaseCode) {
        if(!purchasesMap.containsKey(purchaseCode)) return -1;
        return purchasesMap.get(purchaseCode).getProductsMap().values().stream().mapToInt(Integer::intValue).sum();
	}
	
	//R5
	public SortedMap<Integer, List<String>> productIdsPerNofUnits() {
        return null;
	}
	
	public SortedMap<Integer, Integer> totalPurchasePerCard() {
        return null;
	}
	
	public int totalPurchaseWithoutCard() {
        return -1;
	}
	
	public SortedMap<Integer, Integer> totalDiscountPerCard() {
        return null;
	}


}
