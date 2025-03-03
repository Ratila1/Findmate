package com.ratila.findmate;

import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private List<ChatItem> chatList;
    private OnChatClickListener listener;

    public interface OnChatClickListener {
        void onChatClick(String chatId);
    }

    public ChatAdapter(List<ChatItem> chatList, OnChatClickListener listener) {
        this.chatList = chatList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatItem chat = chatList.get(position);
        holder.name.setText(chat.getName());

        // Устанавливаем текст сообщения
        String lastMessage = chat.getLastMessage();
        if (lastMessage != null && !lastMessage.isEmpty()) {
            holder.lastMessage.setText(lastMessage);
        } else {
            holder.lastMessage.setText("No messages yet");
        }

        // Устанавливаем время сообщения
        long messageTime = chat.getMessageTime();
        if (messageTime > 0) {
            String formattedTime = DateFormat.format("HH:mm", messageTime).toString();
            holder.messageTime.setText(formattedTime);
        } else {
            holder.messageTime.setText("Unknown time");
        }

        // Контейнер сообщения
        LinearLayout messageContainer = holder.itemView.findViewById(R.id.message_container);
        if (messageContainer != null) {
            ViewGroup.LayoutParams params = messageContainer.getLayoutParams();
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT; // Делаем контейнер по размеру сообщения
            messageContainer.setLayoutParams(params);

            if (chat.isUserMessage()) {
                messageContainer.setBackgroundResource(R.drawable.message_background_user);
                ((LinearLayout.LayoutParams) messageContainer.getLayoutParams()).gravity = Gravity.END;
            } else {
                messageContainer.setBackgroundResource(R.drawable.message_background_other);
                ((LinearLayout.LayoutParams) messageContainer.getLayoutParams()).gravity = Gravity.START;
            }
        }

        // Обработка клика
        holder.itemView.setOnClickListener(v -> listener.onChatClick(chat.getChatId()));
    }


    @Override
    public int getItemCount() {
        return chatList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, lastMessage, messageTime;
        ImageView avatar;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.chat_name);
            lastMessage = itemView.findViewById(R.id.last_message);
            messageTime = itemView.findViewById(R.id.message_time);
            avatar = itemView.findViewById(R.id.chat_avatar);
        }
    }
}