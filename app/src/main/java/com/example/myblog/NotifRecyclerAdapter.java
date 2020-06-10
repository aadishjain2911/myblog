package com.example.myblog;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotifRecyclerAdapter extends RecyclerView.Adapter<NotifRecyclerAdapter.ViewHolder> {

    public List<NotifPost> notif_list ;

    private FirebaseFirestore firebaseFirestore ;
    private FirebaseAuth firebaseAuth ;

    private Context context;

    public NotifRecyclerAdapter(Context context){
        this.context=context;
    }

    public NotifRecyclerAdapter(List<NotifPost> notif_list) {
        this.notif_list = notif_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notif_list_item,parent,false);

        firebaseFirestore = FirebaseFirestore.getInstance() ;
        firebaseAuth = FirebaseAuth.getInstance() ;
        return new ViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull final NotifRecyclerAdapter.ViewHolder holder, int position) {

        holder.setIsRecyclable (false) ;

        final String currentUserId = firebaseAuth.getCurrentUser().getUid() ;
        final String blogPostId = notif_list.get(position).getBlogPostId() ;
        final String senderId = notif_list.get(position).getSender_user_id() ;
        final String type = notif_list.get(position).getType() ;

        long milliseconds = notif_list.get(position).getTimestamp().getTime() ;
        String dateString = DateFormat.format("dd/mm/yyyy", new Date(milliseconds)).toString();
        holder.setTime(dateString);

        firebaseFirestore.collection("Users").document(senderId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    String name = task.getResult().getString("name") ;
                    holder.addName(name) ;
                    String image = task.getResult().getString("image") ;
                    holder.addImage(image) ;
                }

            }
        });

        firebaseFirestore.collection("Posts").document(blogPostId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    String eventname = task.getResult().getString("name") ;
                    holder.addEvent(eventname) ;
                }

            }
        });

        holder.setMessage(type) ;

    }

    public int getItemCount() {
        return notif_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView notif_view ;

        private TextView date_view ;

        private CircleImageView sender_imageview ;

        private View mView ;

        private String sender_name, sender_image , event_name;

        public ViewHolder (@Nonnull View itemview) {
            super(itemview) ;

            mView = itemview ;

        }

        public void setTime(String timestamp) {

            date_view = mView.findViewById(R.id.notif_time) ;
            date_view.setText(timestamp) ;

        }

        public void addName(String name) {
            sender_name = name ;
        }

        public void addImage(String image) {
            sender_image = image ;
        }

        public void addEvent(String eventname) {
            event_name = eventname ;
        }

        public void setMessage(final String type) {

            notif_view = mView.findViewById(R.id.notif_textview) ;
            notif_view.setText(sender_name+"has "+type+" you for the event "+event_name+". Tell him if you are interested.") ;

            RequestOptions placeholderoption = new RequestOptions() ;
            placeholderoption.placeholder(R.drawable.kindpng_4517876) ;
            Glide.with(context).applyDefaultRequestOptions(placeholderoption).load(sender_image).into(sender_imageview) ;
        }
    }
}
