package com.xinlian.wb.api.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import springfox.documentation.annotations.ApiIgnore;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * java注释解析
 *
 * @author Jason
 * @date 2018/5/23 下午1:55
 */
@Component
public class AnnotationParse {
    //解析实例
    public static AnnotationParse annotationParse;
    //源码路径
    public static String SRC_PAth;

    public Logger logger = LoggerFactory.getLogger(AnnotationParse.class);

    static {
        try {
            String aa = ResourceUtils.getURL("classpath:").getPath();

            SRC_PAth = (ResourceUtils.getURL("classpath:").getPath().contains("/build/classes/java/main/") ?
                    CommUtil.splitStr4Temp(ResourceUtils.getURL("classpath:").getPath(), "{}/build/classes/java/main/").get(0) :
                    ResourceUtils.getURL("classpath:").getPath().contains("/build/libs/wb-spring-boot-1.3.jar!/BOOT-INF/classes!/") ?
                            CommUtil.splitStr4Temp(ResourceUtils.getURL("classpath:").getPath(), "{}/build/libs/wb-spring-boot-1.3.jar!/BOOT-INF/classes!/").get(0) :
                            ResourceUtils.getURL("classpath:").getPath()) + "/api_core/";
        } catch (FileNotFoundException e) {
        }
    }

    //解析的package
    public List<String> packages = Lists.newArrayList();
    //需要过滤的Controller
    public List<String> filters = Lists.newArrayList();

    /**
     * 创建扫描解析实例
     *
     * @param packages 需要解析的包
     */
    public static AnnotationParse getInstance(String... packages) {
        if (Objects.isNull(annotationParse)) {
            annotationParse = new AnnotationParse();
            annotationParse.packages = Lists.newArrayList(packages);
            return annotationParse;
        } else {
            annotationParse.packages = Lists.newArrayList(packages);
        }
        return annotationParse;
    }

    /**
     * 创建扫描解析实例
     *
     * @param packages 需要解析的包
     */
    public static AnnotationParse getInstance(List<String> packages) {
        if (Objects.isNull(annotationParse)) {
            annotationParse = new AnnotationParse();
            annotationParse.packages = packages;
            return annotationParse;
        } else {
            annotationParse.packages = packages;
        }
        return annotationParse;
    }

    /**
     * 增加过滤controller，需要controller的名称
     *
     * @param filter controllerName
     * @return
     */
    public AnnotationParse filter(String... filter) {
        //过滤controller，统一转成小写
        if (Objects.isNull(annotationParse)) {
            annotationParse = new AnnotationParse();
            annotationParse.filters = Lists.newArrayList(filter).stream().map(f -> f.toLowerCase().trim()).collect(Collectors.toList());
            return annotationParse;
        } else {
            annotationParse.filters = Lists.newArrayList(filter).stream().map(f -> f.toLowerCase().trim()).collect(Collectors.toList());
            ;
        }
        return annotationParse;
    }

    /**
     * 增加过滤controller，需要controller的名称
     *
     * @param filter controllerName
     * @return
     */
    public AnnotationParse filter(List<String> filter) {
        //过滤controller，统一转成小写
        if (Objects.isNull(annotationParse)) {
            annotationParse = new AnnotationParse();
            annotationParse.filters = filter.stream().map(f -> f.toLowerCase()).collect(Collectors.toList());
            return annotationParse;
        } else {
            annotationParse.filters = filter.stream().map(f -> f.toLowerCase()).collect(Collectors.toList());
            ;
        }
        return annotationParse;
    }

    /**
     * 数据加载与读取
     */
    public ApiClassDoc getApiData() {
        ApiClassDoc apiClassDoc = new ApiClassDoc();
        if (!packages.isEmpty()) {
            baseParseApiData(apiClassDoc, null, null, false);
        }
        return apiClassDoc;
    }

    /**
     * 数据加载与读取
     *
     * @param action controller名称 UserController -> userController
     */
    public ApiClassDoc getApiData(String action) {
        ApiClassDoc apiClassDoc = new ApiClassDoc();
        if (!packages.isEmpty()) {
            baseParseApiData(apiClassDoc, f -> getLowName4EndWithJava(f.getName()).equals(action.toLowerCase()), null, false);
        }
        return apiClassDoc;
    }


    /**
     * 此方法会读取Controller内部的所有方法
     *
     * @return
     */
    public ApiClassDoc getApiData4Depth() {
        ApiClassDoc apiClassDoc = new ApiClassDoc();
        if (!packages.isEmpty()) {
            baseParseApiData(apiClassDoc, null, null, true);
        }
        return apiClassDoc;
    }

    /**
     * 此方法会读取Controller内部的所有方法
     *
     * @param action controller名称 UserController -> userController
     * @return
     */
    public ApiClassDoc getApiData4Depth(String action) {
        ApiClassDoc apiClassDoc = new ApiClassDoc();
        if (!packages.isEmpty()) {
            baseParseApiData(apiClassDoc, f -> getLowName4EndWithJava(f.getName()).equals(action.toLowerCase()), null, true);
        }
        return apiClassDoc.setMethod(action);
    }

