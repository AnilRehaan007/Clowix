package com.example.clowix;


import android.app.AlertDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.util.Objects;


public class Login extends Fragment implements Authentication.Confirmation {

    private static final String TAG = "Login";

     private EditText user_name,password,email_id;
     private Button sign_in,forgot_password;
     private  TextInputLayout user,pass,enter_user_email;
     private boolean isFillEmail=false;
     private boolean isFillPassword=false;
     private String user_email;
     private String user_password;
     private static View outerView;

    public Login() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Login_SignUp.getImageView().setVisibility(View.GONE);
        outerView=view;
        animationApplier(view);
        user_name=view.findViewById(R.id.email_id);
        password=view.findViewById(R.id.editPassword);
        sign_in=view.findViewById(R.id.sign_up_button);
        forgot_password=view.findViewById(R.id.forgot_password);
        user=view.findViewById(R.id.enter_user_email);
        pass=view.findViewById(R.id.password);


        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dialog reset_password_box=new Dialog(getContext(),R.style.translate_animator);
                reset_password_box.setContentView(R.layout.reset_password_dialog);
                reset_password_box.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                reset_password_box.setCanceledOnTouchOutside(true);
                Window window=reset_password_box.getWindow();
                window.setGravity(Gravity.CENTER);
                reset_password_box.show();

                enter_user_email=reset_password_box.findViewById(R.id.enter_user_email);
                email_id=reset_password_box.findViewById(R.id.email_id);
                FloatingActionButton reset=reset_password_box.findViewById(R.id.reset);

                reset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                      if(email_id.getText().toString().length()==0)
                      {
                          enter_user_email.setError("Enter Your Email");
                      }
                      else if(!emailPatternChecker(email_id.getText().toString()))
                      {
                          enter_user_email.setError("Invalid Email Address");
                      }
                     else
                      {
                          enter_user_email.setError(null);
                          user_email=email_id.getText().toString();
                          reset_password_box.dismiss();
                          new Thread(new Authentication(Login.this,Authentication.Status.Forgot_password,email_id.getText().toString()
                                  ,"user_password")).start();
                          Login_SignUp.getImageView().setVisibility(View.VISIBLE);
                      }

                    }
                });

            }
        });
        sign_in.setOnClickListener(new View.OnClickListener() {
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
                    user_email=user_name.getText().toString();
                }
                 if(password.getText().toString().length()==0)
                {
                    pass.setError("Enter Password");
                    isFillPassword=false;
                }
                 else
                 {
                     pass.setError(null);
                     isFillPassword=true;
                     user_password=password.getText().toString();
                 }

              if(isFillEmail)
              {

                   if(!emailPatternChecker(user_email))
                   {

                      user.setError("Invalid Email Address");
                   }
                   else
                   {
                       user.setError(null);

                   }


              }

              if(isFillEmail && isFillPassword && emailPatternChecker(user_email))
              {

                  if(isNetworkConnected())
                  {
                      sign_in.setEnabled(false);

                      new Thread(new Authentication(Login.this,Authentication.Status.Sign_in,user_email,user_password)).start();
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
        ConnectivityManager cm = (ConnectivityManager) Objects.requireNonNull(getContext()).getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }


    private boolean emailPatternChecker(String email)
    {

        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());

    }
    private void animationApplier(View view)
    {
        Button sign_in=view.findViewById(R.id.sign_up_button);
        Button forgot_password=view.findViewById(R.id.forgot_password);

          user=view.findViewById(R.id.enter_user_email);
           pass=view.findViewById(R.id.password);

        user.setTranslationY(800);
        pass.setTranslationY(800);
        sign_in.setTranslationY(800);
        forgot_password.setTranslationY(800);
        user.setAlpha(0);
        pass.setAlpha(0);
        sign_in.setAlpha(0);
        forgot_password.setAlpha(0);
        user.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(300).start();
        pass.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(500).start();
        forgot_password.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(500).start();
        sign_in.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(700).start();

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

        sign_in.setEnabled(true);


       if(status== Authentication.Status.All)
       {
           if(task.getException() instanceof FirebaseAuthInvalidCredentialsException)
           {
               DialogShower dialogShower=new DialogShower(R.layout.invalid_password,R.style.translate_animator,getContext());
               dialogShower.showDialog();
           }
           else if(task.getException() instanceof FirebaseAuthInvalidUserException)
           {
               DialogShower dialogShower=new DialogShower(R.layout.invalid_user,R.style.translate_animator,getContext());
               dialogShower.showDialog();

           }

           else if(task.getException() instanceof FirebaseTooManyRequestsException)
           {
               DialogShower dialogShower=new DialogShower(R.layout.invalid_password,R.style.translate_animator,getContext());
               dialogShower.showDialog();
           }

           else
           {
               Toast.makeText(getContext(),"successfully login",Toast.LENGTH_LONG).show();
               Intent intent=new Intent(getContext(),MainFrame.class);
               intent.putExtra(MainFrame.user_email_id,user_email.replaceAll("\\.",""));
               Objects.requireNonNull(getContext()).startActivity(intent);
           }

       }
     else
       {
           if(task.getException() instanceof FirebaseAuthInvalidCredentialsException)
           {
               FirebaseAuth.getInstance().sendPasswordResetEmail(user_email)
                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               if (task.isSuccessful()) {

                                   AlertDialog.Builder builder=new AlertDialog.Builder(getContext())
                                           .setMessage("Link has been send to "+user_email+" click on it to reset password").setNegativeButton("OK", (dialogInterface, i) -> {

                                           });
                                   builder.setCancelable(false);
                                   builder.show();
                               }
                           }
                       });


           }
           else if(task.getException() instanceof FirebaseAuthInvalidUserException)
           {
               DialogShower dialogShower=new DialogShower(R.layout.invalid_user,R.style.translate_animator,getContext());
               dialogShower.showDialog();

           }

           else if(task.getException() instanceof FirebaseTooManyRequestsException)
           {
              Toast.makeText(getContext(),"Too many request attempt",Toast.LENGTH_LONG).show();
           }

           else
           {
               FirebaseAuth.getInstance().sendPasswordResetEmail(user_email)
                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               if (task.isSuccessful()) {

                                   AlertDialog.Builder builder=new AlertDialog.Builder(getContext())
                                           .setMessage("Link has been send to "+user_email+" click on it to reset password").setNegativeButton("OK", (dialogInterface, i) -> {

                                           });
                                   builder.setCancelable(false);
                                   builder.show();
                               }
                           }
                       });
           }

       }

    }
}