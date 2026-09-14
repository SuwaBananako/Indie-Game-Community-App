package com.example.indiegamecommunity.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class AIAssistant {
    private static final List<String> GAME_RECOMMENDATIONS = Arrays.asList(
            "《星露谷物语》- 一款温馨的农场模拟游戏，适合喜欢休闲经营的玩家",
            "《空洞骑士》- 精美的2D动作冒险游戏，拥有丰富的剧情和挑战",
            "《死亡细胞》- 硬核的roguelike动作游戏，每次死亡都是新的开始",
            "《Hollow Knight》- 神秘的地下世界等待你的探索",
            "《Stardew Valley》- 回归田园生活，打造属于你的农场",
            "《Celeste》- 平台跳跃类神作，讲述关于克服困难的故事",
            "《Darkest Dungeon》- 暗黑风格的地牢探险游戏",
            "《Firewatch》- 第一人称叙事游戏，探索森林中的秘密"
    );

    private static final List<String> REPLY_SUGGESTIONS = Arrays.asList(
            "这个观点很有意思，我也有类似的看法！",
            "感谢分享，期待更多内容！",
            "楼主说得太好了，强烈支持！",
            "这个游戏确实很棒，推荐大家都试试！",
            "分析得很到位，学习了！",
            "期待后续更新，加油！",
            "同感！这是我近期玩过最好的游戏之一！"
    );

    private static final List<String> CONTENT_SUGGESTIONS = Arrays.asList(
            "可以分享一下游戏中的精彩瞬间或攻略技巧",
            "推荐介绍游戏的世界观和故事背景",
            "分享你的游戏心得和体验感受",
            "可以做一个游戏评测或对比分析",
            "介绍游戏中的隐藏内容和解谜攻略",
            "分享游戏截图或视频，让内容更丰富",
            "讨论游戏的更新和未来发展方向"
    );

    private static final List<String> AI_RESPONSES = Arrays.asList(
            "你好！我是独立游戏社区的AI助手，有什么可以帮你的吗？",
            "很高兴为你服务！请问需要游戏推荐还是内容建议？",
            "独立游戏世界丰富多彩，让我来帮你探索吧！",
            "有任何关于独立游戏的问题，随时问我！",
            "欢迎来到独立游戏社区，祝你玩得开心！"
    );

    private Random random;

    public AIAssistant() {
        random = new Random();
    }

    public String getGameRecommendation(String preferences) {
        if (preferences == null || preferences.isEmpty()) {
            return GAME_RECOMMENDATIONS.get(random.nextInt(GAME_RECOMMENDATIONS.size()));
        }

        preferences = preferences.toLowerCase();
        if (preferences.contains("休闲") || preferences.contains("经营") || preferences.contains("农场")) {
            return GAME_RECOMMENDATIONS.get(0);
        } else if (preferences.contains("动作") || preferences.contains("冒险")) {
            return GAME_RECOMMENDATIONS.get(1);
        } else if (preferences.contains("roguelike") || preferences.contains("硬核")) {
            return GAME_RECOMMENDATIONS.get(2);
        } else if (preferences.contains("平台") || preferences.contains("跳跃")) {
            return GAME_RECOMMENDATIONS.get(5);
        } else if (preferences.contains("叙事") || preferences.contains("故事")) {
            return GAME_RECOMMENDATIONS.get(7);
        } else {
            return GAME_RECOMMENDATIONS.get(random.nextInt(GAME_RECOMMENDATIONS.size()));
        }
    }

    public String getReplySuggestion(String postContent) {
        return REPLY_SUGGESTIONS.get(random.nextInt(REPLY_SUGGESTIONS.size()));
    }

    public String getContentSuggestion(String topic) {
        if (topic == null || topic.isEmpty()) {
            return CONTENT_SUGGESTIONS.get(random.nextInt(CONTENT_SUGGESTIONS.size()));
        }
        return "关于\"" + topic + "\"，" + CONTENT_SUGGESTIONS.get(random.nextInt(CONTENT_SUGGESTIONS.size()));
    }

    public String chatWithAI(String message) {
        if (message == null || message.isEmpty()) {
            return AI_RESPONSES.get(random.nextInt(AI_RESPONSES.size()));
        }

        message = message.toLowerCase();
        if (message.contains("推荐") || message.contains("游戏")) {
            return getGameRecommendation(message);
        } else if (message.contains("回复") || message.contains("评论")) {
            return getReplySuggestion(message);
        } else if (message.contains("内容") || message.contains("发帖") || message.contains("建议")) {
            return getContentSuggestion(message);
        } else if (message.contains("你好") || message.contains("hi") || message.contains("hello")) {
            return AI_RESPONSES.get(0);
        } else if (message.contains("谢谢") || message.contains("感谢")) {
            return "不客气！很高兴能帮到你！";
        } else {
            return AI_RESPONSES.get(random.nextInt(AI_RESPONSES.size())) + "\n\n你可以尝试问我：\n- 游戏推荐\n- 回复建议\n- 内容创作建议";
        }
    }
}