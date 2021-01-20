package com.example.quaizappadmain;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private final List<CategoryModel> categoryModels;
    public DeleteListener deleteListener;

    public CategoryAdapter(List<CategoryModel> categoryModels,DeleteListener deleteListener) {
        this.categoryModels = categoryModels;
        this.deleteListener=deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.setDate(categoryModels.get(position).getUrl(), categoryModels.get(position).getName(), categoryModels.get(position).getSets(),categoryModels.get(position).getKey(),position);

    }

    @Override
    public int getItemCount() {
        return categoryModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private  CircleImageView imageView;
        private  TextView title;
        private  ImageButton delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image_view);
            title = itemView.findViewById(R.id.title);
            delete=itemView.findViewById(R.id.delete);
        }

        private void setDate(String url, final String title, final int sets,final String Key,final int position) {
            Glide.with(itemView.getContext()).load(url).into(imageView);
            this.title.setText(title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent setIntent = new Intent(itemView.getContext(), SetsActivity.class);
                    setIntent.putExtra("title", title);
                    setIntent.putExtra("sets", sets);
                    setIntent.putExtra("Key", Key);
                    itemView.getContext().startActivity(setIntent);
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteListener.onDelete(Key,position);
                }
            });
        }
    }
    public interface DeleteListener{
        public void onDelete(String Key,int position);
    }
}
