package com.example.appchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {
    EditText ed_email,ed_pass,edrepass;
    Button btnRegister;
    TextView tv_goLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initUI();

        checkUserLogin();
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=ed_email.getText().toString().trim();
                String password=ed_pass.getText().toString().trim();
                String repass=edrepass.getText().toString().trim();

                if (Validate(email,password,repass)){
                    SaveUserToFireBase(email,password);
                    Toast.makeText(getApplicationContext(),"Register successfully",Toast.LENGTH_SHORT).show();
                }

            }
        });



        tv_goLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });

    }

    private void initUI(){
        ed_email=findViewById(R.id.edt_email);
        ed_pass=findViewById(R.id.edt_password);
        edrepass=findViewById(R.id.edt_rePassword);
        tv_goLogin=findViewById(R.id.tv_goLogin);
        btnRegister=findViewById(R.id.btn_register);
    }


    private void checkUserLogin(){
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if (user ==null){
            Intent intent=new Intent(this,LoginActivity.class);
            startActivity(intent);
        }else {
            Intent intent=new Intent(this,MainActivity.class);
            startActivity(intent);
        }
        finishAffinity();
    }

    private boolean Validate(String email,String password,String rePassword){
        boolean check = true;
        if(!email.isEmpty() && !password.isEmpty() && !rePassword.isEmpty()) {
            if(!email.matches("^[a-zA-Z0-9_]+@[a-zA-Z0-9]+(\\.[a-zA-Z0-9]+){1,2}$")
                    || !password.matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$")
                    || !password.equals(rePassword)){
                if (!password.matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$")) {
                    ed_pass.setError("Mật khẩu phải có 5 ký tự trở lên, Ít nhất 1 chữ in hoa và 1 chữ thường !");
                    ed_pass.requestFocus();
                } else if (!password.equals(rePassword)) {
                    edrepass.setError("Nhập lại mật khẩu chưa chính xác !");
                    edrepass.requestFocus();
                } else {
                    ed_email.setError("Email không hợp lệ !");
                    ed_email.requestFocus();
                }
                check = false;
            }
        } else {
            if (email.isEmpty()){
                ed_email.requestFocus();
                ed_email.setError("Khong de trong truong nay");
            }

            if (password.isEmpty()){
                ed_pass.requestFocus();
                ed_pass.setError("Khong de trong truong nay");
            }
            if(rePassword.isEmpty()){
                edrepass.requestFocus();
                edrepass.setError("Khong de trong truong nay");
            }
            check = false;
        }
        return check;
    }

    private void SaveUserToFireBase(String email, String password){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user=mAuth.getCurrentUser();
                            String id=user.getUid();
                            String email=user.getEmail();

                            DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("users");
                            databaseReference.child(id).child("id").setValue(id);
                            databaseReference.child(id).child("email").setValue(email);
                            databaseReference.child(id).child("role").setValue(0);

                            Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                            startActivity(intent);
                            finishAffinity();

                        } else {
                            Log.w("w", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

}