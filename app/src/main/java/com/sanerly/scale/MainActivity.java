package com.sanerly.scale;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.sanerly.scale.view.ScaleView;

public class MainActivity extends AppCompatActivity {

    private ScaleView scaleView;
    private ScaleView scaleView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scaleView = findViewById(R.id.scale_view);
        scaleView2 = findViewById(R.id.scale_view2);
        scaleView2.setOnPassChangeListener(new ScaleView.onPassChangeListener() {
            @Override
            public void onPassChange(int position, int progress, int current, int type) {
                Log.e("MainActivity", "position = " + position);
                Log.e("MainActivity", "progress = " + progress);
                Log.e("MainActivity", "current = " + current);
                Log.e("MainActivity", "type = " + type);
                Log.e("MainActivity", "scaleView.getPosition() = " + scaleView2.getPosition());
            }
        });
    }
}
