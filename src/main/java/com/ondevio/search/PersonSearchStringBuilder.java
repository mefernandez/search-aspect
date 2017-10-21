package com.ondevio.search;

public class PersonSearchStringBuilder {
	
	public static String buildSearchString(Person p) {
		String search = null;
	    Address a = p.getAddress();
	    if (a != null) {
	    	search = String.join(" ", p.getName(), a.getStreet(), a.getNumber());
	    } else {
	    	search = p.getName(); 
	    }
	    if (search != null) {
	    	search = search.toLowerCase();
	    }
    	return search;
	}

}
