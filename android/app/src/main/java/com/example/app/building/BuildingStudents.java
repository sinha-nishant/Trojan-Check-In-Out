package com.example.app.building;

import android.content.Intent;
import android.os.Bundle;
import android.sax.Element;
import android.transition.Explode;
import android.transition.Fade;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.firebaseDB.FbQuery;
import com.example.app.firebaseDB.FirestoreConnector;
import com.example.app.users.StudentAccount;
import com.example.app.users.StudentActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class BuildingStudents extends AppCompatActivity {
    private RecyclerView mFirestoreData;
    private FirebaseFirestore fireStore;
    private FirestoreRecyclerAdapter firestoreRecyclerAdapter;
    public static List<StudentActivity> studentActivityList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_students);
        fireStore = FirestoreConnector.getDB();
        mFirestoreData = findViewById(R.id.studentRView);
        Bundle extras = getIntent().getExtras();
        String buildingName= extras.getString("buildingName");
        Query query = fireStore.collection("Buildings").whereEqualTo("name",buildingName);
        //RecyclerOptions
        FirestoreRecyclerOptions<Building> options = new FirestoreRecyclerOptions.Builder<Building>().setQuery(query,Building.class).build();
        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<Building, BuildingStudents.StudentIdViewHolder>(options) {
            @NonNull
            @Override
            public BuildingStudents.StudentIdViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View theView = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_list_item,parent,false);
                return new BuildingStudents.StudentIdViewHolder(theView);

            }

            @Override
            protected void onBindViewHolder(@NonNull BuildingStudents.StudentIdViewHolder holder, int position, @NonNull Building model) {
                List<Long> ids = model.getStudents_ids();
                ArrayAdapter adapter = new ArrayAdapter<Long>(getApplicationContext(), R.layout.activitylistview, ids);
                ListView listView = holder.idList;
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        startTransition(adapter.getItem(position).toString());
                    }
                });
            }
        };
        mFirestoreData.setHasFixedSize(false);
        mFirestoreData.setLayoutManager(new LinearLayoutManager(this));
        mFirestoreData.setAdapter(firestoreRecyclerAdapter);

    }
    @Override
    protected void onStart() {
        super.onStart();
        firestoreRecyclerAdapter.startListening();
    }

    private void startTransition(String id) {
        Intent intent = new Intent(BuildingStudents.this, StudentDetailedView.class);
        intent.putExtra("STUDENT_ID", id);
        MutableLiveData<StudentAccount> studentDetailMLD = new MutableLiveData<>();
        final Observer<StudentAccount> studentDetailAccountObserver = new Observer<StudentAccount>() {
            @Override
            public void onChanged(@Nullable final StudentAccount student) {
                intent.putExtra("STUDENT_NAME",(student.getFirstName()+" "+student.getLastName()));
                intent.putExtra("STUDENT_MAJOR", student.getMajor());
                intent.putExtra("STUDENT_EMAIL", student.getEmail());
                studentActivityList = student.getActivity();
                intent.putExtra("STUDENT_IMAGE",student.getProfilePicture());
                startActivity(intent);
            }
        };
        studentDetailMLD.observe(BuildingStudents.this,studentDetailAccountObserver);
        FbQuery.getStudent(Long.parseLong(id),studentDetailMLD);
    }
    @Override
    protected void onStop() {
        super.onStop();
        firestoreRecyclerAdapter.stopListening();
    }

    private class StudentIdViewHolder extends RecyclerView.ViewHolder {
        private ListView idList;
        public StudentIdViewHolder(@NonNull View itemView) {
            super(itemView);
            idList =(ListView) itemView.findViewById((R.id.studentIDListView));
        }
    }

}