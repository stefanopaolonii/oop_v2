package delivery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;


public class Delivery {
	public enum Status {PENDING,ASSIGNED};
	List<String> categoriesList= new ArrayList<>();
	Map<String,Restaurant> restaurantsMap = new HashMap<>();
	Map<Integer,Order> ordersMap= new TreeMap<>();
	private int orderCounter=0;
	// R1
	
    /**
     * adds one category to the list of categories managed by the service.
     * 
     * @param category name of the category
     * @throws DeliveryException if the category is already available.
     */
	public void addCategory (String category) throws DeliveryException {
		if(categoriesList.contains(category)) throw new DeliveryException();
		categoriesList.add(category);
	}
	
	/**
	 * retrieves the list of defined categories.
	 * 
	 * @return list of category names
	 */
	public List<String> getCategories() {
		return categoriesList;
	}
	
	/**
	 * register a new restaurant to the service with a related category
	 * 
	 * @param name     name of the restaurant
	 * @param category category of the restaurant
	 * @throws DeliveryException if the category is not defined.
	 */
	public void addRestaurant (String name, String category) throws DeliveryException {
		if(!categoriesList.contains(category)) throw new DeliveryException();
		restaurantsMap.put(name, new Restaurant(name, category));
	}
	
	/**
	 * retrieves an ordered list by name of the restaurants of a given category. 
	 * It returns an empty list in there are no restaurants in the selected category 
	 * or the category does not exist.
	 * 
	 * @param category name of the category
	 * @return sorted list of restaurant names
	 */
	public List<String> getRestaurantsForCategory(String category) {
        return restaurantsMap.values().stream().filter(restaurant->restaurant.getCategory().equals(category)).map(Restaurant::getName).sorted(String::compareTo).collect(Collectors.toList());
	}
	
	// R2
	
	/**
	 * adds a dish to the list of dishes of a restaurant. 
	 * Every dish has a given price.
	 * 
	 * @param name             name of the dish
	 * @param restaurantName   name of the restaurant
	 * @param price            price of the dish
	 * @throws DeliveryException if the dish name already exists
	 */
	public void addDish(String name, String restaurantName, float price) throws DeliveryException {
		if(restaurantsMap.get(restaurantName).getDishesMap().keySet().contains(name)) throw new DeliveryException();
		restaurantsMap.get(restaurantName).addDish(name, price);
	}
	
	/**
	 * returns a map associating the name of each restaurant with the 
	 * list of dish names whose price is in the provided range of price (limits included). 
	 * If the restaurant has no dishes in the range, it does not appear in the map.
	 * 
	 * @param minPrice  minimum price (included)
	 * @param maxPrice  maximum price (included)
	 * @return map restaurant -> dishes
	 */
	public Map<String,List<String>> getDishesByPrice(float minPrice, float maxPrice) {
        return restaurantsMap.values().stream().filter(restaurant->restaurant.getDishesMap().values().stream().anyMatch(dish-> dish.getPrice()>=minPrice && dish.getPrice()<=maxPrice)).collect(Collectors.toMap(Restaurant::getName, restaurant-> restaurant.getDishesMap().values().stream().filter(dish->dish.getPrice()>=minPrice && dish.getPrice()<=maxPrice).map(Dish::getName).collect(Collectors.toList())));
	}
	
	/**
	 * retrieve the ordered list of the names of dishes sold by a restaurant. 
	 * If the restaurant does not exist or does not sell any dishes 
	 * the method must return an empty list.
	 *  
	 * @param restaurantName   name of the restaurant
	 * @return alphabetically sorted list of dish names 
	 */
	public List<String> getDishesForRestaurant(String restaurantName) {
		if(!restaurantsMap.containsKey(restaurantName)) return Collections.emptyList();
        return restaurantsMap.get(restaurantName).getDishesMap().keySet().stream().sorted().collect(Collectors.toList());
	}
	
	/**
	 * retrieves the list of all dishes sold by all restaurants belonging to the given category. 
	 * If the category is not defined or there are no dishes in the category 
	 * the method must return and empty list.
	 *  
	 * @param category     the category
	 * @return 
	 */
	public List<String> getDishesByCategory(String category) {
        return restaurantsMap.values().stream().filter(restaurant->restaurant.getCategory().equals(category)).flatMap(restaurant->restaurant.getDishesMap().keySet().stream()).distinct().collect(Collectors.toList());
	}
	
	//R3
	
