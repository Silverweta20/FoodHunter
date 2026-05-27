package com.innovacionti.service;

import com.innovacionti.domain.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {
    private final ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();

    public User save(User user) {
        if (user == null || user.getEmail() == null) {
            throw new IllegalArgumentException("Usuario inválido.");
        }
        users.put(user.getEmail().toLowerCase(Locale.ROOT), user);
        return user;
    }

    public Optional<User> findByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(users.get(email.toLowerCase(Locale.ROOT)));
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public boolean isInstitutionalEmail(String email) {
        if (email == null) {
            return false;
        }
        String lower = email.toLowerCase(Locale.ROOT);
        return lower.endsWith(".edu.co") || lower.endsWith(".edu");
    }

    public User authenticate(String email, String password) {
        User user = findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Credenciales inválidas.");
        }
        return user;
    }

    public Optional<User> currentUser(HttpSession session) {
        Object email = session.getAttribute("currentUserEmail");
        if (email == null) {
            return Optional.empty();
        }
        return findByEmail(email.toString());
    }

    public void login(HttpSession session, User user) {
        session.setAttribute("currentUserEmail", user.getEmail());
        session.setAttribute("currentUserAlias", user.getAlias());
        session.setAttribute("currentUserRole", user.getRole());
    }

    public void logout(HttpSession session) {
        session.removeAttribute("currentUserEmail");
        session.removeAttribute("currentUserAlias");
        session.removeAttribute("currentUserRole");
    }
}
