package com.z.p;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.CursorWindow;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
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
import com.z.p.room.BitmapDao;
import com.z.p.room.BitmapDatabase;
import com.z.p.room.BitmapModel;
import com.z.p.services.BitmapService;

import org.reactivestreams.Subscription;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Flow;

import io.reactivex.CompletableObserver;
import io.reactivex.FlowableSubscriber;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.internal.cache.DiskLruCache;

public class MainActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private RecyclerView rv;
    private CoordinatorLayout mainLayout;

    private ArrayList<ImageModel> arrayList;

    private FirebaseDatabase firebaseDatabase;

    private BitmapDao bitmapDao;
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
        increaseCursorSize();
        arrayList = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        bitmapDao =  BitmapDatabase.getInstance(this).getBitmapDao();
        setUpTabs();
        setUpRV();
        startService();


    }

    private void increaseCursorSize() {
        try {
            Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
            field.setAccessible(true);
            field.set(null, 100 * 1024 * 1024); //the 100MB is the new size
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        //getImageFromFB();
        getImagesFromDB();

    }

    private void getImagesFromDB(){
        bitmapDao.getBitmapList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<BitmapModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<BitmapModel> bitmapModels) {
                       if(bitmapModels.size() >= 1){
                           SimpleAdapter simpleAdapter = new SimpleAdapter(bitmapModels);
                           rv.setAdapter(simpleAdapter);
                           simpleAdapter.onClick(new SimpleAdapter.ImageListener() {
                               @Override
                               public void onImageSelected(Bitmap bitmap) {
                                   mainLayout.setBackground(new BitmapDrawable(getResources(),bitmap));
                               }
                           });
                       }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("MainActivity TAG","Error throw " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
//    private void getImageFromFB(){
//        firebaseDatabase.getReference()
//                .child("Images")
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if(snapshot.exists()){
//                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
//                                ImageModel imageModel = dataSnapshot.getValue(ImageModel.class);
//                                arrayList.add(imageModel);
//                            }
//                            SimpleAdapter simpleAdapter = new SimpleAdapter(arrayList);
//                            rv.setAdapter(simpleAdapter);
//                            simpleAdapter.onClick(new SimpleAdapter.ImageListener() {
//                                @Override
//                                public void onImageSelected(String image) {
//
//                                }
//                            });
//
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Log.d("TAG","Error getting images " + error.getMessage());
//                    }
//                });
//    }
    private void startService(){
        Intent intent = new Intent(this, BitmapService.class);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

}