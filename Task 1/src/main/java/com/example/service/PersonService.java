package com.example.service;

import com.example.model.Person;
import com.example.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PersonService {
    @Autowired
    private PersonRepository personRepository;

    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    public Optional<Person> getPersonById(Long id) {
        return personRepository.findById(id);
    }

    public Person savePerson(Person person) {
        return personRepository.save(person);
    }

    public void deletePerson(Long id) {
        personRepository.deleteById(id);
    }

    // Методы для поиска по параметрам
    public List<Person> findByLastName(String lastName) {
        return personRepository.findByLastName(lastName);
    }

    public List<Person> findByLastNameIgnoreCase(String lastName) {
        return personRepository.findByLastNameIgnoreCase(lastName);
    }

    public List<Person> findByFirstNameAndLastName(String firstName, String lastName) {
        return personRepository.findByFirstNameAndLastName(firstName, lastName);
    }

    public List<Person> findByEmail(String email) {
        return personRepository.findByEmail(email);
    }

    public List<Person> findByAgeGreaterThan(Integer age) {
        return personRepository.findByAgeGreaterThan(age);
    }

    public List<Person> findByLastNameContaining(String lastName) {
        return personRepository.findByLastNameContaining(lastName);
    }
}
