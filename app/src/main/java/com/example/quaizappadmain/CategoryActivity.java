package com.example.quaizappadmain;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryActivity extends AppCompatActivity {
    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    private RecyclerView recyclerView;
    private Dialog loadingDialog, categoryDialog;


    private CircleImageView addImage;
    private EditText categoryname;
    private Button addBtn;
    private Uri image;
    private String downloadUrl;
    private List<CategoryModel> list;
    private CategoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Toolbar toolbar = findViewById(R.id.toolbar);


        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Categories");
         //getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.round_cornner));
        }
        loadingDialog.setCancelable(false);


        setCategoryDialog();


        recyclerView = findViewById(R.id.rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);


        list = new ArrayList<>();
        adapter = new CategoryAdapter(list, new CategoryAdapter.DeleteListener() {
            @Override
            public void onDelete(String Key, int position) {


                new AlertDialog.Builder(CategoryActivity.this, R.style.Theme_AppCompat_Light_Dialog)
                        .setTitle("Delete Category")
                        .setMessage("Are you sure,you want to delete this category?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                myRef.child("Categories").child(Key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            myRef.child("SETS").child(list.get(position).getName()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        list.remove(position);
                                                        adapter.notifyDataSetChanged();
                                                    }else {
                                                        Toast.makeText(CategoryActivity.this, "failed to delete", Toast.LENGTH_SHORT).show();
                                                    }
                                                    loadingDialog.dismiss();
                                                }
                                            });


                                        } else {
                                            Toast.makeText(CategoryActivity.this, "failed to delete", Toast.LENGTH_SHORT).show();
                                            loadingDialog.dismiss();
                                        }
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

        loadingDialog.show();

        myRef.child("Categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    //   list.add(snapshot1.getValue(CategoryModel.class));

                    list.add(new CategoryModel(snapshot1.child("name").getValue().toString(),
                            Integer.parseInt(snapshot1.child("sets").getValue().toString()),
                            snapshot1.child("url").getValue().toString(),
                            snapshot1.getKey()
                    ));


                }
                adapter.notifyDataSetChanged();
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CategoryActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.icon_add) {
            categoryDialog.show();

            Toast.makeText(this, "dialog", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }


    private void setCategoryDialog() {

        categoryDialog = new Dialog(this);
        categoryDialog.setContentView(R.layout.add_category_dialog);
        categoryDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            categoryDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_box));
        }
        categoryDialog.setCancelable(true);


        addImage = categoryDialog.findViewById(R.id.circle_image);
        categoryname = categoryDialog.findViewById(R.id.edit_category_name);
        addBtn = categoryDialog.findViewById(R.id.btn_add);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, 110);
            }
        });


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (categoryname.getText() == null) {
                    categoryname.setError("Required");
                    return;
                }
               for (CategoryModel model : list) {
                 if (categoryname.getText().toString().equals(model.getName())){
                       categoryname.setError("Category name already present!");
                       return;
                   }
               }
                if (image == null) {
                    Toast.makeText(CategoryActivity.this, "please select your image", Toast.LENGTH_SHORT).show();
                    return;
                }

                categoryDialog.dismiss();
                uploadData();
                //  uplode data
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 110) {
            if (requestCode == RESULT_OK) {
                image = data.getData();
                addImage.setImageURI(image);
            }
        }
    }

    private void uploadData() {
        loadingDialog.show();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference imageReferance = storageReference.child("categories").child(image.getLastPathSegment());

        UploadTask uploadTask = imageReferance.putFile(image);
        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();

                }

                return imageReferance.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            downloadUrl = task.getResult().toString();
                            uploadCategoryName();

                        } else {
                            loadingDialog.dismiss();
                            Toast.makeText(CategoryActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUrl = task.getResult();

                } else {
                    Toast.makeText(CategoryActivity.this, "SomeThing went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadCategoryName() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", categoryname.getText().toString());
        map.put("sets", 0);
        map.put("url", downloadUrl);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference().child("Categories").child("category" + (list.size() + 1)).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    list.add(new CategoryModel(categoryname.getText().toString(), 0, downloadUrl, "category" + (list.size() + 1)));
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(CategoryActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
                loadingDialog.dismiss();
            }
        });
    }
}