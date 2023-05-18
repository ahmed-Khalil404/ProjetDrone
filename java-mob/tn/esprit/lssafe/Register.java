package tn.esprit.lssafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

public class Register extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private TextView banner;
    private Button registreUser, exit;
    private EditText FName, Email,Phone, Password, CPassword;
    private ProgressBar progressBar;

    private CountryCodePicker ccp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        banner = (TextView) findViewById(R.id.banner);
        banner.setOnClickListener(this);



        registreUser = (Button) findViewById(R.id.registreuser);
        registreUser.setOnClickListener(this);

        exit = (Button) findViewById(R.id.cancel);
        exit.setOnClickListener(this);




        FName = (EditText) findViewById(R.id.fullName);
        Email = (EditText) findViewById(R.id.email);
        Phone = (EditText) findViewById(R.id.phone);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);

        Password = (EditText) findViewById(R.id.password);
        CPassword = (EditText) findViewById(R.id.conpassword);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);



    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null
                && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE)
                && v instanceof EditText
                && !v.getClass().getName().startsWith("android.webkit.")) {
            int[] scrcoords = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop()
                    || y > v.getBottom())
                hideKeyboard(this);
        }
        return super.dispatchTouchEvent(ev);
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null
                && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView()
                    .getWindowToken(), 0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.banner:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.cancel:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.registreuser:
                registreUser();
                break;
        }
    }


    private void registreUser() {
        String fullName = FName.getText().toString().trim();
        String email = Email.getText().toString().trim();
        String phone = Phone.getText().toString().trim();
        String ceinture ="";
        String serial ="";
        String role ="user";
        String password = Password.getText().toString().trim();
        String conpassword = CPassword.getText().toString().trim();

        if (fullName.isEmpty()) {
            FName.setError("Full Name is required!");
            FName.requestFocus();
            return;
        }
        if (phone.isEmpty()) {
            Phone.setError("Phone is required!");
            Phone.requestFocus();
            return;
        }
        if (!Patterns.PHONE.matcher(phone).matches()) {
            Phone.setError("Please provide a valid phone number!");
            Phone.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            Email.setError("Email is required!");
            Email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Email.setError("Please provide a valid email!");
            Email.requestFocus();
            return;
        }else{
            progressBar.setVisibility((View.VISIBLE));
            mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            if (task.isSuccessful()){
                                boolean check =!task.getResult().getSignInMethods().isEmpty();
                                if (!check){
                                    progressBar.setVisibility((View.GONE));
                                }
                                else {
                                    progressBar.setVisibility((View.GONE));
                                    Toast.makeText(getApplicationContext(),"email already exist",Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
        }
        if (password.isEmpty()) {
            Password.setError("Password is required!");
            Password.requestFocus();
            return;
        }
        if (password.length() < 6) {
            Password.setError("Min password length should be 6 characters!");
            Password.requestFocus();
            return;
        }
        if (conpassword.isEmpty()) {
            CPassword.setError("Confirm Password is required!");
            CPassword.requestFocus();
            return;
        }
        if (!password.equals(conpassword)) {
            CPassword.setError("Password not matched!");
            CPassword.requestFocus();
            return;
        }

        String passwordv = password.toString().trim();
        progressBar.setVisibility((View.VISIBLE));
                    mAuth.createUserWithEmailAndPassword(email, passwordv).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                User user = new User(fullName,phone, email,ceinture ,serial,role);
                                FirebaseDatabase.getInstance().getReference("User")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                FirebaseUser usere = FirebaseAuth.getInstance().getCurrentUser();
                                                Toast.makeText(Register.this, "User has been registered successfully", Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility((View.GONE));
                                                usere.sendEmailVerification();
                                                Toast.makeText(Register.this, "Check your mail to verify your account!", Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        }});
                }
            }






