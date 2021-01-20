package com.example.quaizappadmain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class QuastionsActivity extends AppCompatActivity {
    private Button add, excel;
    RecyclerView recyclerView;

    private QuestionAdapter adapter;
    public static List<QuestionModel> list;
    private Dialog loadingDialog;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quastions);

        myRef = FirebaseDatabase.getInstance().getReference();
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.round_cornner));
        }
        loadingDialog.setCancelable(false);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String categoryName = getIntent().getStringExtra("category");
        int set = getIntent().getIntExtra("setNo", 1);
        getSupportActionBar().setTitle(categoryName + "/set " + set);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        add = findViewById(R.id.btn_add);
        excel = findViewById(R.id.btn_excel);
        recyclerView = findViewById(R.id.recycler_view);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        list = new ArrayList<>();

        adapter = new QuestionAdapter(list, categoryName, new QuestionAdapter.DeleteListener() {
            @Override
            public void onLongClick(int position, String id) {


                new AlertDialog.Builder(QuastionsActivity.this, R.style.Theme_AppCompat_Light_Dialog)
                        .setTitle("Delete Question")
                        .setMessage("Are you sure,you want to delete this Question?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                loadingDialog.show();
                                myRef.child("SETS").child(categoryName).child("questions").child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {

                                            list.remove(position);
                                            adapter.notifyItemRemoved(position);

                                        } else {
                                            Toast.makeText(QuastionsActivity.this, "failed to delete", Toast.LENGTH_SHORT).show();

                                        }
                                        loadingDialog.dismiss();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("cancel", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();


            }
        });
        recyclerView.setAdapter(adapter);

        getDtata(categoryName, set);


        excel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(QuastionsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                } else {
                    ActivityCompat.requestPermissions(QuastionsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
                }
            }
        });


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addQuestion = new Intent(QuastionsActivity.this, AddQuestionActivity.class);
                addQuestion.putExtra("categoryName", categoryName);
                addQuestion.putExtra("setNo", set);
                startActivity(addQuestion);
            }
        });

    } //                end oncreate


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectFile();
            } else {
                Toast.makeText(this, "please Grant permissions!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void selectFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "SelectFile"), 102);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 102) {
            if (resultCode == RESULT_OK) {
                String filepatch = data.getData().getPath();
                if (filepatch.endsWith(".xlsx")) {
                    Toast.makeText(this, "file selected", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "please choose an Excel file", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private void getDtata(String categoryName, final int set) {
        loadingDialog.show();

        myRef.child("SETS").child(categoryName)
                .child("questions").orderByChild("setNo")
                .equalTo(set).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //  بحط الاسئله في الفاير بيس

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    String id = snapshot1.getKey();
                    String question = snapshot1.child("question").getValue().toString();
                    String a = snapshot1.child("optionA").getValue().toString();
                    String b = snapshot1.child("optionB").getValue().toString();
                    String c = snapshot1.child("optionC").getValue().toString();
                    String d = snapshot1.child("optionD").getValue().toString();
                    String correctANS = snapshot1.child("correctANS").getValue().toString();
                    list.add(new QuestionModel(id, question, a, b, c, d, correctANS, set));
                }
                loadingDialog.dismiss();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(QuastionsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}