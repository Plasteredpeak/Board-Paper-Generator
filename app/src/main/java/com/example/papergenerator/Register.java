package com.example.papergenerator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity implements View.OnClickListener {

    private TextView ARegister;
    private TextView Uname;
    private TextView Email;
    private TextView Pass;
    private TextView ConfirmPass;
    private TextView registerbtn;
    private ProgressBar bar;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth=FirebaseAuth.getInstance();

        ARegister=findViewById(R.id.already);
        Uname=(EditText)findViewById(R.id.emailIn);
        Email=(EditText)findViewById(R.id.inputEmail);
        Pass=(EditText)findViewById(R.id.pass_in);
        ConfirmPass=(EditText)findViewById(R.id.inputPassword2);
        registerbtn=(Button) findViewById(R.id.RegButton);
        bar=findViewById(R.id.progressBar);
        bar.setVisibility(View.GONE);

        registerbtn.setOnClickListener(this);
        ARegister.setOnClickListener(this);

    }
    @Override
    public void onClick (View v){
        if(v.getId()== R.id.already){
            finish();
        }
        if(v.getId()==R.id.RegButton){
            if(Uname.getText().toString().trim().isEmpty()){
                Uname.setError("Enter your Username");
                Uname.requestFocus();
            }
            else if (Email.getText().toString().trim().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(Email.getText().toString().trim()).matches()){
                    Email.setError("Enter a Vaild Email");
                    Email.requestFocus();
                }
            else if(Pass.getText().toString().trim().isEmpty() || Pass.getText().toString().trim().length()<8){
                Pass.setError("Password length must be atleast 8");
                Pass.requestFocus();
            }
            else if(!ConfirmPass.getText().toString().trim().equals(Pass.getText().toString().trim())){
                ConfirmPass.setError("Password Doesn't match the orignal");
                ConfirmPass.requestFocus();
            }
            else {
                bar.setVisibility(View.VISIBLE);
                auth.createUserWithEmailAndPassword(Email.getText().toString().trim(),Pass.getText().toString().trim())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    User user=new User(Uname.getText().toString().trim(),Email.getText().toString().trim());

                                    FirebaseDatabase.getInstance().getReference("Users")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(Register.this,"User Registered",Toast.LENGTH_LONG).show();
                                                        bar.setVisibility(View.GONE);
                                                    }
                                                    else {
                                                        Toast.makeText(Register.this, "Registeration Failed, Try Again!", Toast.LENGTH_LONG).show();
                                                        bar.setVisibility(View.GONE);
                                                    }
                                                }
                                            });

                                    finish();

                                }
                                else {
                                    Toast.makeText(Register.this, "User Already Registered, Try Again!", Toast.LENGTH_LONG).show();
                                    bar.setVisibility(View.GONE);
                                    Uname.setText("");
                                    Email.setText("");
                                    Pass.setText("");
                                    ConfirmPass.setText("");
                                    Uname.requestFocus();
                                }

                            }
                        });
            }
        }
    }
}