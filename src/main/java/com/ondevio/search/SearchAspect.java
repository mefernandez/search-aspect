package com.ondevio.search;

import java.util.Optional;

import javax.transaction.Transactional;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
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
	
	@AfterReturning(pointcut = "execution(* com.ondevio.search.PersonRepository.save*(Person))", returning = "retVal")
	public void log(JoinPoint point, Object retVal) {
	    Person entity = (Person) point.getArgs()[0];
	    Person rp = (Person) retVal;
	    Person p = repository.findOne(entity.getId());
	    String search = PersonSearchStringBuilder.buildSearchString(p);
	    entity.setSearch(search);
	    rp.setSearch(search);
		repository.updateSearch(p.getId(), search);
	}
	


}
