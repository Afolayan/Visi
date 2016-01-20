package com.jcedar.visinaas.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.support.v7.widget.Toolbar;

import com.jcedar.visinaas.R;
import com.jcedar.visinaas.helper.AccountUtils;
import com.jcedar.visinaas.helper.UIUtils;

public class PicViewActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_view);


        imageView = (ImageView) findViewById(R.id.profile_image);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.ic_up);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PicViewActivity.this.finish();
                }
            });
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    toolbar.setTitle(AccountUtils.getUserName(PicViewActivity.this) != null
                            ? AccountUtils.getUserName(PicViewActivity.this) : "Profile Picture");
                }
            });
        }
            imageView.setImageBitmap(UIUtils.getProfilePic(this));
        }


}
