package scan;

import com.ace.ng.annotation.CmdControl;
import com.ace.ng.annotation.CmdMethod;
import com.ace.ng.utils.ClassUtils;
import org.junit.Assert;
import org.junit.Test;
import util.ClassUtil;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Administrator on 2015/7/10.
 */
public class AnnotationTest {
    @Test
    public void scanAnnotationClasses() throws Exception{
        String basePackage="scan";
        List<Class> result= ClassUtils.findClassesByAnnotation(basePackage, CmdControl.class);
        for(Class c:result){
            System.out.println(c.getName());
            Assert.assertEquals(true, ClassUtils.hasAnnotation(c, CmdControl.class));
        }
    }
    @Test
    public void scanAnnotationMethod() throws Exception{
        String basePackage="scan";
        List<Class> classes= ClassUtils.findClassesByAnnotation(basePackage, CmdControl.class);
        for(Class c:classes){
            System.out.println(c.getName());
            Assert.assertEquals(true, ClassUtils.hasAnnotation(c, CmdControl.class));
            List<Method> methods=ClassUtils.findMethodsByAnnotation(c,CmdMethod.class);
            for(Method m:methods){
                System.out.println(m.getName());
                Assert.assertEquals(true, ClassUtils.hasAnnotation(m, CmdMethod.class));
            }
            System.out.println("----------------");
        }
    }
}
