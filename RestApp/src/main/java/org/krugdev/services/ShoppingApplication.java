package org.krugdev.services;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;


@ApplicationPath("/services")
public class ShoppingApplication extends Application {
	
	private Set<Object> singeltons = new HashSet<>();
	private Set<Class<?>> empty = new HashSet<>();
	
	public ShoppingApplication() {
		singeltons.add(new CustomerResource());
	}
	
	@Override
	public Set<Class<?>> getClasses() {
		return empty;
	}
	
	@Override
	public Set<Object> getSingletons() {
		return singeltons;
	}
}
