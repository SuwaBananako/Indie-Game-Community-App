package com.example.indiegamecommunity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.indiegamecommunity.R;
import com.example.indiegamecommunity.adapter.PostAdapter;
import com.example.indiegamecommunity.database.DatabaseHelper;
import com.example.indiegamecommunity.model.Post;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryDetailActivity extends AppCompatActivity {
    private TextView tvCategoryTitle, tvCategoryDesc, tvPostCount, tvEmpty;
    private ImageView ivCategoryIcon;
    private Button btnCreatePost, btnBack;
    private RecyclerView rvCategoryPosts;
    private DatabaseHelper databaseHelper;
    private String categoryName;
    private PostAdapter postAdapter;

    private static class CategoryInfo {
        int iconRes;
        int bgColor;
        String desc;
    }

    private static final Map<String, CategoryInfo> CATEGORY_INFO = new HashMap<>();

    static {
        CategoryInfo gameRecommend = new CategoryInfo();
        gameRecommend.iconRes = R.drawable.icon_game;
        gameRecommend.bgColor = 0xFFFD4C5C;
        gameRecommend.desc = "面向独立游戏开发者、玩家的游戏推荐专区，支持分享单机/手游/端游/自制小游戏。可上传游戏截图、游玩评测、玩法解析、优缺点测评，支持图文、短视频帖子。标签分类：像素、RPG、解谜、ACT、休闲独立游戏。支持收藏、打分、求同类型游戏推荐。";
        CATEGORY_INFO.put("游戏推荐", gameRecommend);

        CategoryInfo devLog = new CategoryInfo();
        devLog.iconRes = R.drawable.icon_dev;
        devLog.bgColor = 0xFFE03A4A;
        devLog.desc = "独立游戏开发者记录开发进度专属板块，支持图文记录工程进度、版本迭代、踩坑记录、功能实现思路、项目排期。可上传代码截图、工程界面、半成品Demo演示。标签：Unity、Android、Godot、美术制作、程序bug。允许互相交流开发难题。";
        CATEGORY_INFO.put("开发日志", devLog);

        CategoryInfo gameArt = new CategoryInfo();
        gameArt.iconRes = R.drawable.icon_art;
        gameArt.bgColor = 0xFFFF7B88;
        gameArt.desc = "游戏美术素材交流专区，包含角色原画、场景立绘、像素画、UI界面、图标、特效、配色方案、建模贴图。可分享原创美术作品、绘画过程、素材资源、临摹练习。支持求约稿、求配色参考、求绘画教程。面向原画、建模、UI美术从业者。";
        CATEGORY_INFO.put("游戏美术", gameArt);

        CategoryInfo gameMusic = new CategoryInfo();
        gameMusic.iconRes = R.drawable.icon_music;
        gameMusic.bgColor = 0xFFFF8C42;
        gameMusic.desc = "游戏音频交流板块，分享BGM背景音乐、战斗音效、角色配音、纯原创游戏配乐、音效素材。支持上传音频片段、编曲工程截图。交流FL Studio、编曲思路、免费商用音源资源。可求适配游戏风格的配乐、音效参考。";
        CATEGORY_INFO.put("游戏音乐", gameMusic);

        CategoryInfo industryNews = new CategoryInfo();
        industryNews.iconRes = R.drawable.icon_news;
        industryNews.bgColor = 0xFFFFC107;
        industryNews.desc = "游戏行业资讯专区，同步版号政策、引擎新版本更新、行业展会、厂商动态、市场数据分析、新人入行指南、外包行情、薪资科普。干货向客观资讯，无娱乐八卦，面向想入行的游戏开发者、美术、程序。";
        CATEGORY_INFO.put("行业资讯", industryNews);

        CategoryInfo events = new CategoryInfo();
        events.iconRes = R.drawable.icon_event;
        events.bgColor = 0xFFFD4C5C;
        events.desc = "游戏创作赛事、线上线下活动聚合板块，收录游戏开发GameJam、美术绘画比赛、配乐大赛、社区投稿活动、线下沙龙、众筹活动。可查看赛事规则、参赛作品展示、获奖名单。支持组队报名、招募队友。";
        CATEGORY_INFO.put("活动赛事", events);

        CategoryInfo qna = new CategoryInfo();
        qna.iconRes = R.drawable.icon_qna;
        qna.bgColor = 0xFFFF7B88;
        qna.desc = "游戏开发全品类问答互助板块，程序bug、美术绘画、配乐编曲、引擎操作、接单版权、上架发行、设备配置等问题均可发帖提问。支持附图、附代码片段。老开发者回复解答，可悬赏提问、采纳优质回答。";
        CATEGORY_INFO.put("问答求助", qna);

        CategoryInfo casualChat = new CategoryInfo();
        casualChat.iconRes = R.drawable.icon_chat;
        casualChat.bgColor = 0xFFFF8C42;
        casualChat.desc = "社区休闲水帖板块，无硬性创作主题，游戏日常、摸鱼碎碎念、生活分享、脑洞游戏构思、兴趣闲聊、交友组队、资源交换。轻松无压力，不限制专业内容，仅禁止违规广告。休闲轻松论坛分区。";
        CATEGORY_INFO.put("闲聊灌水", casualChat);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);

        tvCategoryTitle = findViewById(R.id.tvCategoryTitle);
        tvCategoryDesc = findViewById(R.id.tvCategoryDesc);
        tvPostCount = findViewById(R.id.tvPostCount);
        tvEmpty = findViewById(R.id.tvEmpty);
        ivCategoryIcon = findViewById(R.id.ivCategoryIcon);
        btnCreatePost = findViewById(R.id.btnCreatePost);
        btnBack = findViewById(R.id.btnBack);
        rvCategoryPosts = findViewById(R.id.rvCategoryPosts);

        categoryName = getIntent().getStringExtra("category_name");
        if (categoryName == null) categoryName = "游戏推荐";

        CategoryInfo info = CATEGORY_INFO.get(categoryName);
        if (info != null) {
            tvCategoryTitle.setText(categoryName);
            tvCategoryDesc.setText(info.desc);
            ivCategoryIcon.setImageResource(info.iconRes);
            ivCategoryIcon.setColorFilter(info.bgColor);
        }

        databaseHelper = new DatabaseHelper(this);
        rvCategoryPosts.setLayoutManager(new LinearLayoutManager(this));
        loadPosts();

        btnCreatePost.setOnClickListener(v -> {
            Intent intent = new Intent(this, PostEditActivity.class);
            intent.putExtra("category", categoryName);
            startActivity(intent);
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadPosts() {
        List<Post> posts = databaseHelper.getPostsByCategory(categoryName);
        if (posts.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvCategoryPosts.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvCategoryPosts.setVisibility(View.VISIBLE);
        }
        tvPostCount.setText("共 " + posts.size() + " 篇帖子");

        postAdapter = new PostAdapter(this, posts, new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Post post) {
                Intent intent = new Intent(CategoryDetailActivity.this, PostDetailActivity.class);
                intent.putExtra("post_id", post.getId());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(Post post) {
                showPostOptionsDialog(post);
            }
        });
        rvCategoryPosts.setAdapter(postAdapter);
    }

    private void showPostOptionsDialog(Post post) {
        String[] options = {"查看详情", "编辑帖子", "删除帖子"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择操作");
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    Intent intent = new Intent(this, PostDetailActivity.class);
                    intent.putExtra("post_id", post.getId());
                    startActivity(intent);
                    break;
                case 1:
                    Intent editIntent = new Intent(this, PostEditActivity.class);
                    editIntent.putExtra("post_id", post.getId());
                    startActivity(editIntent);
                    break;
                case 2:
                    showDeleteConfirmDialog(post);
                    break;
            }
        });
        builder.show();
    }

    private void showDeleteConfirmDialog(Post post) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("确认删除");
        builder.setMessage("确定要删除标题为 \"" + post.getTitle() + "\" 的帖子吗？");
        builder.setPositiveButton("删除", (dialog, which) -> {
            if (databaseHelper.deletePostByTitle(post.getTitle())) {
                Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
                loadPosts();
            } else {
                Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPosts();
    }
}
