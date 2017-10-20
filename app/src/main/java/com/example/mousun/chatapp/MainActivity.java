package com.example.mousun.chatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.text.format.DateFormat;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {

    private static int SIGN_IN_REQUEST_CODE = 1;
    private FirebaseListAdapter<ChatMessage> adapter;

    RelativeLayout mActivity_main;
    FloatingActionButton mFab;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_sign_out){
            AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Snackbar.make(mActivity_main, "You are signed out", Snackbar.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
        return true;
    }

    //log out button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SIGN_IN_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                Snackbar.make(mActivity_main, "Successfully singed in. Welcome", Snackbar.LENGTH_SHORT).show();
                displayCharMessage();
            }else{
                Snackbar.make(mActivity_main, " Can not sign in, please try again later" , Snackbar.LENGTH_SHORT).show();
                finish();
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActivity_main = (RelativeLayout) findViewById(R.id.activity_main);
        mFab = (FloatingActionButton) findViewById(R.id.fab);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText mInput = (EditText) findViewById(R.id.input);
                FirebaseDatabase.getInstance().getReference().push().setValue(new ChatMessage(mInput.getText().toString(),
                        FirebaseAuth.getInstance().getCurrentUser().getEmail()));
                mInput.setText("");
            }
        });
        //if not sign-in, navigate to sign-in page
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_REQUEST_CODE);
        }else {
            Snackbar.make(mActivity_main,"Welcome" + FirebaseAuth.getInstance().getCurrentUser().getEmail(), Snackbar.LENGTH_SHORT).show();
        }

        //load content
        displayCharMessage();
    }

    private void displayCharMessage(){
        ListView mListOfMessage = (ListView) findViewById(R.id.list_of_massage);
        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class, R.layout.list_item,
                FirebaseDatabase.getInstance().getReference()) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                TextView mMessageText, mMessageUser, mMessageTime;

                mMessageText = (TextView) findViewById(R.id.message_text);
                mMessageUser = (TextView) findViewById(R.id.message_user);
                mMessageTime = (TextView) findViewById(R.id.message_time);

                mMessageText.setText(model.getMessageText());
                mMessageUser.setText(model.getMessageUser());
                mMessageTime.setText(DateFormat.format("dd-mm-yyyy (HH:mm:ss)", model.getMessageTime()));
            }
        };
        mListOfMessage.setAdapter(adapter);

    }

}
