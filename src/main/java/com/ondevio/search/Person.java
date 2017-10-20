package com.ondevio.search;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Data
@Accessors(chain = true)
public class Person {
	
	@Id
	private Long id;
	
	private String name;
	
	@OneToOne(fetch = FetchType.LAZY)
	private Address address;
	
	private String search;

}