	/**
	 * creates a delivery order. 
	 * Each order may contain more than one product with the related quantity. 
	 * The delivery time is indicated with a number in the range 8 to 23. 
	 * The delivery distance is expressed in kilometers. 
	 * Each order is assigned a progressive order ID, the first order has number 1.
	 * 
	 * @param dishNames        names of the dishes
	 * @param quantities       relative quantity of dishes
	 * @param customerName     name of the customer
	 * @param restaurantName   name of the restaurant
	 * @param deliveryTime     time of delivery (8-23)
	 * @param deliveryDistance distance of delivery
	 * 
	 * @return order ID
	 */
	public int addOrder(String dishNames[], int quantities[], String customerName, String restaurantName, int deliveryTime, int deliveryDistance) {
		Order newOrder= new Order(++orderCounter, customerName, restaurantsMap.get(restaurantName), deliveryTime, deliveryDistance);
		ordersMap.put(orderCounter, newOrder );
		for(int i=0;i<dishNames.length;i++) newOrder.addDish(dishNames[i], quantities[i]);
		return orderCounter;
	}
	
	/**
	 * retrieves the IDs of the orders that satisfy the given constraints.
	 * Only the  first {@code maxOrders} (according to the orders arrival time) are returned
	 * they must be scheduled to be delivered at {@code deliveryTime} 
	 * whose {@code deliveryDistance} is lower or equal that {@code maxDistance}. 
	 * Once returned by the method the orders must be marked as assigned 
	 * so that they will not be considered if the method is called again. 
	 * The method returns an empty list if there are no orders (not yet assigned) 
	 * that meet the requirements.
	 * 
	 * @param deliveryTime required time of delivery 
	 * @param maxDistance  maximum delivery distance
	 * @param maxOrders    maximum number of orders to retrieve
	 * @return list of order IDs
	 */
	public List<Integer> scheduleDelivery(int deliveryTime, int maxDistance, int maxOrders) {
        List<Order> searchedOrderList= ordersMap.values().stream().filter(order->order.getDeliveryTime()==deliveryTime && order.getDeliveryDistance()<=maxDistance).limit(maxOrders).collect(Collectors.toList());
		searchedOrderList.stream().forEach(order->order.setStatus(Status.ASSIGNED));
		return searchedOrderList.stream().map(Order::getCode).collect(Collectors.toList());
	}
	
	/**
	 * retrieves the number of orders that still need to be assigned
	 * @return the unassigned orders count
	 */
	public int getPendingOrders() {
        return (int) ordersMap.values().stream().filter(order->order.getStatus()==Status.PENDING).count();
	}
	
	// R4
	/**
	 * records a rating (a number between 0 and 5) of a restaurant.
	 * Ratings outside the valid range are discarded.
	 * 
	 * @param restaurantName   name of the restaurant
	 * @param rating           rating
	 */
	public void setRatingForRestaurant(String restaurantName, int rating) {
		if(rating<0 || rating>5) return;
		restaurantsMap.get(restaurantName).addRating(rating);
	}
	
	/**
	 * retrieves the ordered list of restaurant. 
	 * 
	 * The restaurant must be ordered by decreasing average rating. 
	 * The average rating of a restaurant is the sum of all rating divided by the number of ratings.
	 * 
	 * @return ordered list of restaurant names
	 */
	public List<String> restaurantsAverageRating() {
        return restaurantsMap.values().stream().filter(restaurant-> restaurant.getRatingsList().size()>0).sorted(Comparator.comparingDouble(Restaurant::getAvgRating).reversed()).map(Restaurant::getName).collect(Collectors.toList());
	}
	
	//R5
	/**
	 * returns a map associating each category to the number of orders placed to any restaurant in that category. 
	 * Also categories whose restaurants have not received any order must be included in the result.
	 * 
	 * @return map category -> order count
	 */
	public Map<String,Long> ordersPerCategory() {
        return categoriesList.stream().collect(Collectors.toMap(category->category,category-> ordersMap.values().stream().filter(order->order.getRestaurant().getCategory().equals(category)).count()));
	}
	
	/**
	 * retrieves the name of the restaurant that has received the higher average rating.
	 * 
	 * @return restaurant name
	 */
	public String bestRestaurant() {
        return restaurantsMap.values().stream().sorted(Comparator.comparingDouble(Restaurant::getAvgRating).reversed()).map(Restaurant::getName).findFirst().orElse(null);
	}
}
