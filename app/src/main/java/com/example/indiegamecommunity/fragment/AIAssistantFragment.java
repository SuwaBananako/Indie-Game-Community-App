package com.example.indiegamecommunity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.indiegamecommunity.R;
import com.example.indiegamecommunity.adapter.ChatMessageAdapter;
import com.example.indiegamecommunity.model.ChatMessage;
import com.example.indiegamecommunity.utils.AIAssistant;

import java.util.ArrayList;
import java.util.List;

public class AIAssistantFragment extends Fragment {
    private RecyclerView rvMessages;
    private EditText etMessage;
    private Button btnSend, btnRecommend, btnReply, btnContent;
    private ChatMessageAdapter chatMessageAdapter;
    private List<ChatMessage> messageList;
    private AIAssistant aiAssistant;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ai_assistant, container, false);

        rvMessages = view.findViewById(R.id.rvMessages);
        etMessage = view.findViewById(R.id.etMessage);
        btnSend = view.findViewById(R.id.btnSend);
        btnRecommend = view.findViewById(R.id.btnRecommend);
        btnReply = view.findViewById(R.id.btnReply);
        btnContent = view.findViewById(R.id.btnContent);

        aiAssistant = new AIAssistant();
        messageList = new ArrayList<>();

        rvMessages.setLayoutManager(new LinearLayoutManager(getContext()));
        chatMessageAdapter = new ChatMessageAdapter(getContext(), messageList);
        rvMessages.setAdapter(chatMessageAdapter);

        addAiMessage("你好！我是独立游戏社区的AI助手，有什么可以帮你的吗？");

        btnSend.setOnClickListener(v -> {
            String message = etMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                addUserMessage(message);
                String reply = aiAssistant.chatWithAI(message);
                addAiMessage(reply);
                etMessage.setText("");
            }
        });

        btnRecommend.setOnClickListener(v -> {
            String recommendation = aiAssistant.getGameRecommendation("");
            addAiMessage("🎮 游戏推荐：\n" + recommendation);
        });

        btnReply.setOnClickListener(v -> {
            String suggestion = aiAssistant.getReplySuggestion("");
            addAiMessage("💡 回复建议：\n" + suggestion);
        });

        btnContent.setOnClickListener(v -> {
            String suggestion = aiAssistant.getContentSuggestion("");
            addAiMessage("📝 内容建议：\n" + suggestion);
        });

        return view;
    }

    private void addUserMessage(String message) {
        messageList.add(new ChatMessage(ChatMessage.TYPE_USER, message));
        chatMessageAdapter.notifyItemInserted(messageList.size() - 1);
        rvMessages.scrollToPosition(messageList.size() - 1);
    }

    private void addAiMessage(String message) {
        messageList.add(new ChatMessage(ChatMessage.TYPE_AI, message));
        chatMessageAdapter.notifyItemInserted(messageList.size() - 1);
        rvMessages.scrollToPosition(messageList.size() - 1);
    }
}