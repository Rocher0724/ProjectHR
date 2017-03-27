package haru.com.hr;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;

/**
 *  데이터 바인딩 사용시
 */

public abstract class BaseActivity<T extends ViewDataBinding> extends AppCompatActivity {

    private T viewBinding;

    public void setBinding (@LayoutRes int layoutResID) {
        if( viewBinding == null) {
            viewBinding = DataBindingUtil.setContentView(this, layoutResID);
        }
    }

    public T getBinding () {
        return viewBinding;
    }
}
