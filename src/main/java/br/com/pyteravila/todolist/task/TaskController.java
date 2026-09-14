package br.com.pyteravila.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.pyteravila.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    
    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity createTask(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        System.out.println("TaskController: Creating a new task with title: ");
        taskModel.setIdUser((java.util.UUID) request.getAttribute("idUser"));

        if(LocalDateTime.now().isAfter(taskModel.getStartAt()) || LocalDateTime.now().isAfter(taskModel.getEndAt())) {
            return ResponseEntity.badRequest().body("Uma tarefa não pode ser criada com uma data de início ou término no passado.");
        }

        if(taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
            return ResponseEntity.badRequest().body("Uma tarefa não pode ser criada com uma data de início após a data de término.");
        }

        return ResponseEntity.ok(taskRepository.save(taskModel));
    }

    @GetMapping("/")
    public List<TaskModel> list(HttpServletRequest request) {
        var idUser = (java.util.UUID) request.getAttribute("idUser");
        System.out.println("TaskController: Listing tasks for user with ID: " + idUser);
        var tasks = this.taskRepository.findByIdUser(idUser);
        return tasks;
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskModel> update (@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id) {
        var idUser = (java.util.UUID) request.getAttribute("idUser");
        System.out.println("TaskController: Updating task with ID: " + id + " for user with ID: " + idUser);
        var task = this.taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));

        if(task == null) {
            return ResponseEntity.notFound().build();
        }

        if(!task.getIdUser().equals(idUser)) {
            return ResponseEntity.status(403).body(null);
        }

        Utils.copyNonNullProperties(taskModel, task);

        return ResponseEntity.ok(this.taskRepository.save(task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(HttpServletRequest request, @PathVariable UUID id) {
        var idUser = (UUID) request.getAttribute("idUser");
        var task = this.taskRepository.findById(id).orElse(null);

        if (task == null) {
            return ResponseEntity.notFound().build();
        }

        if (!task.getIdUser().equals(idUser)) {
            return ResponseEntity.status(403).build();
        }

        this.taskRepository.delete(task);
        return ResponseEntity.noContent().build();
    }
}
