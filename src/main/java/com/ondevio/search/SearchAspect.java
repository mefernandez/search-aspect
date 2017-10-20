package com.ondevio.search;

import javax.transaction.Transactional;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import lombok.Getter;
import lombok.Setter;

//@Service
//@Transactional
@Component
@Aspect
@Getter
@Setter
public class SearchAspect {
	
	@Autowired
	PersonRepository repository;
	
	@After("execution(* com.ondevio.search.PersonRepository.save*(..))")
	public void log(JoinPoint point) {
	    System.out.println("@After: " + point.getSignature().getName() + " was called..");
	    Person entity = (Person) point.getArgs()[0];
	    //Person p = (Person) point.getArgs()[0];
	    //p.setSearch(String.join(" ", p.getName(), a.getStreet(), a.getNumber()));
	    //repository.save(p);
	    Person p = repository.findOne(entity.getId());
	    Address a = p.getAddress();
	    repository.updateSearch(p.getId(), String.join(" ", p.getName(), a.getStreet(), a.getNumber()));
	}

}
