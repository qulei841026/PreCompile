package le.precompile.compiler;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import le.precompile.annotation.BindView;

/**
 * 注解类成员变量解析
 * Created by lei.qu on 2017/1/23.
 */
class BindViewField {

    private int resId;
    private VariableElement variableElement;

    BindViewField(Element element) throws IllegalArgumentException {
        if (ElementKind.FIELD != element.getKind()) {
            throw new IllegalArgumentException(String.format("Only fields can be annotated with @%s",
                    BindView.class.getSimpleName()) + "/" + element.getKind());
        }

        variableElement = (VariableElement) element;
        LeViewBinderProcessor.info("VariableElement : %s .", variableElement);

        BindView bindView = variableElement.getAnnotation(BindView.class);

        resId = bindView.value();
        if (resId < 0) {
            throw new IllegalArgumentException(String.format("value() in %s for field %s is not valid !",
                    BindView.class.getSimpleName(), variableElement.getSimpleName()));
        }
    }

    /**
     * 获取变量名称
     */
    Name getFieldName() {
        return variableElement.getSimpleName();
    }

    /**
     * 获取变量id
     */
    int getResId() {
        return resId;
    }

    /**
     * 获取变量类型
     */
    TypeMirror getFieldType() {
        return variableElement.asType();
    }


}
