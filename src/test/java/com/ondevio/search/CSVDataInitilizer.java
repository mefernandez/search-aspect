package com.ondevio.search;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class CSVDataInitilizer {
	
	@Value("classpath:test_data.csv")
	private Resource data; 

	private final PersonRepository personRepository;
	private final AddressRepository addressRepository;

	public void init() throws IOException {

		if (personRepository.count() != 0) {
			return;
		}
		
		LineNumberReader reader = new LineNumberReader(new InputStreamReader(data.getInputStream()));
		String line = reader.readLine();
		// Skip header
		if (line == null) {
			return;
		}
		line = reader.readLine();
		List<Person> persons = new ArrayList<>();
		List<Address> addresses = new ArrayList<>();
		while (line != null) {
			String[] split = line.split(",");
			if (split.length < 4) {
				line = reader.readLine();
				continue;
			}
			Long id = Long.valueOf(split[3]);
			String name = split[0];
			String street = split[2].substring(split[2].indexOf(" "));
			String number = split[2].substring(0, split[2].indexOf(" "));
			Address a = new Address().setId(id).setStreet(street).setNumber(number);
			Person p = new Person().setId(id).setName(name).setAddress(a);
			p.setSearch(PersonSearchStringBuilder.buildSearchString(p));
			persons.add(p);
			addresses.add(a);
			line = reader.readLine();
		}
		addressRepository.save(addresses);
		personRepository.save(persons);
	}
}