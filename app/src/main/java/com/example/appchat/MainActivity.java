package com.example.appchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.appchat.Adapter.ComicAdapter;
import com.example.appchat.Model.Comic;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private ComicAdapter comicAdapter;
    private RecyclerView recyclerView;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
    List<Comic> comicList;
    FloatingActionButton fab;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab=findViewById(R.id.fab);
        recyclerView=findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        capnhatRCV();
        loadData();
        getPermission();
        fab.setVisibility(View.INVISIBLE);

    }

    public void capnhatRCV(){
        comicList=new ArrayList<>();
        comicAdapter =new ComicAdapter(comicList);
        comicAdapter.setMainActivity(this);
        recyclerView.setAdapter(comicAdapter);
        comicAdapter.setOnItemClickListener(new ComicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Comic comic) {
                Intent intent = new Intent(MainActivity.this, ChitietActivity.class);
                intent.putExtra("id",comic.getId());
                intent.putExtra("name",comic.getName());
                intent.putExtra("chapter",comic.getChapter());
                intent.putExtra("mota",comic.getMota());
                intent.putExtra("image",comic.getImage());
                startActivity(intent);
            }
        });
    }

    private void getPermission() {
        String userId = user.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Lấy thông tin phân quyền của người dùng từ snapshot
                    Long permission = snapshot.child("role").getValue(Long.class);
                    // Xử lý thông tin phân quyền tại đây
                    if (permission==1){
                        fab.setVisibility(View.VISIBLE);
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(MainActivity.this,AddActivity.class));
                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void loadData(){
        DatabaseReference comicsRef = FirebaseDatabase.getInstance().getReference("comics");
        comicsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                comicList.clear();
                for (DataSnapshot comicSnapshot : dataSnapshot.getChildren()) {
                    Comic comic = comicSnapshot.getValue(Comic.class);
                    comicList.add(comic);
                }
                comicAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Failed to load comics", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteComic(String id){
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Delete");
        builder.setMessage("Bạn có muốn xóa không ?");
        builder.setCancelable(true);

        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        databaseReference=FirebaseDatabase.getInstance().getReference("comics");
                        databaseReference.child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(MainActivity.this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Lỗi khi xóa", Toast.LENGTH_SHORT).show();
                            }
                        });

                        loadData();
                        dialog.cancel();
                    }
                });
        builder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert=builder.create();
        builder.show();
    }

    public void editComic(Comic comic){
        Intent intent=new Intent(MainActivity.this,EditActivity.class);
        intent.putExtra("id",comic.getId());
        intent.putExtra("name",comic.getName());
        intent.putExtra("chapter",comic.getChapter());
        intent.putExtra("mota",comic.getMota());
        intent.putExtra("image",comic.getImage());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}