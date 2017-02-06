package le.precompile.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import le.precompile.annotation.BindView;

/**
 * 预编译逻辑处理类
 * RoundEnvironment -> All Class
 * TypeElement -> Class
 * Element -> Class Member Variable
 * Created by lei.qu on 2017/1/19.
 */
@AutoService(Processor.class)  //框架配置预编译文件
@SuppressWarnings("WeakerAccess")
public class LeViewBinderProcessor extends AbstractProcessor {

    private Filer filer;
    private Elements elements;
    private static Messager messager;

    private void error(String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args));
    }

    static void info(String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.NOTE, String.format(msg, args));
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        filer = processingEnv.getFiler();
        elements = processingEnv.getElementUtils();
        messager = processingEnv.getMessager();

        info("LeViewBinderProcessor init", "");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        info("RoundEnvironment : %s .", roundEnv);
        Map<TypeElement, BinderSet> map = findAndParseTargets(roundEnv);

        for (Map.Entry<TypeElement, BinderSet> entry : map.entrySet()) {
            TypeElement typeElement = entry.getKey();
            BinderSet binderSet = entry.getValue();
            JavaFile javaFile = binderSet.brewJava(elements);
            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                error("Unable to write binding for type %s: %s", typeElement, e.getMessage());
            }
        }
        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {
            types.add(annotation.getCanonicalName());
        }
        info("LeViewBinderProcessor getSupportedAnnotationTypes", "");
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        info("LeViewBinderProcessor getSupportedSourceVersion", "");
        return SourceVersion.latestSupported();
    }


    private Set<Class<? extends Annotation>> getSupportedAnnotations() {
        Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();
        annotations.add(BindView.class);
        return annotations;
    }


    private Map<TypeElement, BinderSet> findAndParseTargets(RoundEnvironment env) {
        Map<TypeElement, BinderSet> map = new LinkedHashMap<>();
        for (Element element : env.getElementsAnnotatedWith(BindView.class)) {
            TypeElement typeElement = (TypeElement) element.getEnclosingElement();

            info("Element : %s , TypeElement : %s .", element, typeElement);

            try {
                BindViewField bindViewField = new BindViewField(element);
                BinderSet binderSet;
                if (!map.containsKey(typeElement)) {
                    binderSet = new BinderSet(typeElement);
                    map.put(typeElement, binderSet);
                } else {
                    binderSet = map.get(typeElement);
                }
                binderSet.addBindViewField(bindViewField);
            } catch (Exception e) {
                error(e.getMessage());
            }
        }
        return map;
    }


}
