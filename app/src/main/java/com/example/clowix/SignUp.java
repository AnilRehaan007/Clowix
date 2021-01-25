package com.example.clowix;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;

import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


public class SignUp extends Fragment implements Authentication.Confirmation {

    private static final String TAG = "SignUp";

 private static View outerView;
 private EditText user_name,user_password;
 private String name,password;
 private Button sign_up;
 private TextInputLayout user,pass;
 private boolean isFillEmail=false;
 private boolean isFillPassword=false;

    public SignUp() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Login_SignUp.getImageView().setVisibility(View.GONE);
         outerView=view;
         user_name=view.findViewById(R.id.email_id);
         user_password=view.findViewById(R.id.editPassword);
         sign_up=view.findViewById(R.id.sign_up_button);
         user=view.findViewById(R.id.enter_user_email);
         pass=view.findViewById(R.id.pass);

         sign_up.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 if(user_name.getText().toString().length()==0)
                 {

                     user.setError("Enter Your Email");
                     isFillEmail=false;

                 }
                 else
                 {
                     user.setError(null);
                     isFillEmail=true;
                     name=user_name.getText().toString();
                 }
                 if(user_password.getText().toString().length()==0)
                 {
                     pass.setError("Enter Password");
                     isFillPassword=false;
                 }
                 else
                 {
                     pass.setError(null);
                     isFillPassword=true;
                     password=user_password.getText().toString();
                 }

                 if(isFillEmail)
                 {

                     if(!emailPatternChecker(name))
                     {

                         user.setError("Invalid Email Address");
                     }
                     else
                     {
                         user.setError(null);

                     }


                 }

                 if(isFillEmail && isFillPassword && emailPatternChecker(name))
                 {

                     if(isNetworkConnected())
                     {
                      sign_up.setEnabled(false);

                         new Thread(new Authentication(SignUp.this,Authentication.Status.Sign_Up,name,password)).start();
                         Login_SignUp.getImageView().setVisibility(View.VISIBLE);
                     }
                     else
                     {
                         DialogShower dialogShower=new DialogShower(R.layout.internet_error,R.style.translate_animator,getContext());
                         dialogShower.showDialog();

                     }

                 }

             }
         });





    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }


    
    private boolean emailPatternChecker(String email)
    {
            return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    public static boolean data_fill_confirmation()
    {
       if(outerView!=null)
       {
           return ((EditText)outerView.findViewById(R.id.email_id)).getText().toString().length()!=0
                   || ((EditText)outerView.findViewById(R.id.editPassword)).getText().toString().length()!=0;
       }
       else return false;
    }

    @Override
    public void confirmation(Task<AuthResult> task, Authentication.Status status) {

        Login_SignUp.getImageView().setVisibility(View.GONE);
        sign_up.setEnabled(true);

       if(task.getException() instanceof FirebaseAuthUserCollisionException)
       {

           DialogShower dialogShower=new DialogShower(R.layout.email_already_exits,R.style.translate_animator,getContext());
           dialogShower.showDialog();

       }
       else if(task.getException() instanceof FirebaseAuthWeakPasswordException)
       {

           Log.d(TAG, "confirmation: error: " + task.getException());
           pass.setError("Week Password");


       }

       else
       {
           Dialog dialog=new Dialog(getContext(),R.style.translate_animator);
           dialog.setContentView(R.layout.user_created_dialog);
           dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
           dialog.setCanceledOnTouchOutside(false);
           Window window=dialog.getWindow();
           window.setGravity(Gravity.CENTER);
           dialog.show();

           Timer timer=new Timer();
           timer.schedule(new TimerTask() {
               @Override
               public void run() {
                   dialog.dismiss();
                   Intent intent=new Intent(getContext(),MainFrame.class);
                   intent.putExtra(MainFrame.user_email_id,name.replaceAll("\\.",""));
                   Objects.requireNonNull(getContext()).startActivity(intent);
                   timer.cancel();
               }
           },1000);
       }

    }
}