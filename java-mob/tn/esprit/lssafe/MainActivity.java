package tn.esprit.lssafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView registre, forgotPassword ;
    private EditText Eemail , Epassword ;
    private Button Login ;
    private FirebaseAuth mAuth ;
    private ProgressBar progressBar ;
    CheckBox remember ;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registre = (TextView) findViewById(R.id.registre);
        registre.setOnClickListener(this);

        Login = (Button) findViewById(R.id.login);
        Login.setOnClickListener(this);

        Eemail =(EditText)findViewById(R.id.eEmail);
        Epassword =(EditText)findViewById(R.id.epassword);
        remember=(CheckBox)findViewById(R.id.RememberMe);



        progressBar =(ProgressBar)findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
        forgotPassword=(TextView)findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(this);


        if(mAuth.getCurrentUser() != null){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user.isEmailVerified()){
/*
                if (role != null) {
                    if (role.equals("user")) {
                        startActivity(new Intent(MainActivity.this,MenUser.class));
                        finish();
                    } else if (role.equals("admin")) {
                        startActivity(new Intent(MainActivity.this,MenuTable.class));
                        finish();
                    }
                }
*/
                startActivity(new Intent(MainActivity.this,MenUser.class));
                finish();


            }else {
                //Toast.makeText(MainActivity.this,"Error",Toast.LENGTH_LONG).show();
            }

        }



        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin == true) {
            Eemail.setText(loginPreferences.getString("email", ""));
            Epassword.setText(loginPreferences.getString("password", ""));
            remember.setChecked(true);
        }

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

    public static void hideKeyboard(MainActivity activity) {
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
        switch (v.getId()){
            case R.id.registre:
                startActivity(new Intent(this,Register.class));
                break;

            case R.id.login:
            {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(Eemail.getWindowToken(), 0);

                String email = Eemail.getText().toString();
                String password = Epassword.getText().toString();

                if (remember.isChecked()) {
                    loginPrefsEditor.putBoolean("saveLogin", true);
                    loginPrefsEditor.putString("email", email);
                    loginPrefsEditor.putString("password", password);
                    loginPrefsEditor.commit();
                } else {
                    loginPrefsEditor.clear();
                    loginPrefsEditor.commit();
                }
            }
            userConect();
            break;

            case R.id.forgotPassword:
                startActivity(new Intent(this,ForgotPassword.class));
                break;

        }

    }

    private void userConect(){
        String email = Eemail.getText().toString().trim();
        String password = Epassword.getText().toString().trim();

        if (email.isEmpty()) {
            Eemail.setError("Email is required!");
            Eemail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Eemail.setError("Please provide a valid email!");
            Eemail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            Epassword.setError("Password is required!");
            Epassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            Epassword.setError("Min password length should be 6 characters!");
            Epassword.requestFocus();
            return;
        }
        progressBar.setVisibility((View.VISIBLE));
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user.isEmailVerified()){
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("User").child(user.getUid());
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String role = snapshot.child("role").getValue(String.class);
                                    if (role != null) {
                                        if (role.equals("user")) {
                                            startActivity(new Intent(MainActivity.this,MenUser.class));
                                            progressBar.setVisibility((View.GONE));
                                        } else if (role.equals("admin")) {
                                            startActivity(new Intent(MainActivity.this,MenuTable.class));
                                            progressBar.setVisibility((View.GONE));
                                        }
                                    } else {
                                        Toast.makeText(MainActivity.this,"No role found for user!",Toast.LENGTH_LONG).show();

                                    }
                                } else {
                                    Toast.makeText(MainActivity.this,"Please check your credentials!",Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility((View.GONE));

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {

                            }

                        });
                                    progressBar.setVisibility((View.GONE));
                                }


                    else {
                        user.sendEmailVerification();
                        Toast.makeText(MainActivity.this,"Check your email to verify your account",Toast.LENGTH_LONG).show();
                        progressBar.setVisibility((View.GONE));
                    }
                } else {
                    Toast.makeText(MainActivity.this,"Please check your credentials!",Toast.LENGTH_LONG).show();
                    progressBar.setVisibility((View.GONE));
                }}});



    }
}