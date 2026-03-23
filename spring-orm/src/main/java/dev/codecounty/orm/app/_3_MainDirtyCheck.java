package dev.codecounty.orm.app;

import dev.codecounty.orm.dao.StudentDao;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class _3_MainDirtyCheck {
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("beans_config/orm_config.xml");
		StudentDao studentDao = context.getBean("studentDao", StudentDao.class);
		studentDao.testDirtyChecking();

	}
}