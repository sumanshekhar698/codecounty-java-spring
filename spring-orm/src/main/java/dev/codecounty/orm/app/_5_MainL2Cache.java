package dev.codecounty.orm.app;

import dev.codecounty.orm.dao.WorkerDao;
import dev.codecounty.orm.service.WorkerCsvLoaderService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class _5_MainL2Cache {

    public static void main(String[] args) {

        ApplicationContext ctx =
                new ClassPathXmlApplicationContext("beans_config/L2_cache_config.xml");

        
      WorkerCsvLoaderService csvLoaderService = ctx.getBean(WorkerCsvLoaderService.class);
      csvLoaderService.loadDataFromCsv();

        WorkerDao dao = ctx.getBean(WorkerDao.class);
        System.out.println("---- FIRST CALL (DB HIT) ----");
        dao.getWorkerById(4);// L2 cache miss --> pull from DB
        System.out.println("---- SECOND CALL (L2 CACHE HIT) ----");
        dao.getWorkerById(4);// L2 cache hit
        System.out.println("---- COLLECTION LOAD ----");
        dao.loadTasks(4);//L2 cache miss --> pull tasks from DB
        System.out.println("---- COLLECTION LOAD REPEAT----");
        dao.loadTasks(4);// L2 cache hit
        
//        dao.loadTasks(4);// L2 cache hit
//        dao.loadTasks(4);// L2 cache hit
    }
}

