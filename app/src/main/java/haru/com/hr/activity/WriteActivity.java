package haru.com.hr.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import haru.com.hr.BaseActivity;
import haru.com.hr.R;
import haru.com.hr.adapter.WriteSpinnerAdapter;
import haru.com.hr.databinding.ActivityWriteBinding;
import haru.com.hr.domain.WriteSpinnerData;
import haru.com.hr.domain.WriteSpinnerDataLoader;

public class WriteActivity extends BaseActivity<ActivityWriteBinding> {

    WriteSpinnerDataLoader writeSpinnerDataLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBinding(R.layout.activity_write);

        spinnerSetting();

    }

    private void spinnerSetting() {
        writeSpinnerDataLoader = new WriteSpinnerDataLoader();
        WriteSpinnerAdapter writeSpinnerAdapter = new WriteSpinnerAdapter(this, writeSpinnerDataLoader.getDatas());

        getBinding().spnWriteEmotion.setAdapter(writeSpinnerAdapter);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}
