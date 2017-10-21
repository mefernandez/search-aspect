package com.ondevio.search;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@ActiveProfiles("performance")
@RunWith(SpringRunner.class)
//@DataJpaTest(showSql = false)
//@Transactional
@SpringBootTest
public class CSVDataInitilizerTest {

    @Autowired
    private PersonRepository personRepository;
    
    @Autowired
    private AddressRepository addressRepository;
    
    @Autowired
    ResourceLoader resourceLoader;

    @Test//(timeout = 60*1000)
    public void loadTestData() throws IOException {
    	CSVDataInitilizer initilizer = new CSVDataInitilizer(personRepository, addressRepository);
    	Resource resource = resourceLoader.getResource("classpath:test_data.csv");
		initilizer.setData(resource);
    	initilizer.init();
    	assertEquals(100000L, personRepository.count());
    	assertEquals(100000L, addressRepository.count());
    }

}