    /**
     * 此方法会读取Controller内部的所有方法
     *
     * @param action controller名称 UserController -> userController
     * @param method method方法名称
     * @return
     */
    public ApiClassDoc getApiData4Depth(String action, String method) {
        ApiClassDoc apiClassDoc = new ApiClassDoc();
        if (!packages.isEmpty()) {
            baseParseApiData(apiClassDoc,
                    f -> getLowName4EndWithJava(f.getName()).equals(action.toLowerCase()),
                    m -> m.name().equals(method),
                    true);
        }
        return apiClassDoc.setAction(action).setMethod(method);
    }

    File[] files = null;

    /**
     * 数据解析，过滤
     *
     * @param apiClassDoc
     * @param controllerPred controller过滤条件
     * @param methodPred     method过滤条件
     * @param isDepth        true:内部方法全部提取 false:不做提取
     */
    public void baseParseApiData(ApiClassDoc apiClassDoc, Predicate<File> controllerPred
            , Predicate<MethodDoc> methodPred, boolean isDepth) {
        SRC_PAth = SRC_PAth.replace("file:", "").trim();
        logger.info("ApiDoc目录："+SRC_PAth);
        if (files == null) {
            File[] a = new File(SRC_PAth).listFiles();
            List<File> aa = new ArrayList<>();
            for (int i = 0; i <a .length; i++) {
                if (!a[i].getPath().contains(".DS_Store")){
                    aa.add(a[i]);
                }
            }
            files = new File[aa.size()];
            for (int i = 0; i < aa.size(); i++) {
                files[i] = aa.get(i);
            }
        }

        for (int i = 0; i < files.length; i++) {
            //调用解析方法
            ClassDoc[] data = JavaDocReader.show(files[i].getPath());
            String name = data[0].name();
            apiClassDoc.putClassDoc(name, parseClassDoc(data, methodPred, isDepth));
        }


    }

    public static void writeToLocal(String destination, InputStream input)
            throws IOException {
        int index;
        byte[] bytes = new byte[2048];
        FileOutputStream downloadFile = new FileOutputStream(destination);
        while ((index = input.read(bytes)) != -1) {
            downloadFile.write(bytes, 0, index);
            downloadFile.flush();
        }
        input.close();
        downloadFile.close();

    }


    /**
     * 内部数据注释提取
     *
     * @param data    数据
     * @param isDepth true:内部方法全部提取 false:不做提取
     * @return
     */
    public Dict parseClassDoc(ClassDoc[] data, Predicate<MethodDoc> methodPred, Boolean isDepth) {
        Predicate<MethodDoc> pre = ObjectUtil.isNull(methodPred) ? file -> true : methodPred;
        Dict dict = Dict.create();
        ClassDoc classDoc = data[0];
        dict.set("name", classDoc.name());
        dict.set("commentText", classDoc.commentText());
        dict.putAll(parseTags(classDoc.tags()));
        Map<String, Dict> methodList = Maps.newLinkedHashMap();
        //转换method
        if (classDoc.methods().length > 0) {
            //注解忽略 ApiIgnore
            MethodDoc[] aa = classDoc.methods();
            Stream.of(aa).filter(pre).forEach(f -> {
                try {
                    Class<?> ControllerClass = Class.forName(classDoc.toString());
                    ApiIgnore annotation = null;
                    try {
                        annotation = ControllerClass.getMethod(f.name()).getAnnotation(ApiIgnore.class);
                    } catch (NoSuchMethodException e) {
                    } catch (SecurityException e) {
                    }
                    if (ObjectUtil.isNotNull(annotation)) {
                        return;
                    }
                } catch (Exception e) {
                }
                Dict methodDict = Dict.create();
                methodDict.set("name", f.name());
                methodDict.set("commentText", f.commentText());
                methodDict.putAll(parseTags(f.tags()));
                if (isDepth) {
                    methodList.put(f.name(), methodDict);
                }
            });
        }
        dict.set("methods", methodList);
        return dict;
    }

    /**
     * 解析tages
     *
     * @param tags
     * @return
     */
    public Dict parseTags(Tag[] tags) {
        Dict dict = Dict.create();
        Stream.of(tags).forEach(tag -> {
            String key = CommUtil.splitStr4Temp(tag.name(), "@{}").get(0);
            //验证是否为多个参数，如果为多个转换为数组存储
            if (dict.keySet().contains(key)) {
                if (dict.get(key) instanceof List) {
                    List list = Convert.convert(List.class, dict.get(key));
                    list.add(tag.text());
                    dict.set(key, list);
                } else {
                    dict.set(key, Lists.newArrayList(dict.get(key), tag.text()));
                }
            } else {
                dict.set(key, tag.text());
            }
        });
        return dict;
    }

    public String getLowName4EndWithJava(String f) {
        return StrUtil.removeSuffix(f, ".java").toLowerCase();
    }

    public AnnotationParse() {
    }
}
