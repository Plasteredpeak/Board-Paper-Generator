package com.example.papergenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class TemplateMenu extends AppCompatActivity implements View.OnClickListener{

    Button create,upload,load,back;

    String TemplateName;

    FirebaseUser user;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_menu);

        create=findViewById(R.id.createT);
        load=findViewById(R.id.LoadT);
        upload=findViewById(R.id.uploadT);
        back=findViewById(R.id.backT);

        create.setOnClickListener(this);
        load.setOnClickListener(this);
        upload.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.backT){
            finish();
        }
        if(v.getId()==R.id.uploadT){
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage("Feature not Available yet!").setCancelable(false)
                    .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog= alert.create();
            alertDialog.show();
        }
        if(v.getId()==R.id.createT){
            startActivity(new Intent(this,CreateTemplate.class));
        }
        if(v.getId()==R.id.LoadT){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enter Name of the Template");

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    TemplateName= input.getText().toString();
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    ref = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("Template");
                    ref.addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    FindTemplate((Map<String, Object>) dataSnapshot.getValue());
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }
    }

    private void FindTemplate(Map<String, Object> users) {
        ArrayList<String> Names= new ArrayList<>();

        for (Map.Entry<String, Object> entry : users.entrySet()){

                Map User = (Map) entry.getValue();

            if(User.get("Name").toString().equals(TemplateName)) {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setMessage("Name :"+User.get("Name").toString()+
                        "\nPaper Type :"+User.get("Paper Type")+"\nNumber of Questions :"+User.get("No of Questions")
                +"\nMarks :"+User.get("Marks")+"\nTime Duration :"+User.get("Time Duration")).setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog= alert.create();
                alertDialog.show();

                return;
            }
        }

        Toast.makeText(TemplateMenu.this, "No Template Found!", Toast.LENGTH_LONG).show();

    }
}