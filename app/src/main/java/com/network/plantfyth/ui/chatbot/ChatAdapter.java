package com.network.plantfyth.ui.chatbot;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.network.plantfyth.R;
import com.network.plantfyth.model.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private List<ChatMessage> mensagens;

    public ChatAdapter(List<ChatMessage> mensagens) {
        this.mensagens = mensagens;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage msg = mensagens.get(position);
        holder.txtMensagem.setText(msg.getTexto());

        if (msg.isUsuario()) {
            holder.txtMensagem.setBackgroundResource(R.drawable.bg_message_user);
            holder.txtMensagem.setTextColor(0xFFFFFFFF);
            ((LinearLayout) holder.itemView).setGravity(Gravity.END);
        } else {
            holder.txtMensagem.setBackgroundResource(R.drawable.bg_message_bot);
            holder.txtMensagem.setTextColor(0xFF212121);
            ((LinearLayout) holder.itemView).setGravity(Gravity.START);
        }
    }

    @Override
    public int getItemCount() { return mensagens.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtMensagem;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMensagem = itemView.findViewById(R.id.txtMensagem);
        }
    }
}