package com.ace.ng.utils;

import java.io.File;
import java.io.FileFilter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by Caedmon on 2015/7/10.
 */
public class ClassUtils {
    public static List<Class> getClasssFromPackage(String pack) throws Exception{
        List<Class> clazzs = new ArrayList<Class>();
        // 是否循环搜索子包
        boolean recursive = true;
        // 包名字
        String packageName = pack;
        // 包名对应的路径名称
        String packageDirName = packageName.replace('.', '/');

        Enumeration<URL> dirs=Thread.currentThread().getContextClassLoader().getResources(packageDirName);
        while (dirs.hasMoreElements()) {
            URL url = dirs.nextElement();
            String protocol = url.getProtocol();
            if ("file".equals(protocol)) {
                System.out.println("file类型的扫描");
                String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                findClassInPackageByFile(packageName, filePath, recursive, clazzs);
            } else if ("jar".equals(protocol)) {
                System.out.println("jar类型的扫描");
            }
        }
        return clazzs;
    }
    public static List<Class> findClassesByAnnotation(String packageName,Class<? extends Annotation> ac) throws Exception{
        List<Class> allClasses=getClasssFromPackage(packageName);
        List<Class> result=new ArrayList<>();
        for(Class c:allClasses){
            if(hasAnnotation(c,ac)){
                result.add(c);
            }
        }
        return result;
    }
    /**
     * 在package对应的路径下找到所有的class
     *
     * @param packageName
     *            package名称
     * @param filePath
     *            package对应的路径
     * @param recursive
     *            是否查找子package
     * @param clazzs
     *            找到class以后存放的集合
     */
    public static void findClassInPackageByFile(String packageName, String filePath, final boolean recursive, List<Class> clazzs) {
        File dir = new File(filePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        // 在给定的目录下找到所有的文件，并且进行条件过滤
        File[] dirFiles = dir.listFiles(new FileFilter() {

            @Override
            public boolean accept(File file) {
                boolean acceptDir = recursive && file.isDirectory();// 接受dir目录
                boolean acceptClass = file.getName().endsWith("class");// 接受class文件
                return acceptDir || acceptClass;
            }
        });

        for (File file : dirFiles) {
            if (file.isDirectory()) {
                findClassInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, clazzs);
            } else {
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    clazzs.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + "." + className));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static List<Method> findMethodsByAnnotation(Class c,Class annotationClass){
        Method[] methods=c.getDeclaredMethods();
        List<Method> result=new ArrayList<>();
        for(Method m:methods){
            if(hasAnnotation(m,annotationClass)){
                result.add(m);
            }
        }
        return result;
    }
    public static List<Parameter> findMethodParamsByAnnotation(Method method,Class annotationClass){
        Parameter[] parameters=method.getParameters();
        List<Parameter> result=new ArrayList<>();
        for(Parameter p:parameters){
            if(hasAnnotation(p,annotationClass)){
                result.add(p);
            }
        }
        return result;
    }
    public static Annotation getAnnotation(Class c,Class annotationClass){
        Annotation result=c.getAnnotation(annotationClass);
        return result;
    }
    public static boolean hasAnnotation(Class c,Class annotationClass){
        return getAnnotation(c,annotationClass)!=null;
    }
    public static Annotation getAnnotation(Method m,Class annotationClass){
        Annotation result=m.getAnnotation(annotationClass);
        return result;
    }
    public static boolean hasAnnotation(Method m,Class annotationClass){
        return getAnnotation(m,annotationClass)!=null;
    }
    public static Annotation getAnnotation(Field f,Class annotationClass){
        Annotation result=f.getAnnotation(annotationClass);
        return result;
    }
    public static boolean hasAnnotation(Field f,Class annotationClass){
        return getAnnotation(f,annotationClass)!=null;
    }
    public static Annotation getAnnotation(Parameter p,Class annotationClass){
        Annotation result=p.getAnnotation(annotationClass);
        return result;
    }
    public static boolean hasAnnotation(Parameter p,Class annotationClass){
        return getAnnotation(p,annotationClass)!=null;
    }
}
