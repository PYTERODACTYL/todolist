package br.com.pyteravila.todolist.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import at.favre.lib.crypto.bcrypt.BCrypt;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserRepository userRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody UserModel user) {
        var existingUser = this.userRepository.findByUsername(user.getUsername());
        
        if (existingUser != null) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        user.setPassword(BCrypt.withDefaults().hashToString(12, user.getPassword().toCharArray()));

        var userCreated = this.userRepository.save(user);
        return ResponseEntity.ok().body(userCreated);
    }
}
