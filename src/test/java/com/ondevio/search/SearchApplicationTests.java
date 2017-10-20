package com.ondevio.search;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SearchApplicationTests {

	@Autowired
	PersonRepository repository;
	
	@Autowired
	SearchAspect aspect;


	@Test
	public void contextLoads() {
	}
	
	@Test
	public void savePersonAndAddress() {
		Person p = new Person()
				.setId(1L)
				.setName("John")
				.setAddress(
						new Address()
							.setId(1L)
							.setStreet("Baker St.")
							.setNumber("21")
							
				);
		repository.save(p);
		assertEquals("John Baker St. 21", p.getSearch());
		p.setName("Sherlock");
		aspect.setUpdated(false);
		repository.save(p);
		assertEquals("Sherlock Baker St. 21", p.getSearch());
	}

}
