package dev.codecounty.orm.app;

import dev.codecounty.orm.dao.WorkerDao;
import dev.codecounty.orm.entities.Task;
import dev.codecounty.orm.entities.Worker;
import dev.codecounty.orm.service.WorkerCsvLoaderServiceOneTime;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;


public class _6_MainL2Cache {

    public static void main(String[] args) {

        ApplicationContext ctx =
                new ClassPathXmlApplicationContext("beans_config/L2_cache_config.xml");


        WorkerDao dao = ctx.getBean(WorkerDao.class);

        System.out.println("---- FIRST CALL (DB HIT) ----");
        Worker workerById = dao.getWorkerById(4);// L2 cache miss --> pull from DB
        System.out.println("Retrieved Worker: " + workerById);

        System.out.println("---- SECOND CALL (L2 CACHE HIT) ----");
        workerById = dao.getWorkerById(4);// L2 cache hit --> DB skip
        System.out.println("Retrieved Worker: " + workerById);

        System.out.println("---- TASKS LOAD ----");
        List<Task> tasks = dao.loadTasks(4);//L2 cache miss --> pull tasks from DB
        System.out.println("Retrieved Tasks: " + tasks);

        System.out.println("---- TASKS LOAD REPEAT----");
        tasks = dao.loadTasks(4);// L2 cache hit --> DB skip
        System.out.println("Retrieved Tasks: " + tasks);


        System.out.println("\n---- ATTEMPTING TO ADD TASK (BRACE FOR IMPACT) ----");
        try {
            Task newTask = new Task();
            newTask.setTaskName("Fix Production Bug");

            // This will trigger the ReadOnlyCacheException
            dao.addTaskToWorker(4, newTask);

            System.out.println("Success? (Unlikely with READ_ONLY)");
        } catch (Exception e) {
            System.err.println("!!! BOOM: DATABASE/CACHE ERROR !!!");
            System.err.println("Reason: " + e.getMessage());
            // This usually wraps a ReadOnlyCacheException
            e.printStackTrace();
        }

    }
}

