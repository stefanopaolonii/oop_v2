package warehouse;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Warehouse {
	private Map<String,Product> productsMap= new HashMap<>();
	private Map<String,Supplier> suppliersMap= new HashMap<>();
	private Map<String,Order> ordersMap= new HashMap<>();
	private int orderCounter=1;
	
	public Warehouse() {
	}

	public Product newProduct(String code, String description){
		productsMap.put(code, new Product(code, description));
		return productsMap.get(code);
	}
	
	public Collection<Product> products(){
		return productsMap.values().stream().collect(Collectors.toList());
	}

	public Product findProduct(String code){
		return productsMap.get(code);
	}

	public Supplier newSupplier(String code, String name){
		// TODO: completare
		return null;
	}
	
	public Supplier findSupplier(String code){
		// TODO: completare
		return null;
	}

	public Order issueOrder(Product prod, int quantity, Supplier supp)
		throws InvalidSupplier {
		// TODO: completare
		return null;
	}

	public Order findOrder(String code){
		// TODO: completare
		return null;
	}
	
	public List<Order> pendingOrders(){
		// TODO: completare
		return null;
	}

	public Map<String,List<Order>> ordersByProduct(){
	    return null;
	}
	
	public Map<String,Long> orderNBySupplier(){
	    return null;
	}
	
	public List<String> countDeliveredByProduct(){
	    return null;
	}
}
