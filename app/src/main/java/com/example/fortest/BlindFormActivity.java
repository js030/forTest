package com.example.fortest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fortest.databinding.ActivityBlindFormBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class BlindFormActivity extends AppCompatActivity {

    ActivityBlindFormBinding binding;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBlindFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        Button blindFormBtn = binding.blindFormBtn;

        blindFormBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeBlind(); // 시각장애우분들 정보 저장
            }
        });

    }

    private void storeBlind() {

        EditText blindName = binding.blindName;
        EditText blindAge = binding.blindAge;
        EditText blindSex = binding.blindSex;

        String name = blindName.getText().toString();
        String age = blindAge.getText().toString();
        String gender = blindSex.getText().toString();

        if (name.isEmpty() || age.isEmpty() || gender.isEmpty()){
            Toast.makeText(this, "빈칸을 채워주세요", Toast.LENGTH_SHORT).show();
            return;
        } // 입력을 안 했을 경우 확인

        Blind blind = new Blind(name, age, gender);

        db.collection("blinds")
                .add(blind)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        switchToMainActivity();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(BlindFormActivity.this, "실패하였습니다", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void switchToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}