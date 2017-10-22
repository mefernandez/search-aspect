# Multifield entity search: a proof of concept

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

