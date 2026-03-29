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



    /*
     *  Hibernate only throws an exception for READ_ONLY if it attempts a direct Update statement on an entity row that is
     *  already known to the session.
     *  Since your code involves a Collection and an Insert of a new Task, Hibernate uses the "Evict and Reload" strategy
     *  instead of throwing a hard error.
     *
     * */
    @Transactional
    @Override
    public void addNewTaskToWorker(int workerId, Task newTask) {
        Worker worker = sessionFactory.getCurrentSession().get(Worker.class, workerId);

        // Explicitly link them
        newTask.setWorker(worker);
        worker.getTasks().add(newTask);

        // While Cascade handles it, calling persisting makes your intent clear
        // to other developers reading the code.
        sessionFactory.getCurrentSession().persist(newTask);
    }

    @Transactional
    @Override
    public void updateWorkerName(int workerId, String newName) {
        // 1. Fetch worker (puts them in L2 cache)
        Worker worker = sessionFactory.getCurrentSession().
                get(Worker.class, workerId);

        // 2. TRIGGER THE ERROR: Modify an existing field that is being cached by Hibernate
        worker.setName(newName);//ERROR causing code

        // 3. Optional: add the task
//        newTask.setWorker(worker);
//        sessionFactory.getCurrentSession().persist(newTask);

        // Force Hibernate to sync with the DB and Cache right now
        sessionFactory.getCurrentSession().flush();
    }
}
