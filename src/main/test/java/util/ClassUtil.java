package util;

import com.ace.ng.utils.ClassUtils;
import org.junit.Test;

import java.util.List;

/**
 * Created by Administrator on 2015/7/10.
 */
public class ClassUtil {
    @Test
    public void getPackageClasses() throws Exception{
        String basePackage="util";
        List<Class> result=ClassUtils.getClasssFromPackage(basePackage);
        for(Class c:result){
            System.out.println(c.getName());
        }
    }
}
