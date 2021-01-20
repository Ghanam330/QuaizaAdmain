package com.example.quaizappadmain;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;


public class SetsActivity extends AppCompatActivity {

    private GridView gridView;
    private Dialog loadingDialog;
    private GridAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sets);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.round_cornner));
        }
        loadingDialog.setCancelable(false);

        getSupportActionBar().setTitle(getIntent().getStringExtra("title"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        gridView = findViewById(R.id.gridView);

        adapter = new GridAdapter(getIntent().getIntExtra("sets", 0), getIntent().getStringExtra("title"), new GridAdapter.GridListener() {
            @Override
            public void addSets() {


                FirebaseDatabase database = FirebaseDatabase.getInstance();
                database.getReference()
                        .child("Categories")
                        .child(getIntent().getStringExtra("Key"))
                        .child("sets").setValue(getIntent()
                        .getIntExtra("sets", 0) + 1)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    adapter.sets++;
                                    adapter.notifyDataSetChanged();

                                } else {

                                }
                                loadingDialog.dismiss();
                            }
                        });
            }
        });
        gridView.setAdapter(adapter);


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}