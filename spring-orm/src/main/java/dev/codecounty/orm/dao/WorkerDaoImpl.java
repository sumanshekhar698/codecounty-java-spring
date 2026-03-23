package dev.codecounty.orm.dao;

import dev.codecounty.orm.entities.Worker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

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
    public void loadTasks(int id) {
        Worker worker = sessionFactory.getCurrentSession().get(Worker.class, id);

        // Triggers lazy collection initialization
        System.out.println(worker.getTasks().size());
    }
}
