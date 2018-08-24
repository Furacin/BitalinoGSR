package com.furazin.android.mbandgsr;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by manza on 04/06/2017.
 */

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String TAG;
    String email, password;
    EditText editTextEmail, editTextPassword;
    Button bt_conectar;

    // Variable para recordar las credenciales del usuario
    private SharedPreferences sharedPref;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_2);

        progressDialog = new ProgressDialog(Login.this,
                R.style.loginDialogStyle);

        // Instanciamos una referencia al Contexto
        Context context = this.getApplicationContext();
        //Instanciamos el objeto SharedPrefere  nces y creamos un fichero Privado bajo el
        //nombre definido con la clave preference_file_key en el fichero string.xml
        sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        bt_conectar = (Button) findViewById(R.id.bt_conectar2);
        bt_conectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextEmail = (EditText) findViewById(R.id.edittext_email2);
                email = editTextEmail.getText().toString();

                editTextPassword = (EditText) findViewById(R.id.edittext_password2);
                password = editTextPassword.getText().toString();

                if (!validate()) {
                    onLoginFailed();
                    return;
                }

                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Autenticando...");
                progressDialog.show();

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                // On complete call either onLoginSuccess or onLoginFailed
                                onLoginSuccess();
                                // onLoginFailed();
//                                progressDialog.dismiss();
                            }
                        }, 3000);
                iniciarSesion(email,password);
            }
        });

        bt_conectar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                    bt_conectar.setAlpha(0.2f);
                }
                if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                    bt_conectar.setAlpha(1f);
                }
                return false;
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    private void iniciarSesion(String email, String pass) {
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(Login.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(Login.this, "Login correcto!",
                                    Toast.LENGTH_SHORT).show();
                            saveProfile();
                            Intent i = new Intent(Login.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//
//    }

    public void onLoginSuccess() {
        progressDialog.hide();
        //finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Fallo con los campos introducidos", Toast.LENGTH_LONG).show();
    }

    ////////////////////////////////////////////////////////////////////////////

    private void saveProfile() {
        //Capturamos en una variable de tipo String
        String input_email = editTextEmail.getText().toString();
        String input_password = editTextPassword.getText().toString();

        //Instanciamos un objeto del SharedPreferences.Editor
        //el cual nos permite almacenar con su metodo putString
        //los 4 valores del perfil profesional asociandolos a una
        //clave la cual definimos como un string en el fichero strings.xml
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.password_key), input_password);
        editor.putString(getString(R.string.email_key), input_email);

        //NOTA: En el caso de que necesitemos gauirdar un valor numerico podeis usar
        //el metodo putInt en vez del putString.

        //Con el mtodo commit logramos guardar los datos en el fichero
        //de preferncias compartidas de nombre cuyo nombre se defini en
        // el String preference_file_key
        editor.commit();

        //Notificamos la usuario de que se han guardado los datos del perfil correctamente.
        //Toast.makeText(getApplicationContext(),getString(R.string.msg_save), Toast.LENGTH_SHORT).show();

    }

    public boolean validate() {
        boolean valid = true;

        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Introduce una dirección de email válida");
            valid = false;
        } else {
            editTextEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 6 ) {
            editTextPassword.setError("Mínimo 6 caracteres alfanuméricos");
            valid = false;
        } else {
            editTextPassword.setError(null);
        }

        return valid;
    }
}
