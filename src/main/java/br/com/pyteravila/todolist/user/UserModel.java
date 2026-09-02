package br.com.pyteravila.todolist.user;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import lombok.Data;
import java.util.UUID;

@Data
@Entity(
    name = "tb_users"
)
public class UserModel {

    @Id
    @GeneratedValue(
        generator = "UUID"
    )
    private UUID id;
    
    @Column(unique = true, nullable = false)
    private String username;
    private String name;
    private String password; 

    @CreationTimestamp
    private LocalDateTime createdAt;
    
}
