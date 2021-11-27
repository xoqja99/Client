package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db;
    TextView TV_signup;
    TextView TV_sample; // 샘플코드
    EditText et_inputID;
    EditText et_inputPW;
    Button BTN_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();


        // 상단 타이틀바 제거
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        // 회원가입 textview, 로그인 button 변수 설정
        TV_signup = (TextView) findViewById(R.id.tv_signup);
        TV_sample = (TextView) findViewById(R.id.tv_sample); // 샘플코드
        BTN_login = (Button) findViewById(R.id.btn_login);
        et_inputID = (EditText) findViewById(R.id.et_inputid);
        et_inputPW = (EditText) findViewById(R.id.et_inputpassword);
    }

    // 로그인 버튼 클릭
    public void btn_LoginClicked(View v) {
        String str = TV_sample.getText().toString();
        Toast.makeText(getApplicationContext(),str,Toast.LENGTH_SHORT);
        Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
        startActivity(intent);
    }

    // 회원가입 텍스트뷰 클릭
    public void tv_SignupClicked(View v) {
        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
        startActivity(intent);
    }

    // 샘플 코드
    public void btn_SampleClicked(View v) {
        getData("user", "asdf", 1);
        getData("user", "asdf", 2);
        getData("user", "asdf", 3);
        getData("user", "asdf", 4);
        //getAllData("user", 1);
    }

    // Firestore에 데이터 추가
    // 사용법:
    // Map<String, Object> data = new HashMap<>();
    // data.put(key1, value1);
    // data.put(key2, value2);
    // putData("user", "zxcv", data);
    public void putData(String collec, String doc, Object data) {
        DocumentReference docRef = db.collection(collec).document(doc);
        docRef
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("데이터 추가", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("데이터 추가", "Error updating document", e);
                    }
                });
    }

    // Firestore에 데이터 업데이트
    public void updateData(String collec, String doc, String key, String value) {
        DocumentReference docRef = db.collection(collec).document(doc);
        docRef
                .update(key, value)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("데이터 업데이트", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("데이터 업데이트", "Error updating document", e);
                    }
                });
    }

    // Firestore에서 데이터 읽기
    public void getData(String collec, String doc, int what) {
        DocumentReference docRef = db.collection(collec).document(doc);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("데이터 읽기", "DocumentSnapshot data: " + document.getData());
                        useData(document.getData(), what);
                    } else {
                        Log.d("데이터 읽기", "No such document");
                    }
                } else {
                    Log.d("데이터 읽기", "get failed with ", task.getException());
                }
            }
        });
    }

    // Firestore에서 데이터 모두가져오기
    public void getAllData(String collec, int what) {
        ArrayList<Map<String, Object>> arr = new ArrayList<>();
        db.collection(collec)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("데이터 모두 읽기", document.getId() + " => " + document.getData());
                                arr.add(document.getData());
                            }
                            useData(arr, what);
                        } else {
                            Log.d("데이터 모두 읽기", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    // Firestore에서 가져온 데이터 사용
    public void useData(Map<String, Object> data, int what) {
        switch (what) {
            //PW
            case 1:
                String[] splitID = data.toString().split(",");
                String[] splitID2 = splitID[2].split("=");
                TV_sample.setText(splitID2[1]); // 샘플코드
                et_inputID.setText(splitID2[1]);
                break;
            //NickName
            case 2:
                String[] splitNickName = data.toString().split(",");
                String[] splitNickName2 = splitNickName[1].split("=");
                TV_sample.append(" "+splitNickName2[1]+" "); // 샘플코드
                break;
            //Name
            case 3:
                String[] splitPW = data.toString().split(",");
                String[] splitPW2 = splitPW[0].split("=");
                TV_sample.append(splitPW2[1]+" "); // 샘플코드
                et_inputPW.setText(splitPW2[1]);
                break;
            //Email
            case 4:
                String[] splitEmail = data.toString().split(",");
                String[] splitEmail2 = splitEmail[3].split("=");
                String splitEmail3 = splitEmail2[1].replace("}","");
                TV_sample.append(splitEmail3+" "); // 샘플코드
                break;
        }
    }

    // Firestore에서 가져온 모든 데이터 사용
    public void useData(ArrayList<Map<String, Object>> data, int what) {
        switch (what) {
            case 1:
                TV_sample.setText(data.toString()); // 샘플코드
        }
    }
}