package com.example.papergenerator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignIn extends AppCompatActivity implements View.OnClickListener {

    TextView signup,forgetpass;
    TextView email;
    TextView pass;
    TextView signin;
    ProgressBar bar;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        signup=findViewById(R.id.signup);
        email=findViewById(R.id.emailIn);
        pass=findViewById(R.id.pass_in);
        signin=findViewById(R.id.loginbtn);
        forgetpass=findViewById(R.id.forget);
        bar=(ProgressBar) findViewById(R.id.loginbar);
        bar.setVisibility(View.GONE);

        auth=FirebaseAuth.getInstance();


        signup.setOnClickListener(this);
        signin.setOnClickListener(this);
        forgetpass.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Are you sure you want to exit?").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SignIn.super.onBackPressed();
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

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.signup){
            startActivity(new Intent(this,Register.class));
        }
        if(v.getId()==R.id.forget){
            String mail=email.getText().toString().trim();
            if (mail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(mail).matches()){
                email.setError("Email is required to reset Password");
                email.requestFocus();
            }
            else{
                bar.setVisibility(View.VISIBLE);
                auth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            bar.setVisibility(View.GONE);
                            Toast.makeText(SignIn.this, "Check Your Email to Reset Password", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(SignIn.this, "No Account with this email exists!", Toast.LENGTH_LONG).show();
                            bar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        }
        if(v.getId()==R.id.loginbtn){
            String mail=email.getText().toString().trim();
            String pas=pass.getText().toString().trim();

            if (mail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(mail).matches()){
                email.setError("Enter a Vaild Email");
                email.requestFocus();
            }
            else if(pas.isEmpty()){
                pass.setError("Password is required");
                pass.requestFocus();
            }
            else {

                bar.setVisibility(View.VISIBLE);

                auth.signInWithEmailAndPassword(mail, pas).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                            if(user.isEmailVerified()) {
                                bar.setVisibility(View.GONE);
                                Toast.makeText(SignIn.this, "Logged in Successfully", Toast.LENGTH_LONG).show();
                                finish();
                                startActivity(new Intent(SignIn.this, MainActivity.class));
                            }
                            else{
                                user.sendEmailVerification();
                                Toast.makeText(SignIn.this, "Email Verification is required! (Check Mail)", Toast.LENGTH_LONG).show();
                                pass.setText("");
                                bar.setVisibility(View.GONE);
                            }
                        }
                        else {
                            Toast.makeText(SignIn.this, "Invalid Email or Password!", Toast.LENGTH_LONG).show();
                            bar.setVisibility(View.GONE);
                            pass.setText("");
                            email.setText("");
                            pass.setError("Error");
                            email.setError("Error");
                        }
                    }
                });
            }

        }
    }
}