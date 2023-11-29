package com.z.p;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.z.p.models.ImageModel;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.internal.cache.DiskLruCache;

public class MainActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private RecyclerView rv;
    private CoordinatorLayout mainLayout;

    private ArrayList<ImageModel> arrayList;

    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv = findViewById(R.id.rv);
        tabLayout = findViewById(R.id.tabLayout);
        mainLayout = findViewById(R.id.mainLayout);

        ScrollView bottomSheet = findViewById(R.id.bottom_sheet);
        BottomSheetBehavior<?> mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheet.setNestedScrollingEnabled(false);
        BottomSheetBehavior.from(bottomSheet);

        arrayList = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();

        setUpTabs();
        setUpRV();

    }

    private void setUpTabs(){
        tabLayout.addTab(tabLayout.newTab().setText("Tab1"));
        tabLayout.addTab(tabLayout.newTab().setText("Tab2"));
        tabLayout.addTab(tabLayout.newTab().setText("Tab3"));
        tabLayout.addTab(tabLayout.newTab().setText("Tab4"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Toast.makeText(MainActivity.this,"You selected " + tab.getPosition(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
    private void setUpRV() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        gridLayoutManager.setAutoMeasureEnabled(true);
        rv.setLayoutManager(gridLayoutManager);
        //rv.setNestedScrollingEnabled(false);


        getImageFromFB();

    }

    private void getImageFromFB(){
        firebaseDatabase.getReference()
                .child("Images")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                ImageModel imageModel = dataSnapshot.getValue(ImageModel.class);
                                arrayList.add(imageModel);
                            }
                            SimpleAdapter simpleAdapter = new SimpleAdapter(arrayList);
                            rv.setAdapter(simpleAdapter);
                            simpleAdapter.onClick(new SimpleAdapter.ImageListener() {
                                @Override
                                public void onImageSelected(String image) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                URL url = new URL(image);
                                                InputStream inputStream =  url.openStream();
                                                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        mainLayout.setBackground(new BitmapDrawable(getResources(),bitmap));
                                                    }
                                                });
                                            } catch (Exception exception){
                                                Log.d("TAG","Error " + exception.getMessage());
                                            }
                                        }
                                    }).start();
                                }
                            });
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("TAG","Error getting images " + error.getMessage());
                    }
                });
    }
}