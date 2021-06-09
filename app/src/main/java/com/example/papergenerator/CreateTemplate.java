package com.example.papergenerator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class CreateTemplate extends AppCompatActivity implements View.OnClickListener {

    FirebaseUser user;
    DatabaseReference ref;

    EditText Tname,Tmarks,Ttime,Tqno;
    RadioGroup Rg;
    RadioButton Rb;
    Button creatT,cancel;
    ProgressBar bar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_template);
        Tname=findViewById(R.id.Tname_input);
        Tmarks=findViewById(R.id.marks_input);
        Ttime=findViewById(R.id.time_input);
        Tqno=findViewById(R.id.qno_input);
        creatT=findViewById(R.id.create);
        cancel=findViewById(R.id.CancelT);

        auth=FirebaseAuth.getInstance();

        bar=(ProgressBar) findViewById(R.id.Tbar);
        bar.setVisibility(View.GONE);

        Rg=findViewById(R.id.Ptype);

        creatT.setOnClickListener(this);
        cancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        String name=Tname.getText().toString().trim();
        String marks=Tmarks.getText().toString().trim();
        String time=Ttime.getText().toString().trim();
        String qno=Tqno.getText().toString().trim();

        if(v.getId()==R.id.create){
            if(name.isEmpty()){
                Tname.setError("Template Name cannot be Empty");
                Tname.requestFocus();
            }
            else if(qno.isEmpty()){
                Tqno.setError("Questions Field cannot be empty");
                Tqno.requestFocus();
            }
            else if(Integer.parseInt(qno)<=0){
                Tqno.setError("No of questions cannot be 0");
                Tqno.requestFocus();
            }
            else if(marks.isEmpty()){
                Tmarks.setError("Marks Field cannot be empty");
                Tmarks.requestFocus();
            }
            else if(Integer.parseInt(marks)<=0){
                Tmarks.setError("Marks cannot be 0");
                Tmarks.requestFocus();
            }
            else if(time.isEmpty()){
                Ttime.setError("Time Field cannot be empty");
                Ttime.requestFocus();
            }
            else if(Float.parseFloat(time)>3 || Float.parseFloat(time)<=0){
                Ttime.setError("Time cannot be less than 0 or greater than 3");
                Ttime.requestFocus();
            }
            else {
                user=FirebaseAuth.getInstance().getCurrentUser();
                ref= FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("Template").push();
                bar.setVisibility(View.VISIBLE);
                int rid= Rg.getCheckedRadioButtonId();
                Rb=findViewById(rid);
                Map<String, Object> values = new HashMap<>();
                values.put("Name", name);
                values.put("No of Questions", qno);
                values.put("Paper Type",Rb.getText().toString());
                values.put("Marks", marks);
                values.put("Time Duration", time+" hours");
                ref.setValue(values)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(CreateTemplate.this,"Template Created",Toast.LENGTH_LONG).show();
                                    bar.setVisibility(View.GONE);
                                    finish();
                                }
                                else {
                                    Toast.makeText(CreateTemplate.this, "Error, Try Again!", Toast.LENGTH_LONG).show();
                                    bar.setVisibility(View.GONE);
                                }
                            }
                        });
            }

        }
        if(v.getId()==R.id.CancelT){
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage("Are you sure you want to Cancel?\nAll Progress will be lost").setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog= alert.create();
            alertDialog.show();
        }
    }
}