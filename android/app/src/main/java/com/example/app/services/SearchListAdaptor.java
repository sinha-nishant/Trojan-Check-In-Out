package com.example.app.services;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.app.R;
import com.example.app.firebaseDB.FbCheckInOut;
import com.example.app.firebaseDB.FbQuery;
import com.example.app.users.StudentAccount;
import com.example.app.users.StudentActivity;
import com.google.android.material.card.MaterialCardView;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SearchListAdaptor extends ArrayAdapter<StudentAccount> {
    private final Context mContext;
    private final int mResource;
    private int lastPosition=-1;
    private AlertDialog alertDialog;
    private Bundle bundle;
    static class ViewHolder{
        TextView name ;
        TextView email;
        TextView id;
        ImageView pic;
        TextView major;
        TextView isActive;
        ListView history;
    }

    public SearchListAdaptor(Context context, int resource, List<StudentAccount> objects){
        super(context,resource,objects);
        mContext=context;
        mResource=resource;
        bundle=new Bundle();
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

        String isActive;
        if (getItem(position).getIsActive() == null || getItem(position).getIsActive()) {
            isActive = "Not deleted";
        }
        else {
            isActive = "DELETED";
        }

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
            holder.isActive = convertView.findViewById(R.id.isActive);
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
        holder.isActive.setText(isActive);
        if (holder.isActive.getText() == "DELETED") {
            holder.isActive.setTextColor(Color.RED);
        }

        ArrayList<String> visits = new ArrayList<>();
        for(StudentActivity visit: history)
            visits.add(visit.toString());
        ArrayAdapter<String> buildingAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, visits);
        holder.history.setAdapter(buildingAdapter);

        Glide.with(mContext).load(url).thumbnail(0.2F).error(Glide.with(holder.pic).load(R.drawable.profile_blank)).diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).into(holder.pic);
        //adding kick out listener
        ImageView picView=(ImageView) convertView.findViewById(R.id.majorPic);
        View finalConvertView = convertView;

        picView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                bundle.putString("id",((TextView)finalConvertView.findViewById(R.id.uscID)).getText().toString());
                bundle.putString("name",((TextView)finalConvertView.findViewById(R.id.studentName)).getText().toString());
                DialogInit();
                alertDialog.setMessage("Please Confirm if you want to kick out "+  ((TextView)finalConvertView.findViewById(R.id.studentName)).getText().toString());
                alertDialog.show();
                return false;
            }
        });



        return convertView;
    }
    private void DialogInit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle("Status of Action");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes",
                new DialogInterface
                        .OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        kickOut();

                    }
                });
        builder.setNegativeButton("No",
                new DialogInterface
                        .OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {

                    }
                });
        alertDialog = builder.create();
    }


public void kickOut(){
    MutableLiveData<StudentAccount> studentMLD = new MutableLiveData<>();
    MutableLiveData<Boolean> checkOutMLD = new MutableLiveData<>();
    Long retrieveID =Long.parseLong(bundle.getString("id"));
    final Observer<Boolean> checkOutObserver = new Observer<Boolean>(){
        @Override
        public void onChanged(@javax.annotation.Nullable final Boolean success){
            if(success){ //student is checked in  display checkin message
                //display new alert saying student was kicked out
                Toast.makeText(mContext,"Student Kicked Out", Toast.LENGTH_LONG).show();
            }else { //wasn't able to check in student
                //student wasn't kicked out because student is not in the building
                String error;
                if(bundle.containsKey("kickOutError")){
                    error=bundle.getString("kickOutError");
                }
                else{
                    error="student has not checked into any building";
                }
                Toast.makeText(mContext,"Student Not Kicked Out because "+ error, Toast.LENGTH_LONG).show();
            }
        }
    };
    checkOutMLD.observe((LifecycleOwner) mContext, checkOutObserver);
    final Observer<StudentAccount> studentAccountObserver = new Observer<StudentAccount>(){
        @Override
        public void onChanged(@javax.annotation.Nullable final StudentAccount student){
            // check last index of studentactivity list
            List<StudentActivity> sa_list = student.getActivity();
            StudentActivity sa;
            if(!sa_list.isEmpty()) {//no activity so check in if occupancy isn't full
                sa = sa_list.get(sa_list.size() - 1);
                if(sa.getCheckOutTime()!=null){
                    checkOutMLD.setValue(false);
                    bundle.putString("kickOutError","student checked out of last building they entered");
                    return;
                }
                bundle.putString("kickOutError","size= " +sa_list.size());
                Date kickOutDate = new Date();
                FbCheckInOut.checkOut(retrieveID,sa,kickOutDate,checkOutMLD);
            }
            else{
                checkOutMLD.setValue(false);
                bundle.putString("kickOutError","student has not checked into any building");
            }
        }
    };
    studentMLD.observe((LifecycleOwner) mContext,studentAccountObserver);
    FbQuery.getStudent(retrieveID, studentMLD);
}
}
