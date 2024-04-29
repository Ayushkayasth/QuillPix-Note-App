package com.example.quillpix;

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

import java.util.regex.Pattern;

public class CreateAccountActivity extends AppCompatActivity {

    EditText emailEditText,passwordEditText,confirmPasswordEditText;
    Button createAccountBtn;
    ProgressBar progressBar;
    TextView loginBtnText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        emailEditText = findViewById(R.id.edit_email_text);
        passwordEditText = findViewById(R.id.edit_password_text);
        confirmPasswordEditText = findViewById(R.id.edit_confirm_password_text);
        createAccountBtn = findViewById(R.id.create_account);
        progressBar = findViewById(R.id.progress_bar);
        loginBtnText = findViewById(R.id.login_txtview_btn);

        createAccountBtn.setOnClickListener(v-> createAccount());
        loginBtnText.setOnClickListener((v)->startActivity(new Intent(CreateAccountActivity.this,LoginActivity.class)));
    }

    void createAccount() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        boolean isValidate = validateData(email,password,confirmPassword);
        if(!isValidate){
            return;
        }

        createAccountInFirebase(email,password);
    }

    void createAccountInFirebase(String email, String password){
        changeInProgress(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(CreateAccountActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        changeInProgress(false);
                        if(task.isSuccessful()){
                            //task is done
                            Utility.showToast(CreateAccountActivity.this,"Account Created Successfully, Check mail to Verify");
                            firebaseAuth.getCurrentUser().sendEmailVerification();
                            firebaseAuth.signOut();
                            finish();
                        }
                        else{
                            //failure
                            Utility.showToast(CreateAccountActivity.this,task.getException().getLocalizedMessage());
                        }
                    }
                });

    }

    void changeInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            createAccountBtn.setVisibility(View.GONE);
        }
        else{
            progressBar.setVisibility(View.GONE);
            createAccountBtn.setVisibility(View.VISIBLE);
        }
    }


    boolean validateData(String email,String password,String confirmPassword){
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Email is invalid");
            return false;
        }
        if(password.length()<6){
            passwordEditText.setError("Password length is invalid");
            return false;
        }
        if(!password.equals(confirmPassword)){
            confirmPasswordEditText.setError("Password not matched");
            return false;
        }
        return true;
    }

}