package haru.com.hr.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import haru.com.hr.BaseActivity;
import haru.com.hr.R;
import haru.com.hr.databinding.ActivityWriteBinding;

public class WriteActivity extends BaseActivity<ActivityWriteBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBinding(R.layout.activity_write);


    }

    public void writeAcitivityClickListener(View view) {
        switch (view.getId()) {
            case R.id.imgWriteCancel:
                onBackPressed();
                break;
            case R.id.tvWriteSave:
                //TODO 글저장작업
                break;
        }


    }

}
