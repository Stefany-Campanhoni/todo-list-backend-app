package br.com.stefanycampanhoni.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.stefanycampanhoni.todolist.utils.ProjectUtils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity<?> create(@RequestBody TaskModel taskModel, HttpServletRequest request) {

        taskModel.setId((UUID) request.getAttribute("userId"));

        LocalDateTime currentTime = LocalDateTime.now();

        boolean isEndAtBeforeStartAt = taskModel.getEndAt().isBefore(taskModel.getStartAt());
        boolean isCurrentTimeAfterStartAt = currentTime.isAfter(taskModel.getStartAt());
        boolean isCurrentTimeAfterEndAt = currentTime.isAfter(taskModel.getEndAt());

        if (isEndAtBeforeStartAt || isCurrentTimeAfterStartAt || isCurrentTimeAfterEndAt) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Date!");
        }

        TaskModel task = this.taskRepository.save(taskModel);
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }

    @GetMapping("/")
    public List<TaskModel> listAll(HttpServletRequest request) {
        List<TaskModel> userTasks = taskRepository.findByUserId((UUID) request.getAttribute("userId"));

        return userTasks;
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody TaskModel taskModel,
            HttpServletRequest request) {
        // Variable will be Taskmodel or null
        var taskToUpdate = this.taskRepository.findById(id).orElse(null);

        if (taskToUpdate == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nonexistent task");
        }

        if (!taskToUpdate.getUserId().equals(request.getAttribute("userId"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("User do not have permission to update this task!");
        }

        ProjectUtils.getNonNullProperties(taskModel, taskToUpdate);

        return ResponseEntity.status(HttpStatus.OK).body(this.taskRepository.save(taskToUpdate));
    }
}
