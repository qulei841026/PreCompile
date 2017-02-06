package le.precompile.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * 生成预编译类
 * Created by lei.qu on 2017/1/22.
 */
class BinderSet {

    private static final ClassName BINDER = ClassName.get("le.precompile.library", "ViewBinder");
    private static final ClassName VIEW = ClassName.get("android.view", "View");

    private TypeElement typeElement;

    private List<BindViewField> fields = new ArrayList<>();

    BinderSet(TypeElement typeElement) {
        this.typeElement = typeElement;
    }

    JavaFile brewJava(Elements elements) {
        String packageName = elements.getPackageOf(typeElement).getQualifiedName().toString();
        return JavaFile.builder(packageName, createTypeSpec(typeElement).build()).build();
    }

    void addBindViewField(BindViewField bindViewField) {
        fields.add(bindViewField);
    }

    /**
     * 构建类文件
     */
    private TypeSpec.Builder createTypeSpec(TypeElement typeElement) {
        return TypeSpec.classBuilder(typeElement.getSimpleName() + "$$ViewBinder")
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(BINDER)
                .addMethod(createMethodByBind().build())
                .addMethod(createMethodByUnbind().build());
    }

    /**
     * 构建类方法
     */
    private MethodSpec.Builder createMethodByBind() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("bindView")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(TypeName.OBJECT, "target")
                .addParameter(VIEW, "view");

        for (BindViewField item : fields) {
            builder.addStatement("(($T)target).$N = ($T)(view.findViewById($L))",
                    TypeName.get(typeElement.asType()), item.getFieldName(),
                    ClassName.get(item.getFieldType()), item.getResId());
        }

        return builder;
    }

    /**
     * 构建类方法
     */
    private MethodSpec.Builder createMethodByUnbind() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("unBindView")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.OBJECT, "target")
                .addAnnotation(Override.class);

        for (BindViewField item : fields) {
            builder.addStatement("(($T)target).$N = null",
                    TypeName.get(typeElement.asType()), item.getFieldName());
        }

        return builder;
    }

}
