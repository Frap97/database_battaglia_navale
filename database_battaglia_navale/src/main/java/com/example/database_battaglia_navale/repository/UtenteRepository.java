package com.example.database_battaglia_navale.repository;


import com.example.database_battaglia_navale.model.Utente;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UtenteRepository extends CrudRepository<Utente, Integer> {
    Utente findById(long id);
    List<Utente> Username(String username);
    List<Utente> findByemail(String email);
    List<Utente> findAll();
    Utente deleteById(long id);

    Utente findByUsernameAndPassword(String username, String password);


    Utente findByUsername(String username);

    boolean existsByUsername(String newUsername);
}