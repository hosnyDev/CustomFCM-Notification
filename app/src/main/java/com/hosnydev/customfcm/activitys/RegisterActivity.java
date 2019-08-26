package com.hosnydev.customfcm.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.hosnydev.customfcm.R;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    // view
    private EditText editTextEmail, editTextPassword, editTextName;
    private ProgressBar progressBar;

    // firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private String email, password, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // findView
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextName = findViewById(R.id.name);
        progressBar = findViewById(R.id.proRegister);

        // firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // onClick button
        findViewById(R.id.btnRegister).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnRegister) {
            validationError();
        }
    }

    private void validationError() {

        email = editTextEmail.getText().toString().trim();
        password = editTextPassword.getText().toString().trim();
        name = editTextName.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError("Reacquired Email Address");
            editTextEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Invalid Email Address");
            editTextEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            editTextPassword.setError("Reacquired Password");
            editTextPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            editTextPassword.setError("Reacquired Password 6 char");
            editTextPassword.requestFocus();
            return;
        }
        if (name.isEmpty()) {
            editTextName.setError("Reacquired Name");
            editTextName.requestFocus();
            return;
        }
        Register(email, password);

    }

    private void Register(String email, String password) {

        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            storeUserData();
                        } else {

                            progressBar.setVisibility(View.GONE);
                            try {

                                Exception exception = task.getException();
                                assert exception != null;
                                throw exception;

                            } catch (FirebaseAuthWeakPasswordException e) {

                                showAlert("كلمه المرور ضعيفه");

                            } catch (FirebaseAuthInvalidCredentialsException e) {

                                showAlert("البريد الالكتروني او كلمه المرور خطا");

                            } catch (FirebaseAuthUserCollisionException e) {

                                showAlert("هذا البريد الالكتروني مسجل من قبل");

                            } catch (FirebaseNetworkException e) {

                                showAlert("لا يوجد اتصال بالانترت");


                            } catch (FirebaseAuthInvalidUserException e) {

                                showAlert("البريد الالكتروني او كلمه المرور خطا");


                            } catch (Exception e) {

                                showAlert("" + e);

                            }

                        }

                    }
                });
    }

    private void storeUserData() {
        if (firebaseAuth.getCurrentUser() != null) {

            String uID = firebaseAuth.getCurrentUser().getUid();
            String tokin = FirebaseInstanceId.getInstance().getToken();
            DocumentReference db = firestore.collection("user").document(uID);

            Map<String, Object> map = new HashMap<>();
            map.put("id", uID);
            map.put("email", email);
            map.put("password", password);
            map.put("name", name);
            map.put("timestamp", FieldValue.serverTimestamp());
            assert tokin != null;
            map.put("tokin", tokin);


            db.set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {

                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();

                    } else {
                        progressBar.setVisibility(View.GONE);

                        try {

                            Exception exception = task.getException();
                            assert exception != null;
                            throw exception;

                        } catch (FirebaseNetworkException e) {

                            showAlert("لا يوجد اتصال بالانترت");


                        } catch (Exception e) {

                            showAlert("" + e);

                        }
                    }

                }
            });


        }
    }

    private void showAlert(String MSG) {

        // create alert dialog
        AlertDialog.Builder al = new AlertDialog.Builder(this);
        al.setMessage(MSG)
                .setPositiveButton("حاول مرة اخرى", null);
        al.create();
        al.show();
    }
}
