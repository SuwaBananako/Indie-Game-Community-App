package com.example.indiegamecommunity.network;

import retrofit2.Call;
import retrofit2.http.*;
import okhttp3.MultipartBody;
import java.util.List;

public interface ApiService {

    @POST("api/auth/login")
    Call<ApiResponse<LoginResponse>> login(@Body LoginRequest request);

    @POST("api/auth/register")
    Call<ApiResponse<String>> register(@Body RegisterRequest request);

    @GET("api/posts/approved")
    Call<ApiResponse<PageResponse<PostDto>>> getApprovedPosts(@Query("page") int page, @Query("size") int size);

    @GET("api/posts/hot")
    Call<ApiResponse<PageResponse<PostDto>>> getHotPosts(@Query("page") int page, @Query("size") int size);

    @GET("api/posts/search")
    Call<ApiResponse<PageResponse<PostDto>>> searchPosts(@Query("keyword") String keyword, @Query("page") int page, @Query("size") int size);

    @GET("api/posts/category/{category}")
    Call<ApiResponse<PageResponse<PostDto>>> getPostsByCategory(@Path("category") String category, @Query("page") int page, @Query("size") int size);

    @GET("api/posts/{id}")
    Call<ApiResponse<PostDto>> getPostById(@Path("id") long id);

    @POST("api/posts")
    Call<ApiResponse<PostDto>> createPost(@Body PostDto post);

    @PUT("api/posts/{id}")
    Call<ApiResponse<PostDto>> updatePost(@Path("id") long id, @Body PostUpdateBody body);

    @POST("api/posts/{id}/like")
    Call<ApiResponse<String>> likePost(@Path("id") long id);

    @GET("api/posts/my")
    Call<ApiResponse<List<PostDto>>> getMyPosts(@Query("username") String username);

    @GET("api/posts/{id}/comments")
    Call<ApiResponse<List<CommentDto>>> getComments(@Path("id") long postId);

    @POST("api/posts/{id}/comments")
    Call<ApiResponse<CommentDto>> addComment(@Path("id") long postId, @Body CommentBody comment);

    @GET("api/votes/approved")
    Call<ApiResponse<List<VoteDto>>> getApprovedVotes();

    @GET("api/votes/{id}")
    Call<ApiResponse<VoteDto>> getVoteById(@Path("id") long id);

    @GET("api/votes/my")
    Call<ApiResponse<List<VoteDto>>> getMyVotes(@Query("username") String username);

    @POST("api/votes")
    Call<ApiResponse<VoteDto>> createVote(@Body VoteDto vote);

    @POST("api/votes/{id}/vote")
    Call<ApiResponse<String>> doVote(@Path("id") long id);

    @DELETE("api/votes/{id}")
    Call<ApiResponse<String>> deleteVote(@Path("id") long id);

    @GET("api/promos/approved")
    Call<ApiResponse<List<PromoDto>>> getApprovedPromos();

    @GET("api/promos/my")
    Call<ApiResponse<List<PromoDto>>> getMyPromos(@Query("username") String username);

    @GET("api/promos/{id}")
    Call<ApiResponse<PromoDto>> getPromoById(@Path("id") long id);

    @DELETE("api/promos/{id}")
    Call<ApiResponse<String>> deletePromo(@Path("id") long id);

    @POST("api/promos")
    Call<ApiResponse<PromoDto>> createPromo(@Body PromoDto promo);

    @POST("api/promos/{id}/dislike")
    Call<ApiResponse<PromoDto>> dislikePromo(@Path("id") long id);

    @GET("api/crowdfundings/approved")
    Call<ApiResponse<List<CFDTo>>> getApprovedCFs();

    @GET("api/crowdfundings/my")
    Call<ApiResponse<List<CFDTo>>> getMyCFs(@Query("username") String username);

    @GET("api/crowdfundings/{id}")
    Call<ApiResponse<CFDTo>> getCFById(@Path("id") long id);

    @DELETE("api/crowdfundings/{id}")
    Call<ApiResponse<String>> deleteCF(@Path("id") long id);

    @POST("api/crowdfundings")
    Call<ApiResponse<CFDTo>> createCF(@Body CFDTo cf);

    @POST("api/crowdfundings/{id}/support")
    Call<ApiResponse<CFDTo>> supportCF(@Path("id") long id, @Body SupportBody body);

    @GET("api/notifications")
    Call<ApiResponse<List<NotifDto>>> getNotifications(@Query("username") String username);

    @GET("api/notifications/unread-count")
    Call<ApiResponse<Integer>> getUnreadCount(@Query("username") String username);

    @POST("api/notifications/read-all")
    Call<ApiResponse<String>> markAllRead(@Query("username") String username);

    @POST("api/notifications/{id}/read")
    Call<ApiResponse<String>> markRead(@Path("id") long id);

    @Multipart
    @POST("api/upload/image")
    Call<ApiResponse<String>> uploadImage(@Part MultipartBody.Part file);

    @GET("api/admin/posts/pending")
    Call<ApiResponse<List<AdminReviewItem>>> getPendingPosts();

    @POST("api/admin/posts/{id}/approve")
    Call<ApiResponse<AdminReviewItem>> approvePost(@Path("id") long id);

    @POST("api/admin/posts/{id}/reject")
    Call<ApiResponse<AdminReviewItem>> rejectPost(@Path("id") long id);

    @DELETE("api/admin/posts/{id}")
    Call<ApiResponse<String>> adminDeletePost(@Path("id") long id);

    @GET("api/admin/promos/pending")
    Call<ApiResponse<List<AdminReviewItem>>> getPendingPromos();

    @POST("api/admin/promos/{id}/approve")
    Call<ApiResponse<AdminReviewItem>> approvePromo(@Path("id") long id);

    @POST("api/admin/promos/{id}/reject")
    Call<ApiResponse<AdminReviewItem>> rejectPromo(@Path("id") long id);

    @GET("api/admin/votes/pending")
    Call<ApiResponse<List<AdminReviewItem>>> getPendingVotes();

    @POST("api/admin/votes/{id}/approve")
    Call<ApiResponse<AdminReviewItem>> approveVote(@Path("id") long id);

    @POST("api/admin/votes/{id}/reject")
    Call<ApiResponse<AdminReviewItem>> rejectVote(@Path("id") long id);

    @GET("api/admin/crowdfundings/pending")
    Call<ApiResponse<List<AdminReviewItem>>> getPendingCFs();

    @POST("api/admin/crowdfundings/{id}/approve")
    Call<ApiResponse<AdminReviewItem>> approveCF(@Path("id") long id);

    @POST("api/admin/crowdfundings/{id}/reject")
    Call<ApiResponse<AdminReviewItem>> rejectCF(@Path("id") long id);
}
