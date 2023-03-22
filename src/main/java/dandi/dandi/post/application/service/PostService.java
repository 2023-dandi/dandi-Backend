package dandi.dandi.post.application.service;

import dandi.dandi.common.exception.ForbiddenException;
import dandi.dandi.common.exception.NotFoundException;
import dandi.dandi.post.application.port.in.MyPostResponses;
import dandi.dandi.post.application.port.in.PostDetailResponse;
import dandi.dandi.post.application.port.in.PostRegisterCommand;
import dandi.dandi.post.application.port.in.PostRegisterResponse;
import dandi.dandi.post.application.port.in.PostUseCase;
import dandi.dandi.post.application.port.out.PostPersistencePort;
import dandi.dandi.post.domain.Post;
import dandi.dandi.post.domain.Temperatures;
import dandi.dandi.post.domain.WeatherFeeling;
import dandi.dandi.postlike.application.port.out.PostLikePersistencePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostService implements PostUseCase {

    private static final int POST_IMAGE_URL_INDEX = 1;

    private final PostPersistencePort postPersistencePort;
    private final PostLikePersistencePort postLikePersistencePort;
    private final String imageAccessUrl;

    public PostService(PostPersistencePort postPersistencePort, PostLikePersistencePort postLikePersistencePort,
                       @Value("${cloud.aws.cloud-front.uri}") String imageAccessUrl) {
        this.postPersistencePort = postPersistencePort;
        this.postLikePersistencePort = postLikePersistencePort;
        this.imageAccessUrl = imageAccessUrl;
    }

    @Override
    @Transactional
    public PostRegisterResponse registerPost(Long memberId, PostRegisterCommand postRegisterCommand) {
        Temperatures temperatures = new Temperatures(
                postRegisterCommand.getMinTemperature(), postRegisterCommand.getMaxTemperature());
        WeatherFeeling weatherFeeling = new WeatherFeeling(
                postRegisterCommand.getFeelingIndex(), postRegisterCommand.getAdditionalFeelingIndices());
        String postImageUrl = removeImageAccessUrl(postRegisterCommand.getPostImageUrl());

        Post post = Post.initial(temperatures, postImageUrl, weatherFeeling);
        Long postId = postPersistencePort.save(post, memberId);
        return new PostRegisterResponse(postId);
    }

    private String removeImageAccessUrl(String postImageUrl) {
        return postImageUrl.split(imageAccessUrl)[POST_IMAGE_URL_INDEX];
    }

    @Override
    @Transactional(readOnly = true)
    public PostDetailResponse getPostDetails(Long memberId, Long postId) {
        Post post = postPersistencePort.findById(postId)
                .orElseThrow(NotFoundException::post);
        boolean mine = post.isWrittenBy(memberId);
        boolean liked = postLikePersistencePort.existsByPostIdAndMemberId(memberId, postId);
        return new PostDetailResponse(post, mine, liked, imageAccessUrl);
    }

    @Override
    @Transactional
    public void deletePost(Long memberId, Long postId) {
        Post post = postPersistencePort.findById(postId)
                .orElseThrow(NotFoundException::post);
        validateDeleteAuthorization(post, memberId);
        postPersistencePort.deleteById(postId);
    }

    public void validateDeleteAuthorization(Post post, Long memberId) {
        if (!post.isWrittenBy(memberId)) {
            throw ForbiddenException.postDeletion();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public MyPostResponses getMyPostIdsAndPostImageUrls(Long memberId) {
        return new MyPostResponses(postPersistencePort.findPostIdAndPostImageUrlByMemberId(memberId));
    }
}
