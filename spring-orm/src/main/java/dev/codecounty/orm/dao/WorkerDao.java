package dev.codecounty.orm.dao;

import dev.codecounty.orm.entities.Worker;
import org.springframework.transaction.annotation.Transactional;

public interface WorkerDao {
    @Transactional(readOnly = true)
    Worker getWorkerById(int id);

    @Transactional(readOnly = true)
    void loadTasks(int id);
}
