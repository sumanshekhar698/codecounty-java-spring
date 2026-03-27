package dev.codecounty.orm.dao;

import dev.codecounty.orm.entities.Task;
import dev.codecounty.orm.entities.Worker;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface WorkerDao {
    @Transactional(readOnly = true)
    Worker getWorkerById(int id);

    @Transactional(readOnly = true)
    List<Task> loadTasks(int id);

    @Transactional
    void addTaskToWorker(int workerId, Task newTask);
}
