package com.example.database_battaglia_navale.controller;
import com.example.database_battaglia_navale.repository.UtenteRepository;
import com.example.database_battaglia_navale.model.Utente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequestMapping("/")
public class ViewController {
     @Autowired
    UtenteRepository utenteRepository;
    @GetMapping("/")
    public String getHomePage() {
        return  "redirect:/index.html";
    }
    /*
    @PostMapping("/login")
    public String loginUser(@RequestParam("username") String username, @RequestParam("password") String password, HttpSession session) {
        // Controlla se l'utente esiste nel database
        Utente user = utenteRepository.findByUsernameAndPassword(username, password);

        if (user != null) {
            // Memorizza le informazioni di autenticazione dell'utente nella sessione


            // Reindirizza l'utente alla pagina del gioco
            return "redirect:/menu.html";
        } else {
            // Restituisci un errore o reindirizza l'utente alla pagina di login con un messaggio di errore
            return "redirect:/login?error=1";
        }
    }

*/


}
