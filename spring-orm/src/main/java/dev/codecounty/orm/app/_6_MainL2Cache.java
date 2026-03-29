package dev.codecounty.orm.app;

import dev.codecounty.orm.dao.WorkerDao;
import dev.codecounty.orm.entities.Task;
import dev.codecounty.orm.entities.Worker;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;


public class _6_MainL2Cache {

    public static void main(String[] args) {

        ApplicationContext ctx =
                new ClassPathXmlApplicationContext("beans_config/L2_cache_config.xml");


        WorkerDao dao = ctx.getBean(WorkerDao.class);
        int workerId = 18;

        System.out.println("---- FIRST CALL (DB HIT) ----");
        Worker workerById = dao.getWorkerById(workerId);// L2 cache miss --> pull from DB
        System.out.println("Retrieved Worker: " + workerById);

        System.out.println("---- SECOND CALL (L2 CACHE HIT) ----");
        workerById = dao.getWorkerById(4);// L2 cache hit --> DB skip
        System.out.println("Retrieved Worker: " + workerById);

        System.out.println("---- TASKS LOAD ----");
        List<Task> tasks = dao.loadTasks(workerId);//L2 cache miss --> pull tasks from DB
        System.out.println("Retrieved Tasks: " + tasks);

        System.out.println("---- TASKS LOAD REPEAT----");
        tasks = dao.loadTasks(workerId);// L2 cache hit --> DB skip
        System.out.println("Retrieved Tasks: " + tasks);

        System.out.println("---- ADDING A NEW TASK TO EXISTING WORKER ----");
        System.out.println("Before Tasks :: "+dao.loadTasks(workerId).size());
        Task newTask = new Task();
        newTask.setTaskName("Fix STAGE Bug");
        // This will work fine
        dao.addNewTaskToWorker(workerId, newTask);
        System.out.println("After Tasks :: "+dao.loadTasks(workerId).size());


        System.out.println("\n---- ATTEMPTING TO CHANGE NAME OF ENTITY (BRACE FOR IMPACT) ----");
        try {
            // This will trigger the ReadOnlyCacheException
            dao.updateWorkerName(workerId, "Merlin");
            System.out.println("Success? (Unlikely with READ_ONLY)");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

