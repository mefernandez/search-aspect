package com.ondevio.search;

import org.junit.Test;
import static org.junit.Assert.*;


import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("performance")
@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
public class PerformanceTest {

	@Autowired
	PersonRepository personRepository;
	
	@Autowired
	AddressRepository addressRepository;

	@Autowired
	SearchAspect aspect;
	
	@Test
	//@Repeat(10)
	public void testPerformanceSearchingByAddressCriteria() {
		String query = "Road";
		long before = System.currentTimeMillis();
		Page<Person> page = personRepository.findByNameContainingIgnoreCaseOrAddressStreetContainingIgnoreCaseOrAddressNumberContainingIgnoreCase(query, query, query, new PageRequest(1, 20));
		long after = System.currentTimeMillis();
		System.out.println(String.format("It took %s milliseconds to search with Person JOIN Address and OR criteria", after-before));
		assertEquals(2220, page.getTotalElements());
	}	

	@Test
	//@Repeat(10)
	public void testPerformanceSearchingBySearchField() {
		// It's better to apply toLowerCase() beforehand instead of using "ContainingIgnoreCase".
		String query = "Road".toLowerCase();
		long before = System.currentTimeMillis();
		Page<Person> page = personRepository.findBySearchContaining(query, new PageRequest(1, 10));
		long after = System.currentTimeMillis();
		System.out.println(String.format("It took %s milliseconds to search with Person custom search field", after-before));
		assertEquals(2220, page.getTotalElements());
	}	
}
