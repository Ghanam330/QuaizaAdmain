package com.example.quaizappadmain;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {
    private List<QuestionModel> list;
    private String category;
    private DeleteListener listener;

    public QuestionAdapter(List<QuestionModel> list, String category, DeleteListener listener) {
        this.category = category;
        this.list = list;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quaistion_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionAdapter.ViewHolder holder, int position) {
        String question = list.get(position).getQuastion();
        String answer = list.get(position).getAnswer();
        holder.setData(question, answer, position);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView quastion, answer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            quastion = itemView.findViewById(R.id.quaistion);
            answer = itemView.findViewById(R.id.answer);
        }

        private void setData(String quastion, String answer, int position) {
            this.quastion.setText(position + 1 + "." + quastion);
            this.answer.setText("Ans ." + answer);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent editIntent = new Intent(itemView.getContext(), AddQuestionActivity.class);

                    Toast.makeText(itemView.getContext(), "welcom", Toast.LENGTH_SHORT).show();

                    editIntent.putExtra("SetNo", list.get(position).getSet());
                    editIntent.putExtra("CategoryName", category);
                    editIntent.putExtra("position", position);
                    itemView.getContext().startActivity(editIntent);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onLongClick(position, list.get(position).getId());
                    return false;
                }
            });
        }
    }

    public interface DeleteListener {
        void onLongClick(int position, String id);
    }
}
