package com.example.database_battaglia_navale.controller;
import com.example.database_battaglia_navale.model.UsernameRequest;
import com.example.database_battaglia_navale.repository.UtenteRepository;
import com.example.database_battaglia_navale.model.Utente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class UtenteController {
    @Autowired
    UtenteRepository utenteRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/getUtenti")
    public List<Utente> getUtenti(){

        return utenteRepository.findAll();
    }

    @GetMapping("/getUtenteById")
    public Utente getUtenteById(
            @RequestParam(value = "id")
            int id)
    {
        return utenteRepository.findById(id);
    }

   @DeleteMapping("/deleteById")
  public String deleteById(@RequestParam(value = "id")
                                      int id){
       utenteRepository.deleteById(id);
       return "Utente eliminato correttamente";
    }

    @PostMapping("/deleteByUsername")
    public ResponseEntity<Map<String, Object>> deleteByUsername(@RequestBody String username) {
        Utente u = utenteRepository.findByUsername(username);
        if (u != null) {
            long userId = u.getId(); // ottieni l'id dell'utente
            // elimina l'utente dal database usando l'id
            utenteRepository.deleteById(userId);
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("success", true);
            body.put("message", "Utente eliminato correttamente");
            return new ResponseEntity<>(body, HttpStatus.OK);
        } else {

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("success", false);
            body.put("message", "Utente non trovato");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/registrazione")
    public ResponseEntity<Map<String, Object>> registrazione(@RequestBody Utente nuovoUtente) {
        Utente utenteEsistente = utenteRepository.findByUsername(nuovoUtente.getUsername());

        if (utenteEsistente != null) {
            // L'utente esiste già, restituisci un messaggio di errore
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("success", false);
            body.put("message", "Nome utente già utilizzato");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        } else {
            // Crea il nuovo utente e salvalo nel database
            String passwordCriptata = passwordEncoder.encode(nuovoUtente.getPassword());
            nuovoUtente.setPassword(passwordCriptata);
            utenteRepository.save(nuovoUtente);

            // Restituisci un messaggio di successo
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("success", true);
            body.put("message", "Registrazione avvenuta con successo");
            return new ResponseEntity<>(body, HttpStatus.OK);
        }
    }
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Utente utente) {
        Utente u = utenteRepository.findByUsernameAndPassword(utente.getUsername(), utente.getPassword());
        if (u == null) {
            // L'utente o password sbagliati
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("success", false);
            body.put("message", "Nome utente o password errati");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        } else {
            // L'utente è stato trovato, esegui le azioni di login
            // ad esempio, impostare l'utente nella sessione o creare un token JWT

            // Creare il token JWT
            String token = Jwts.builder()
                    .setSubject(u.getUsername())
                    .signWith(SignatureAlgorithm.HS512, "ChiaveSegretaPerIlTokenJWT")
                    .compact();

            // Aggiungere le intestazioni alla risposta
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            // Restituire la risposta con il token come parte delle intestazioni
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("success", true);
            body.put("message", "Login avvenuto con successo");
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(body);
        }
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        // Elimina la sessione dell'utente
        request.getSession().invalidate();
        // Reindirizza l'utente alla home page o alla pagina di login
        return ResponseEntity.ok().build();
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/utenti/{id}/updateUsername")
    public ResponseEntity<Map<String, Object>> updateUsername(@PathVariable(value = "id") long id, @RequestBody Map<String, String> body) {
        String newUsername = body.get("newUsername");
        Optional<Utente> utenteOptional = Optional.ofNullable(utenteRepository.findById(id));
        if (!utenteOptional.isPresent()) {
            // L'utente con questo id non esiste, restituisci un messaggio di errore
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Utente non trovato");
            return ResponseEntity.badRequest().body(response);
        } else {
            Utente utente = utenteOptional.get();
            utente.setUsername(newUsername);
            utenteRepository.save(utente);
            // Restituisci un messaggio di successo
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Username aggiornato con successo");
            return ResponseEntity.ok().body(response);
        }
    }
    @CrossOrigin(origins = "http://localhost:3000")
    @PutMapping("/users/{username}/username")
    public ResponseEntity<Map<String, Object>> updateUsername(@PathVariable String username, @RequestBody UsernameRequest request) {
        String newUsername = request.getNewUsername();

        // cerca l'utente nel database tramite l'username
        Utente utente = utenteRepository.findByUsername(username);

        if (utente != null) {
            // controlla se il nuovo username è già in uso
            if (utenteRepository.existsByUsername(newUsername)) {
                // restituisci un messaggio di errore
                Map<String, Object> body = new LinkedHashMap<>();
                body.put("success", false);
                body.put("message", "Nome utente già utilizzato");
                return new ResponseEntity<>(body, HttpStatus.CONFLICT);
            }

            // modifica l'username dell'utente trovato
            utente.setUsername(newUsername);
            utenteRepository.save(utente);

            // restituisci un messaggio di successo
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("success", true);
            body.put("message", "Username aggiornato con successo");
            return new ResponseEntity<>(body, HttpStatus.OK);
        } else {
            // l'utente non esiste nel database, restituisci un messaggio di errore
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("success", false);
            body.put("message", "L'utente con questo username non esiste");
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        }
    }

}