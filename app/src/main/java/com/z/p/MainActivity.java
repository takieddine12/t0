package com.z.p;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class MainActivity extends AppCompatActivity {
    private BottomSheetBehavior<?> mBottomSheetBehavior;
    private RecyclerView rv;
    private SimpleAdapter simpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv = findViewById(R.id.rv);

        View bottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        setUpRV();
    }

    private void setUpRV() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        gridLayoutManager.setAutoMeasureEnabled(true);
        rv.setLayoutManager(gridLayoutManager);
        rv.setNestedScrollingEnabled(false);
        simpleAdapter = new SimpleAdapter(Extras.getImagesList());
        rv.setAdapter(simpleAdapter);
    }
}