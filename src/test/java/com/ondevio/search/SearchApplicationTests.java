package com.ondevio.search;

import org.junit.Test;
import static org.junit.Assert.*;


import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@Transactional
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
	public void searchAspectSetsSearchPropertyToEntityPassedByParam() {
		Person p = new Person()
				.setId(1L)
				.setName("John");

		Person retval = personRepository.save(p);
		assertTrue(p != retval);
		assertEquals("John", p.getSearch());
	}
	
	@Test
	public void searchAspectSetsSearchPropertyToReturnedEntity() {
		Person p = new Person()
				.setId(1L)
				.setName("John");

		Person retval = personRepository.save(p);
		assertTrue(p != retval);
		assertEquals("John", retval.getSearch());
	}
	
	@Test
	public void findPersonByAddresUsingSearchField() {
		Address a = new Address()
		.setId(1L)
		.setStreet("Baker St.")
		.setNumber("21");
		addressRepository.save(a);
		
		Person p = new Person()
				.setId(1L)
				.setName("John")
				.setAddress(a);
		
		personRepository.save(p);
		assertEquals("John Baker St. 21", p.getSearch());
		assertEquals(1, personRepository.findBySearchContaining("Baker").size());
	}
	
	@Test
	public void findPersonByNameAfterUpdate() {
		Person p = new Person()
				.setId(1L)
				.setName("John");
		personRepository.save(p);
		
		p.setName("Sherlock");
		personRepository.save(p);
		
		assertEquals("Sherlock", p.getSearch());
		assertEquals(1, personRepository.findBySearchContaining("Sherlock").size());
	}
	
	@Test
	public void searchFieldContainsFullLazyReferencedAddress() {
		Address a = new Address()
		.setId(1L)
		.setStreet("Baker St.")
		.setNumber("21");
		addressRepository.save(a);
		
		Person p = new Person()
				.setId(2L)
				.setName("Arthur")
				.setAddress(
						// A Lazy Reference to Address 1L
						new Address()
							.setId(1L)
				);

		assertNull(p.getSearch());
		personRepository.save(p);
		assertEquals("Arthur Baker St. 21", p.getSearch());
		assertEquals("Baker St.", addressRepository.findOne(1L).getStreet());
		assertEquals("21", addressRepository.findOne(1L).getNumber());
		assertEquals("Arthur Baker St. 21",  personRepository.findOne(p.getId()).getSearch());
		
	}
	
	@Test
	public void searchByNameOrStreetOrNumber() {
		Address a = new Address()
		.setId(1L)
		.setStreet("Baker St.")
		.setNumber("21");
		addressRepository.save(a);
		
		Person p = new Person()
				.setId(2L)
				.setName("Arthur")
				.setAddress(
						// A Lazy Reference to Address 1L
						new Address()
							.setId(1L)
				);

		assertNull(p.getSearch());
		personRepository.save(p);

		{
		String query = "Arthur";
		assertEquals(1, personRepository.findByNameContainingIgnoreCaseOrAddressStreetContainingIgnoreCaseOrAddressNumberContainingIgnoreCase(query, query, query).size());
		}
		{
		String query = "Baker";
		assertEquals(1, personRepository.findByNameContainingIgnoreCaseOrAddressStreetContainingIgnoreCaseOrAddressNumberContainingIgnoreCase(query, query, query).size());
		}
		{
		String query = "21";
		assertEquals(1, personRepository.findByNameContainingIgnoreCaseOrAddressStreetContainingIgnoreCaseOrAddressNumberContainingIgnoreCase(query, query, query).size());
		}
		{
		String query = "William";
		assertEquals(0, personRepository.findByNameContainingIgnoreCaseOrAddressStreetContainingIgnoreCaseOrAddressNumberContainingIgnoreCase(query, query, query).size());
		}
	}	

	@Test
	public void testTransactionCleanedUpEntities() {
		assertEquals(0L, addressRepository.count());
		assertEquals(0L, personRepository.count());
	}
}
