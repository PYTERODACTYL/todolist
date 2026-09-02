package br.com.pyteravila.todolist.task;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ITaskRepository extends JpaRepository<TaskModel, UUID> {
    java.util.List<TaskModel> findByIdUser(UUID idUser);

}
