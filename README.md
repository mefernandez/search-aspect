# Multifield entity search: proof of concept of a simple db-centric solution

This PoC shows a simple way to search for JPA entities by querying multiple fields to contain a String.
Note this is just a simple, database-centric approach with limited serach capabilities.
For a full-text search please take a look at [Hibernate Search](http://hibernate.org/search/documentation/getting-started/).

This approach simply consists of an extra column in the entity's table that stores a concatenation of the fields to search upon.

Consider this Person - Address model.

![YUML Person Address class diagram](http://yuml.me/b8905acf)

```java
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

@Entity
@Data
@Accessors(chain = true)
public class Address {
	
	@Id
	private Long id;
	
	private String street;
	private String number;

}
```

The goal is to search for Person(s) whos name or Address (street, number) matches a certain String.

# Problem: slow queries

Using Spring Data Repositories, you could write a query method named like this:

```java
public interface PersonRepository extends JpaRepository<Person, Long> {
	
	public Page<Person> findByNameContainingIgnoreCaseOrAddressStreetContainingIgnoreCaseOrAddressNumberContainingIgnoreCase(String name, String street, String number, Pageable pageable);
	
}
```

And use it like this:
```java
String query = "Road";
Page<Person> page = personRepository.findByNameContainingIgnoreCaseOrAddressStreetContainingIgnoreCaseOrAddressNumberContainingIgnoreCase(query, query, query, new PageRequest(1, 20));
```

You may have noticed two strange(r) things:
1. The query method's awkward long name.
2. Passing the same `query` String 3 times. 

These are easy to overcome writing a custom method implementation. 
The real problem is that the resulting SQL query will be slow because:
1. It will apply upper() to each column to ignore case.
2. It will OUTER JOIN person and address tables.

# Solution: one field to bind them all

The solution explored in this PoC is to create a new field named `search` in `Person` entity to store a concatenation of name, street name and street number upon save or update.

The key to keeping `search` field up to date is to use Spring AOP.

```java
@Component
@Aspect
@Getter
@Setter
public class SearchAspect {
	
	@Autowired
	PersonRepository repository;
	
	@AfterReturning(pointcut = "execution(* com.ondevio.search.PersonRepository.save*(Person))", returning = "retVal")
	public void updateSearch(JoinPoint point, Object retVal) {
	    Person entity = (Person) point.getArgs()[0];
	    Person rp = (Person) retVal;
	    Person p = repository.findOne(entity.getId());
	    String search = PersonSearchStringBuilder.buildSearchString(p);
	    entity.setSearch(search);
	    rp.setSearch(search);
		repository.updateSearch(p.getId(), search);
	}
}
```

The method `updateSearch` will fire every time `com.ondevio.search.PersonRepository.save(Person)` method saves (insert or update) a `Person` entity and returns the saved entity. This method will update `search` field using `PersonRepository`.

Concatenation is performed by class `PersonSearchStringBuilder`.

```java
public class PersonSearchStringBuilder {
	
	public static String buildSearchString(Person p) {
		String search = null;
	    Address a = p.getAddress();
	    if (a != null) {
	    	search = String.join(" ", p.getName(), a.getStreet(), a.getNumber());
	    } else {
	    	search = p.getName(); 
	    }
	    if (search != null) {
	    	search = search.toLowerCase();
	    }
    	return search;
	}

}
```

`PersonRepository` looks like this:

```java
public interface PersonRepository extends JpaRepository<Person, Long> {
	
	@Transactional
	@Modifying
	@Query("update Person p set p.search= :search where p.id = :id")
	int updateSearch(@Param("id") Long id, @Param("search") String search);
	
	public Page<Person> findBySearchContaining(String search, Pageable pageable);
}
```

Now you can query entities like this:
```java
String query = "Road".toLowerCase();
Page<Person> page = personRepository.findBySearchContaining(query, new PageRequest(1, 10));
```

Advantages of this approach:
1. Complete freedom to put into `search` whatever data fits best about your aggregate (main entity and related entities).
2. Supports `FetchType.LAZY` relationships.
3. Supports `@Transational`.
4. Entities and search index always in sync.
5. Simple deployment (just an extra table column).

# Performance

There is a `@Test` named `PerformanceTest`. It shows an order of magnitude improvement over default implementation.

# Try it out

This repo only has a couple of `@Test` to prove it works.
Performance is tested against PostgreSQL database, configured in `application-performance.properties`.
You can set PostgreSQL and load test data using Docker and these commands:

```
docker run -d --hostname resolvable -v /var/run/docker.sock:/tmp/docker.sock -v /etc/resolv.conf:/tmp/resolv.conf mgood/resolvable 
docker run --name search -e POSTGRES_PASSWORD=postgres -d postgres:9.6.5-alpine
docker run -it -v $PWD/dump:/dump --rm --link search:postgres postgres:9.6.5-alpine pg_restore -C -d postgres -Fc -h postgres -U postgres /dump/db.dump
```

Happy hacking!
