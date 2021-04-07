package com.example.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.app.users.StudentAccount;
import com.example.app.users.StudentActivity;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class MajorListAdaptor extends ArrayAdapter<StudentAccount> {
    private final Context mContext;
    private final int mResource;
    private int lastPosition=-1;
    static class ViewHolder{
        TextView name ;
        TextView email;
        TextView id;
        ImageView pic;
        TextView major;
        ListView history;
    }

    public MajorListAdaptor(Context context, int resource, List<StudentAccount> objects){
        super(context,resource,objects);
        mContext=context;
        mResource=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String fName= getItem(position).getFirstName();
        String lName= getItem(position).getLastName();
        String email= getItem(position).getEmail();
        String id= getItem(position).getUscID().toString();
        String url= getItem(position).getProfilePicture();
        String major = getItem(position).getMajor();
        List<StudentActivity> history = getItem(position).getActivity();

        final View result;
        ViewHolder holder;

        if(convertView ==null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView= inflater.inflate(mResource,parent,false);

            holder= new ViewHolder();
            holder.name= convertView.findViewById(R.id.studentName);
            holder.email= convertView.findViewById(R.id.email);
            holder.id= convertView.findViewById(R.id.uscID);
            holder.pic= convertView.findViewById(R.id.majorPic);
            holder.major = convertView.findViewById(R.id.major);
            holder.history = convertView.findViewById(R.id.visitHistory);

            MaterialCardView materialCardView = convertView.findViewById(R.id.card);

            materialCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.history.getVisibility() == View.VISIBLE) {
                        holder.history.setVisibility(View.GONE);
                    } else {
                        holder.history.setVisibility(View.VISIBLE);
                    }
                }
            });

            result= convertView;
            convertView.setTag(holder);
        }
        else {
            holder= (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation= AnimationUtils.loadAnimation(mContext, (position>lastPosition) ? R.anim.load_down_anim : R.anim.load_up_anim);
        animation.setDuration(300);
        result.startAnimation(animation);
        lastPosition=position;

        holder.name.setText(String.format("%s %s", fName, lName));
        holder.email.setText(email);
        holder.id.setText(id);
        holder.major.setText(major);

        ArrayList<String> visits = new ArrayList<>();
        for(StudentActivity visit: history)
            visits.add(visit.toString());
        ArrayAdapter<String> buildingAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, visits);
        holder.history.setAdapter(buildingAdapter);

        Glide.with(mContext).load(url).error(Glide.with(holder.pic).load(R.drawable.profile_blank)).diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).into(holder.pic);

        return convertView;
    }
}
