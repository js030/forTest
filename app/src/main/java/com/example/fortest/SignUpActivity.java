package com.example.fortest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fortest.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot()); // 뷰 바인딩 활용

        mAuth = FirebaseAuth.getInstance();

        Button btnSignUp = binding.btnSignUp;
        TextView txtAlreadyHaveAccount = binding.txtAlreadyHaveAccount;

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser(); // 회원가입 기능
            }
        });


        txtAlreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToLogin(); // 이미 계정이 있으므로 로그인 페이지로 이동
            }
        });


        if (mAuth.getCurrentUser() != null){
            finish();
            return;
        } // 이미 인증이 되어 있을 시 다음 화면으로
    }


    private void registerUser(){
        EditText txtUsername = binding.txtUsername;
        EditText txtEmail = binding.txtEmail;
        EditText txtPassword = binding.txtPassword;

        String username = txtUsername.getText().toString();
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            User user = new User(username, email);
                            FirebaseDatabase.getInstance().getReference("users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            startMainActivity(); // 회원가입 성공 시 메인 화면으로 이동
                                        }
                                    });

                        }else{
                            Toast.makeText(SignUpActivity.this, "회원가입에 실패 하였습니다", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    } // 회원가입 기능 구현

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void switchToLogin() {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

}