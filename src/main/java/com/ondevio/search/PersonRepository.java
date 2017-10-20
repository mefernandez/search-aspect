package com.ondevio.search;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface PersonRepository extends JpaRepository<Person, Long> {
	
	@Transactional
	@Modifying
	@Query("update Person p set p.search= :search where p.id = :id")
	int updateSearch(@Param("id") Long id, @Param("search") String search);
	
}
