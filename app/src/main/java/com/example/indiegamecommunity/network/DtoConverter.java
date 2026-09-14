package com.example.indiegamecommunity.network;

import com.example.indiegamecommunity.model.*;

public class DtoConverter {

    public static Post toPost(PostDto dto) {
        Post p = new Post();
        p.setId((int) dto.getId());
        p.setTitle(dto.getTitle());
        p.setContent(dto.getContent());
        p.setCategory(dto.getCategory());
        p.setCoverImage(dto.getCoverImage());
        p.setContentImages(dto.getContentImages());
        p.setUsername(dto.getUsername());
        p.setLikeCount(dto.getLikeCount());
        p.setCollectCount(dto.getCollectCount());
        p.setStatus(dto.getStatus());
        p.setCreateTime(dto.getCreateTime());
        return p;
    }

    public static Vote toVote(VoteDto dto) {
        Vote v = new Vote();
        v.setId((int) dto.getId());
        v.setGameName(dto.getGameName());
        v.setDescription(dto.getDescription());
        v.setContentImages(dto.getContentImages());
        v.setIconPath(dto.getIconPath());
        v.setBannerPath(dto.getBannerPath());
        v.setAuthorUsername(dto.getAuthorUsername());
        v.setStatus(dto.getStatus());
        v.setVoteCount(dto.getVoteCount());
        v.setCreateTime(dto.getCreateTime());
        return v;
    }

    public static GamePromo toGamePromo(PromoDto dto) {
        GamePromo gp = new GamePromo();
        gp.setId((int) dto.getId());
        gp.setName(dto.getName());
        gp.setDescription(dto.getDescription());
        gp.setScreenshot(dto.getCoverImage());
        gp.setContentImages(dto.getContentImages());
        gp.setAuthorUsername(dto.getAuthorUsername());
        gp.setStatus(dto.getStatus());
        gp.setCreateTime(dto.getCreateTime());
        return gp;
    }

    public static Crowdfunding toCrowdfunding(CFDTo dto) {
        Crowdfunding cf = new Crowdfunding();
        cf.setId((int) dto.getId());
        cf.setName(dto.getName());
        cf.setDescription(dto.getDescription());
        cf.setContentImages(dto.getContentImages());
        cf.setAuthorUsername(dto.getAuthorUsername());
        cf.setTargetAmount(dto.getTargetAmount());
        cf.setCurrentAmount(dto.getCurrentAmount());
        cf.setSupporterCount(dto.getSupporterCount());
        cf.setStatus(dto.getStatus());
        cf.setCreateTime(dto.getCreateTime());
        return cf;
    }

    public static Comment toComment(CommentDto dto) {
        Comment c = new Comment();
        c.setId((int) dto.getId());
        c.setPostId((int) dto.getPostId());
        c.setUsername(dto.getUsername());
        c.setContent(dto.getContent());
        c.setImagePath(dto.getImagePath());
        c.setCreateTime(dto.getCreateTime());
        return c;
    }

    public static PostDto fromPost(Post p) {
        PostDto dto = new PostDto();
        dto.setId(p.getId());
        dto.setTitle(p.getTitle());
        dto.setContent(p.getContent());
        dto.setCategory(p.getCategory());
        dto.setCoverImage(p.getCoverImage());
        dto.setContentImages(p.getContentImages());
        dto.setUsername(p.getUsername());
        dto.setLikeCount(p.getLikeCount());
        dto.setCollectCount(p.getCollectCount());
        dto.setStatus(p.getStatus());
        dto.setCreateTime(p.getCreateTime());
        return dto;
    }

    public static VoteDto fromVote(Vote v) {
        VoteDto dto = new VoteDto();
        dto.setId(v.getId());
        dto.setGameName(v.getGameName());
        dto.setDescription(v.getDescription());
        dto.setContentImages(v.getContentImages());
        dto.setIconPath(v.getIconPath());
        dto.setBannerPath(v.getBannerPath());
        dto.setAuthorUsername(v.getAuthorUsername());
        dto.setStatus(v.getStatus());
        dto.setVoteCount(v.getVoteCount());
        dto.setCreateTime(v.getCreateTime());
        return dto;
    }

    public static PromoDto fromGamePromo(GamePromo gp) {
        PromoDto dto = new PromoDto();
        dto.setId(gp.getId());
        dto.setName(gp.getName());
        dto.setDescription(gp.getDescription());
        dto.setCoverImage(gp.getScreenshot());
        dto.setContentImages(gp.getContentImages());
        dto.setAuthorUsername(gp.getAuthorUsername());
        dto.setStatus(gp.getStatus());
        dto.setCreateTime(gp.getCreateTime());
        return dto;
    }

    public static CFDTo fromCrowdfunding(Crowdfunding cf) {
        CFDTo dto = new CFDTo();
        dto.setId(cf.getId());
        dto.setName(cf.getName());
        dto.setDescription(cf.getDescription());
        dto.setCoverImage(cf.getIconPath());
        dto.setContentImages(cf.getContentImages());
        dto.setAuthorUsername(cf.getAuthorUsername());
        dto.setTargetAmount(cf.getTargetAmount());
        dto.setCurrentAmount(cf.getCurrentAmount());
        dto.setSupporterCount(cf.getSupporterCount());
        dto.setStatus(cf.getStatus());
        dto.setCreateTime(cf.getCreateTime());
        return dto;
    }
}
