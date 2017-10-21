package com.ondevio.search;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PersonRepository extends JpaRepository<Person, Long> {
	
	@Transactional
	@Modifying
	@Query("update Person p set p.search= :search where p.id = :id")
	int updateSearch(@Param("id") Long id, @Param("search") String search);
	
	public Page<Person> findBySearchContaining(String search, Pageable pageable);
	public List<Person> findBySearchContaining(String search);
	

	public Page<Person> findByNameContainingIgnoreCaseOrAddressStreetContainingIgnoreCaseOrAddressNumberContainingIgnoreCase(String name, String street, String number, Pageable pageable);
	public List<Person> findByNameContainingIgnoreCaseOrAddressStreetContainingIgnoreCaseOrAddressNumberContainingIgnoreCase(String name, String street, String number);
	
}
