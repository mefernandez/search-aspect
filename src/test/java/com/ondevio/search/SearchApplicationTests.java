package com.ondevio.search;

import org.junit.Test;
import static org.junit.Assert.*;

import javax.transaction.Transactional;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SearchApplicationTests {

	@Autowired
	PersonRepository personRepository;
	
	@Autowired
	AddressRepository addressRepository;

	@Autowired
	SearchAspect aspect;
	
	@Test
	public void contextLoads() {
	}
	
	@Test
	public void savePersonAndAddress() {
		Address a = new Address()
		.setId(1L)
		.setStreet("Baker St.")
		.setNumber("21");
		addressRepository.save(a);
		
		{
		Person p = new Person()
				.setId(1L)
				.setName("John")
				.setAddress(a);
		
		assertNull(p.getSearch());
//		personRepository.saveAndFlush(p);
		personRepository.save(p);
		assertNull(p.getSearch());
		assertEquals("John Baker St. 21", personRepository.findOne(p.getId()).getSearch());

		p.setName("Sherlock");
//		personRepository.saveAndFlush(p);
		personRepository.save(p);
		assertEquals("Sherlock Baker St. 21",  personRepository.findOne(p.getId()).getSearch());
		}
		
		{
		Person p = new Person()
				.setId(2L)
				.setName("Arthur")
				.setAddress(
						new Address()
							.setId(1L)
				);

		assertNull(p.getSearch());
//		personRepository.saveAndFlush(p);
		personRepository.save(p);
		assertNull(p.getSearch());
		assertEquals("Baker St.", addressRepository.findOne(1L).getStreet());
		assertEquals("21", addressRepository.findOne(1L).getNumber());
		assertEquals("Arthur Baker St. 21",  personRepository.findOne(p.getId()).getSearch());
		}
		
	}

}
