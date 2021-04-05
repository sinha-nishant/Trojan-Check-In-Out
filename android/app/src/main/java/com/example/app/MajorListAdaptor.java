package com.example.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.app.users.StudentAccount;

import java.util.ArrayList;
import java.util.List;

public class MajorListAdaptor extends ArrayAdapter<StudentAccount> {
    private static final String TAG = "MajorListAdaptor";
    private Context mContext;
    private int mResource;
    private int lastPosition=-1;
    static class ViewHolder{
        TextView name ;
        TextView email;
        TextView id;
        ImageView pic;
    }


    public MajorListAdaptor(Context context, int resource, List<StudentAccount> objects){
        super(context,resource,objects);
        mContext=context;
        mResource=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return super.getView(position, convertView, parent);
        String fName= getItem(position).getFirstName();
        String lName= getItem(position).getLastName();
        String email= getItem(position).getEmail();
        String id= getItem(position).getUscID().toString();
        String url= getItem(position).getProfilePicture();

        final View result;
        ViewHolder holder;

        if(convertView ==null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView= inflater.inflate(mResource,parent,false);

            holder= new ViewHolder();
            holder.name= (TextView) convertView.findViewById(R.id.majorName);
            holder.email= (TextView) convertView.findViewById(R.id.majorEmail);
            holder.id= (TextView) convertView.findViewById(R.id.majorID);
            holder.pic= (ImageView) convertView.findViewById(R.id.majorPic);

            result= convertView;
            convertView.setTag(holder);
        }
        else{
            holder= (ViewHolder) convertView.getTag();
            result=convertView;
        }
//        LayoutInflater inflater = LayoutInflater.from(mContext);
//        convertView= inflater.inflate(mResource,parent,false);
//
//        holder= new ViewHolder();
//        holder.name= (TextView) convertView.findViewById(R.id.majorName);
//        holder.email= (TextView) convertView.findViewById(R.id.majorEmail);
//        holder.id= (TextView) convertView.findViewById(R.id.majorID);
//        holder.pic= (ImageView) convertView.findViewById(R.id.majorPic);
//
//        result= convertView;
//        convertView.setTag(holder);

//        TextView fNameTv= (TextView) convertView.findViewById(R.id.majorName);
//        TextView emailTv= (TextView) convertView.findViewById(R.id.majorEmail);
//        TextView idTv= (TextView) convertView.findViewById(R.id.majorID);
//        ImageView pic= (ImageView) convertView.findViewById(R.id.majorPic);

//        fNameTv.setText(fName+" "+lName);
//        emailTv.setText(email);
//        idTv.setText(id);
//        Glide.with(mContext).load(url).error(Glide.with(pic).load(R.drawable.profile_blank)).diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true).into(pic);


        Animation animation= AnimationUtils.loadAnimation(mContext,(position>lastPosition)? R.anim.load_down_anim:R.anim.load_up_anim);
        result.startAnimation(animation);
        lastPosition=position;

        holder.name.setText(fName+" "+lName);
        holder.email.setText(email);
        holder.id.setText(id);
        Glide.with(mContext).load(url).error(Glide.with(holder.pic).load(R.drawable.profile_blank)).diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).into(holder.pic);

        return convertView;

    }
}
