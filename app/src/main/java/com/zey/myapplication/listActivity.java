package com.zey.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class listActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Sarki> MusicList = new ArrayList<>();
    TextView ust_yazi;

    Context context;
    MyAdapter myadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        recyclerView = findViewById(R.id.recycler_view);
        ust_yazi= findViewById(R.id.MusicText);

        context=this;

        if(!checkPermission()){
            requestPermission();
            return;
        }

        String selection = MediaStore.Audio.Media.IS_MUSIC +" != 0";

        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.SIZE
        };


        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,selection,null,null);

        while(cursor.moveToNext()){
            System.out.println("count:"+cursor.getString(5));
            File file =new File(cursor.getString(2));
            Uri uri = getArtUriFromMusicFile(context,file);

            Sarki songData = new Sarki(cursor.getString(0),cursor.getString(2),cursor.getString(1),cursor.getString(3),cursor.getString(4),uri.toString(),convertBtoMB(cursor.getString(5)));

            if(new File(songData.getPath()).exists())   //silindiğinde anlamak için
                MusicList.add(songData);
        }
//        int i=0;
//        for(i=0;i<MusicList.size();i++){
//            System.out.println(String.valueOf(i)+":::::"+MusicList.get(i).getSize());
//        }

        Collections.sort(MusicList, Sarki.nameComparator);  //şarkılar alfabetik sıralansın

        myadapter=new MyAdapter(getApplicationContext(),MusicList);

        ust_yazi.setText(MusicList.size()+" adet şarkı listelenmektedir");

        if(MusicList.size()==0){
            Toast.makeText(listActivity.this, "Hiç şarkı bulunamadı", Toast.LENGTH_LONG).show();
            System.out.println("şaarkı yok");
        }else{
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            recyclerView.setAdapter(myadapter);
        }

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context,DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);




    } //onCreate sonuı

    //medianın yolundan album artı getiren fonksiyon
    public static Uri getArtUriFromMusicFile(Context context, File file) {
        final Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        final String[] cursor_cols = { MediaStore.Audio.Media.ALBUM_ID };

        final String where = MediaStore.Audio.Media.IS_MUSIC + "=1 AND " + MediaStore.Audio.Media.DATA + " = '"
                + file.getAbsolutePath() + "'";
        final Cursor cursor = context.getApplicationContext().getContentResolver().query(uri, cursor_cols, where, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            Long albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

            Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);
            cursor.close();
            return albumArtUri;
        }
        return Uri.EMPTY;
    }

    ItemTouchHelper.SimpleCallback simpleCallback   = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT ) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int position =viewHolder.getAdapterPosition();
            switch (direction){
                case ItemTouchHelper.LEFT:
                    File fdelete = new File(MusicList.get(position).getPath());
                    if (fdelete.exists()) {
                        if (fdelete.delete()) {
                            System.out.println("Dosya silindi:" + MusicList.get(position).getPath());
                        } else {
                            System.out.println("Dosya silinemedi:" + fdelete.getAbsolutePath());
                        }
                    }else
                        System.out.println("Dosya bulunamadı");
                    MusicList.remove(position);
                    recyclerView.setAdapter( myadapter);
                    ust_yazi.setText(MusicList.size()+" adet şarkı listelenmektedir");

                    break;

            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            final int position =viewHolder.getAdapterPosition();
            new RecyclerViewSwipeDecorator.Builder(c,recyclerView,viewHolder,dX,dY,actionState,isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(context,R.color.red))
                    .addSwipeLeftLabel("SİL")
                    .create()
                    .decorate();


            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    public String convertBtoMB(String boyutS){
        double boyut = Double.parseDouble(boyutS);
        double size_kb = boyut /1024;
        double size_mb = size_kb / 1024;

        return String.format("%.2fMB",size_mb);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(recyclerView!=null){
            recyclerView.setAdapter(new MyAdapter(getApplicationContext(),MusicList));
        }
    }

    boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(listActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    void requestPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(listActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
            Toast.makeText(listActivity.this,"Bu izni vermeliydiniz",Toast.LENGTH_SHORT).show();
        }else
            ActivityCompat.requestPermissions(listActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},123);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                break;
            case R.id.hakkinda:
                Toast.makeText(this, "Bu uygulama Zeynep Çolak tarafından Mayıs 20222'de yazılmıştır.", Toast.LENGTH_LONG).show();
                break;
            case R.id.yeni_kayit:
                Intent i2 = new Intent(this, signupActivity.class);
                startActivity(i2);
                break;
            case R.id.sortby:
                Collections.sort(MusicList, Sarki.artistComparator);
                recyclerView.setAdapter( myadapter);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}