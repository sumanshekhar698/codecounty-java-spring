package dev.codecounty.orm.dao;

import dev.codecounty.orm.entities.Task;
import dev.codecounty.orm.entities.Worker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class WorkerDaoImpl implements WorkerDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional(readOnly = true)
    @Override
    public Worker getWorkerById(int id) {
        return sessionFactory.getCurrentSession().get(Worker.class, id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Task> loadTasks(int id) {
        Worker worker = sessionFactory.getCurrentSession().get(Worker.class, id);

        // Triggers lazy collection initialization
        System.out.println(worker.getTasks().size());

        return worker.getTasks();
    }


    @Transactional
    public void addTaskToWorkerOLD(int workerId, Task newTask) {
        Worker worker = sessionFactory.getCurrentSession().get(Worker.class, workerId);

        // This modifies a collection marked as READ_ONLY
        worker.getTasks().add(newTask);
        newTask.setWorker(worker);

        // CRASH: Hibernate cannot update the 'collection cache' for this worker
    }

    @Transactional
    @Override
    public void addTaskToWorker(int workerId, Task newTask) {
        // 1. Fetch worker (puts them in L2 cache)
        Worker worker = sessionFactory.getCurrentSession().get(Worker.class, workerId);

        // 2. TRIGGER THE ERROR: Modify an existing field
        worker.setName("Ryan");//ERROR causing code

        // 3. Optional: add the task
        newTask.setWorker(worker);
        sessionFactory.getCurrentSession().persist(newTask);

        // Force Hibernate to sync with the DB and Cache right now
        sessionFactory.getCurrentSession().flush();
    }
}
