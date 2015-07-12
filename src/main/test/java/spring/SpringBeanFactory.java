package spring;

import common.TestCmdControl;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by Caedmon on 2015/7/12.
 */
public class SpringBeanFactory {
    @Test
    public void getAutoWireBean() throws Exception{
        ClassPathXmlApplicationContext context=new ClassPathXmlApplicationContext("spring/beans.xml");
        TestCmdControl control=context.getBean(TestCmdControl.class);
        System.out.println(control.login(null,null));
    }
}
