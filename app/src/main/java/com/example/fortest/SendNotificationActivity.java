package com.example.fortest;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fortest.databinding.ActivitySendNotificationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendNotificationActivity extends AppCompatActivity {

    FirebaseFirestore db;
    ActivitySendNotificationBinding binding;
    Driver driver;
    String fcmToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySendNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        Button btnGetData = binding.btnGetData;

        btnGetData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchData("432435");// 버스 번호로 query 하여 db에서 운전자 정보 가져오는 함수
                fcmPostRequest(fcmToken); // 위 fetchData 함수에서 가져온 token으로 fcm 요청하는 함수
            }
        });
    }

    private void fcmPostRequest(String token) {

        Notification notification = new Notification();
        notification.setTitle("Retofit 테스트");
        notification.setBody("메세지 전송");
        FcmRequest fcmRequest = new FcmRequest();
        fcmRequest.setTo(token);
        fcmRequest.setNotification(notification);


        FcmClient fcmClient = FcmClient.getInstance();

        if (fcmClient != null){
            FcmApi fcmApi = fcmClient.getFcmApi();

            fcmApi.pushNotification(fcmRequest).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    System.out.println(response);
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    System.out.println("Failed");
                }
            });
        }
    }

    private void fetchData(String busNumber) {
        db.collection("drivers")
                .whereEqualTo("busNum", busNumber)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                driver = document.toObject(Driver.class); // 가져온 정보를 다시 DTO에 담음

                                fcmToken = driver.fcmToken; // DTO에서 fcm Token을 가져옴

                            }
                        } else {
                            System.out.println("정보 가져오기 실패");
                        }
                    }
                });
    }
}