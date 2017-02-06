package le.precompile.library;

import android.support.annotation.NonNull;
import android.view.View;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 框架功能类
 * Created by le.qu on 2017/1/19.
 */

public final class LeViewBinder {

    private static final Map<String, ViewBinder> binders = new LinkedHashMap<>();//管理保持管理者Map集合

    private LeViewBinder() {

    }

    @SuppressWarnings("TryWithIdenticalCatches")
    public static void bind(@NonNull Object target, @NonNull View source) {
        String className = target.getClass().getName();
        try {
            ViewBinder viewBinder = binders.get(className);
            if (viewBinder == null) {
                Class<?> aClass = Class.forName(className + "$$ViewBinder");
                viewBinder = (ViewBinder) aClass.newInstance();
                binders.put(className, viewBinder);
            }
            if (viewBinder != null) {
                viewBinder.bindView(target, source);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


    }

    public static void unbind(@NonNull Object target) {
        String className = target.getClass().getName();
        ViewBinder viewBinder = binders.get(className);
        if (viewBinder != null) {
            viewBinder.unBindView(target);
        }
        binders.remove(className);
    }


}
