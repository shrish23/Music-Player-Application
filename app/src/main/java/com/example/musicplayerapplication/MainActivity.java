package com.example.musicplayerapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    String[] items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listViewSong);
        runtimePermission();

    }

    public void runtimePermission(){
        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        displaySongs();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<File> findSong(File file){
        ArrayList<File> arraylist = new ArrayList<>();

        File f = file;

        File[] files = null;

        try {
            files = f.listFiles();


            if (files.length > 0) {
                for (File singleFile : files) {

                    if (singleFile.isDirectory() && !singleFile.isHidden()) {
                        arraylist.addAll(findSong(singleFile));
                    } else {
                        if (singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav")) {

                            arraylist.add(singleFile);
                        }
                    }
                }
            } else {
                Path path = Paths.get("/storage/emulated/0/Download");
                File filePath = path.toFile();
                arraylist.add(filePath);
            }
        } catch (NullPointerException e) {
        }

        return arraylist;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void displaySongs(){
        final ArrayList<File> mySongs = findSong(Environment.getExternalStorageDirectory());

        items = new String[mySongs.size()];
        for(int i=0;i< mySongs.size();i++){
            if(mySongs.get(i).getName().toString().replace(".mp3","").replace(".wav","") != "Download") {
                items[i] = mySongs.get(i).getName().toString().replace(".mp3", "").replace(".wav", "");
            }
        }
//        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,items);
//        listView.setAdapter(myAdapter);

        CustomAdapter customAdapter = new CustomAdapter();
        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String songName = (String)listView.getItemAtPosition(position);
                startActivity(new Intent(getApplicationContext(),PlayerActivity.class).putExtra("songs",mySongs).putExtra("songname",songName).putExtra("pos",position));
            }
        });
    }

    class CustomAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View myView = getLayoutInflater().inflate(R.layout.list_item,null);
            TextView textSong = myView.findViewById(R.id.txtSongName);
            textSong.setSelected(true);
            textSong.setText(items[position]);

            return myView;
        }
    }
}