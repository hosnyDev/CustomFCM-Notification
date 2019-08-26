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
import com.hosnydev.customfcm.home.MainActivity;
import com.hosnydev.customfcm.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    // view
    private EditText editTextEmail, editTextPassword;
    private ProgressBar progressBar;

    // firebase
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // findView
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.proRegister);

        // firebase
        firebaseAuth = FirebaseAuth.getInstance();

        // onClick button
        findViewById(R.id.btnLogin).setOnClickListener(this);
        findViewById(R.id.createNewAccount).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnLogin) {
            validationError();
        } else if (id == R.id.createNewAccount) {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        }
    }

    private void validationError() {

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

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

        Login(email, password);

    }

    private void Login(String email, String password) {

        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
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

    private void showAlert(String MSG) {

        // create alert dialog
        AlertDialog.Builder al = new AlertDialog.Builder(this);
        al.setMessage(MSG)
                .setPositiveButton("حاول مرة اخرى", null);
        al.create();
        al.show();
    }

}
