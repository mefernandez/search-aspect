package com.ondevio.search;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Data
@Accessors(chain = true)
public class Address {
	
	@Id
	private Long id;
	
	private String street;
	private String number;

}
