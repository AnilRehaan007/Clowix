package com.example.clowix;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Authentication implements Runnable{

    private final Confirmation confirmation;
    private final Status status;
    private final String user_email;
    private final String user_password;

    enum Status
    {
      Sign_in,Sign_Up,Forgot_password,All;

    }
    interface Confirmation
    {

        void confirmation(Task<AuthResult> task,Status status);
    }

    public Authentication(Confirmation confirmation, Status status, String user_email, String user_password) {
        this.confirmation = confirmation;
        this.status = status;
        this.user_email = user_email;
        this.user_password = user_password;
    }

    @Override
    public void run() {

        final FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();

         switch (status)
         {
             case Sign_in:
                 firebaseAuth.signInWithEmailAndPassword(user_email,user_password)
                         .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                             @Override
                             public void onComplete(@NonNull Task<AuthResult> task) {

                                 if(confirmation!=null)
                                 {
                                     confirmation.confirmation(task,Status.All);

                                 }
                             }
                         });
                 break;

             case Sign_Up:

                 firebaseAuth.createUserWithEmailAndPassword(user_email,user_password)
                         .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                             @Override
                             public void onComplete(@NonNull Task<AuthResult> task) {

                                 if(confirmation!=null)
                                 {
                                     confirmation.confirmation(task,Status.All);

                                 }

                             }
                         });

                 break;

             case Forgot_password:

                 firebaseAuth.signInWithEmailAndPassword(user_email,user_password)
                         .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                             @Override
                             public void onComplete(@NonNull Task<AuthResult> task) {

                                 if(confirmation!=null)
                                 {

                                     System.out.println("");

                                     confirmation.confirmation(task,Status.Forgot_password);

                                 }
                             }
                         });
                 break;
         }

    }
}
