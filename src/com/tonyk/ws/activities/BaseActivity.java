package com.tonyk.ws.activities;

import com.tonyk.ws.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by kienbt on 9/1/2015.
 */
public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

//    @Override
//    public void startActivity(Intent intent) {
//        super.startActivity(intent);
//
//        // activity transition animation
//        overridePendingTransition(R.anim.translate_in_right, R.anim.translate_out_left);
//    }
//
//    @Override
//    public void startActivityForResult(Intent intent, int requestCode) {
//        super.startActivityForResult(intent, requestCode);
//
//        // activity transition animation
//        overridePendingTransition(R.anim.translate_in_right, R.anim.translate_out_left);
//    }
//
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.zoom_out);
    }

    // ===========================================================================
    // Functions
    // ===========================================================================

    public void startActivityWithoutAnimation(Intent intent) {
        super.startActivity(intent);
    }

    public void startActivityForResultWithoutAnimation(Intent intent,
                                                       int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }

    public void finishWithoutAnimation() {
        super.finish();
    }
}
