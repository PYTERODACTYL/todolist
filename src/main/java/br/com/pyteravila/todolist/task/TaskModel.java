package br.com.pyteravila.todolist.task;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(
    name = "tb_tasks"
)
public class TaskModel {
    
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    private String description;
    
    @Column(length = 100)
    private String title;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private String priority;
    
    private UUID idUser;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public void setTitle(String title) throws IllegalArgumentException {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }

        if (title.length() > 100) {
            throw new IllegalArgumentException("Title cannot exceed 100 characters");
        }
        
        this.title = title;
    }

}
