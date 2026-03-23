package dev.codecounty.orm.app;

import dev.codecounty.orm.service.StudentConcurrencyService;
import dev.codecounty.orm.service.StudentLifecycleService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class _4_MainHibernateLifecycle {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("beans_config/orm_config.xml");
        StudentLifecycleService service = context.getBean(StudentLifecycleService.class);


//		1: Create and Persist (Moves to Detached at the end)
//		Student student = service.demonstrateStudentLifecycle();

//		2: ReAttach and Update
//		service.reattachStudent(student);
//		System.out.println("Final State: " + student);

//		3: Simulate Stale Object
        StudentConcurrencyService service2 = context.getBean(StudentConcurrencyService.class);
        service2.simulateStaleStudentUpdate();

    }
}