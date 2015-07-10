package javassit;

import com.ace.ng.annotation.CmdControl;
import com.ace.ng.codec.BinaryCodecApi;
import com.ace.ng.codec.CustomBuf;
import com.ace.ng.dispatch.javassit.HandlerPropertySetter;
import com.ace.ng.dispatch.javassit.NoOpHandlerPropertySetter;
import com.ace.ng.utils.ClassUtils;
import com.ace.ng.utils.CommonUtils;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/7/10.
 */
public class ProxyCreater {
    private static Map<Integer,HandlerPropertySetter> handlerPropertySetterMap=new HashMap<>(100);
    private static ClassPool classPool=ClassPool.getDefault();
    private static Map<String,BinaryCodecApi.ProtoCacheElement> protoElementCache =new HashMap<>();
    private static final Object lock=new Object();
    @Test
    public void createProxyControl() throws Exception{
        String basePackage="scan";
        List<Class> result= ClassUtils.findClassesByAnnotation(basePackage, CmdControl.class);
        for(Class c:result){
            System.out.println(c.getName());
            Assert.assertEquals(true, ClassUtils.hasAnnotation(c, CmdControl.class));
        }
        //构造HandlerPropertySetter
        HandlerPropertySetter handlerPropertySetter=handlerPropertySetterMap.get(cmd);
        if(handlerPropertySetter==null){
            String handlerClassName=handler.getClass().getName();
            CtClass tempHandler=classPool.get(handlerClassName);
            String handlerProxyName=NoOpHandlerPropertySetter.class.getName()+"Proxy$"+cmd;
            CtClass handlerPropertySetterClass=classPool.getOrNull(handlerProxyName);
            if(handlerPropertySetterClass==null){
                handlerPropertySetterClass=classPool.getAndRename(NoOpHandlerPropertySetter.class.getName(),
                        handlerProxyName);
            }
            synchronized (lock){
                //双重检查
                if((handlerPropertySetter=handlerPropertySetterMap.get(cmd))==null){
                    CtMethod setPropertiesMethod=handlerPropertySetterClass.
                            getDeclaredMethod("setHandlerProperties");
                    if(CommonUtils.hasDeclaredMethod(handler.getClass(), "decode", CustomBuf.class)){//如果自己实现了decode方法,则不采用自动set方式
                        setPropertiesMethod.insertAfter("$2.decode($1);");
                    }else{//自动调用set方法解码
                        addAutoDecodeSrc(setPropertiesMethod,tempHandler,handlerClassName);
                    }
                    Class clazz=handlerPropertySetterClass.toClass();
                    handlerPropertySetter=(HandlerPropertySetter)clazz.newInstance();
                    handlerPropertySetterMap.put(cmd,handlerPropertySetter);
                }
            }
        }
        return handlerPropertySetter;
    }
    }
}
