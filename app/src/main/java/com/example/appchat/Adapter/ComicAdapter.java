package com.example.appchat.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchat.AddActivity;
import com.example.appchat.MainActivity;
import com.example.appchat.Model.Comic;
import com.example.appchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import java.util.List;

public class ComicAdapter extends RecyclerView.Adapter<ComicAdapter.ViewHolder> {
    List<Comic> comicList;
    private MainActivity mainActivity;
    DatabaseReference databaseReference;
    FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }


    public ComicAdapter(List<Comic> comicList) {
        this.comicList = comicList;
    }
    public void setComicList(List<Comic> comicList) {
        this.comicList = comicList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comic comic=comicList.get(position);
        holder.titleTextView.setText(comic.getMota());
        holder.tvname.setText(String.valueOf(comic.getName()));
        holder.tvchapter.setText(comic.getChapter());

        holder.imgDelete.setVisibility(View.INVISIBLE);
        holder.imgEdit.setVisibility(View.INVISIBLE);
        getPermission(holder);
        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   mainActivity.deleteComic(comic.getId());
            }
        });

        holder.imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   mainActivity.editComic(comic);
            }
        });

        if (!comic.getImage().equals("")){
            Picasso.get().load(comic.getImage()).into(holder.thumbnailImageView);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(comic);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return comicList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tvname,tvchapter;
        public TextView titleTextView;
        public ImageView thumbnailImageView;
        public ImageView imgDelete,imgEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView= itemView.findViewById(R.id.tvtitle);
            thumbnailImageView=itemView.findViewById(R.id.imageV5);
            tvname=itemView.findViewById(R.id.tvname);
            tvchapter=itemView.findViewById(R.id.tvchapter);
            imgDelete=itemView.findViewById(R.id.imgDeleteS);
            imgEdit=itemView.findViewById(R.id.imgEdit);

        }
    }

    private void getPermission(@NonNull ViewHolder holder) {
        String userId = user.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Long permission = snapshot.child("role").getValue(Long.class);
                    if (permission==1){
                        holder.imgDelete.setVisibility(View.VISIBLE);
                        holder.imgEdit.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public interface OnItemClickListener {
        void onItemClick(Comic comic);
    }
    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
