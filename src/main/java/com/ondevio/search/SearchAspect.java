package com.ondevio.search;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Aspect
@Getter
@Setter
public class SearchAspect {
	
	@Autowired
	PersonRepository repository;
	
	boolean updated = false;
	
	@After("execution(* save(..))")
	public void log(JoinPoint point) {
		// FIXME Es para evitar llamadas infinitas. Llamar a un método save() custom para search.
		if (updated) {
			return;
		}
	    updated = true;
	    System.out.println("@After: " + point.getSignature().getName() + " was called..");
	    Person p = (Person) point.getArgs()[0];
	    Address a = p.getAddress();
	    p.setSearch(String.join(" ", p.getName(), a.getStreet(), a.getNumber()));
	    repository.save(p);
	}

}
