package tn.esprit.lssafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private EditText emailText ;
    private Button resetPassword ;
    private ProgressBar progressBar ;
    FirebaseAuth auth ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);


        emailText = (EditText) findViewById(R.id.email);
        resetPassword = (Button) findViewById(R.id.reset);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        auth=FirebaseAuth.getInstance();



        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPas();

            }
        });
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
                hideKeyboard(ForgotPassword.this);
        }
        return super.dispatchTouchEvent(ev);
    }

    public static void hideKeyboard(ForgotPassword activity) {
        if (activity != null && activity.getWindow() != null
                && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView()
                    .getWindowToken(), 0);
        }
    }

    private void resetPas() {
        String email = emailText.getText().toString().trim();
        if (email.isEmpty()) {
            emailText.setError("Email is required!");
            emailText.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("Please provide a valid email!");
            emailText.requestFocus();
            return;
        }

        //progressBar.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.VISIBLE);
                if(task.isSuccessful()){
                    progressBar.setVisibility((View.GONE));
                    Toast.makeText(ForgotPassword.this,"Check your email to reset password!",Toast.LENGTH_LONG).show();

                }else{
                    progressBar.setVisibility((View.GONE));
                    Toast.makeText(ForgotPassword.this,"Try again! something wrong happend!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}