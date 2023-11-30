package com.z.p.services;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.z.p.MainActivity;
import com.z.p.R;
import com.z.p.SimpleAdapter;
import com.z.p.models.ImageModel;
import com.z.p.room.BitmapDao;
import com.z.p.room.BitmapDatabase;
import com.z.p.room.BitmapModel;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class BitmapService extends Service {
    private BitmapDao bitmapDao;
    private BitmapDatabase bitmapDatabase;
    private FirebaseDatabase firebaseDatabase;
    private ArrayList<ImageModel> arrayList;
    private int count;


    @Override
    public void onCreate() {
        super.onCreate();
        bitmapDatabase = BitmapDatabase.getInstance(this);
        bitmapDao = bitmapDatabase.getBitmapDao();
        firebaseDatabase = FirebaseDatabase.getInstance();
        arrayList = new ArrayList<>();
        showNotification();
        if(isConnected()){
            deleteTable();
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getImageFromFB();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @SuppressLint("MissingPermission")
    private void showNotification(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(
                    "id","name", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder compat = new NotificationCompat.Builder(this,"id")
                .setContentTitle("Running")
                .setContentText("Please wait..storing images locally")
                .setSmallIcon(R.drawable.baseline_circle_notifications_24);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            startForeground(1,compat.build());
        } else {
            NotificationManagerCompat.from(this).notify(1,compat.build());
        }


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
                            for (int a = 0; a < arrayList.size(); a++) {
                                downloadUrl(arrayList.get(a).getImage());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("TAG","Error getting images " + error.getMessage());
                    }
                });
    }
    private void storeImageOffline(Bitmap bitmap){
        String fileName = System.currentTimeMillis() + ".jpeg";

        // deleteTable();
        bitmapDao.insertBitmap(new BitmapModel(fileName,bitmap))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        count++;
                        if(count >= arrayList.size()){
                            stopSelf();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("TAG","STORAGE ERROR " + e.getMessage());
                    }
                });
    }
    private void downloadUrl(String image){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(image);
                    InputStream inputStream =  url.openStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            storeImageOffline(bitmap);
                        }
                    });
                } catch (Exception error){
                    Log.d("TAG","downloadUrl " + error.getMessage());
                }
            }
        }).start();
    }
    private void deleteTable(){
        bitmapDao.deleteRecords()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });

    }

    private boolean isConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        return  (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);
    }
}
