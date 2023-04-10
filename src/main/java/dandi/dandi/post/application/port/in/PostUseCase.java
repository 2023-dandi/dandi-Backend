package dandi.dandi.post.application.port.in;

import org.springframework.data.domain.Pageable;

public interface PostUseCase {

    PostRegisterResponse registerPost(Long memberId, PostRegisterCommand postRegisterCommand);

    PostDetailResponse getPostDetails(Long memberId, Long postId);

    void deletePost(Long memberId, Long postId);

    MyPostResponses getMyPostIdsAndPostImageUrls(Long memberId, Pageable pageable);

    MyPostsByTemperatureResponses getMyPostsByTemperature(Long memberId, Double minTemperature,
                                                          Double maxTemperature, Pageable pageable);

    FeedResponse getPostsByTemperature(Long memberId, Double minTemperature, Double maxTemperature, Pageable pageable);

    void reportPost(Long memberId, Long postId);

    LikedPostResponses getLikedPost(Long memberId, Pageable pageable);
}
