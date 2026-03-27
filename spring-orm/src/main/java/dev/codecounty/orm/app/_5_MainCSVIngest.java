package dev.codecounty.orm.app;

import dev.codecounty.orm.service.WorkerCsvLoaderServiceOneTime;
import dev.codecounty.orm.service.WorkerCsvLoaderServiceSafe;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class _5_MainCSVIngest {

    public static void main(String[] args) {

        ApplicationContext ctx =
                new ClassPathXmlApplicationContext("beans_config/L2_cache_config.xml");

        WorkerCsvLoaderServiceOneTime csvLoaderService = ctx.getBean(WorkerCsvLoaderServiceOneTime.class);
        csvLoaderService.loadDataFromCsv();

//        WorkerCsvLoaderServiceSafe csvLoaderSafeService = ctx.getBean(WorkerCsvLoaderServiceSafe.class);
//        csvLoaderSafeService.loadDataFromCsv();



    }
}

