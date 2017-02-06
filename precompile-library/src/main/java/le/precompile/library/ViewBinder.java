package le.precompile.library;

import android.view.View;

/**
 * 框架接口
 * Created by lei.qu on 2017/1/20.
 */
public interface ViewBinder {

    void bindView(Object target, View view);

    void unBindView(Object target);
}
