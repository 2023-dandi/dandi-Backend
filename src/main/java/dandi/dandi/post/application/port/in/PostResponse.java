package dandi.dandi.post.application.port.in;

import dandi.dandi.post.domain.Post;
import java.time.LocalDate;

public class PostResponse {

    private Long id;
    private PostWriterResponse writer;
    private boolean liked;
    private TemperatureResponse temperatures;
    private long feelingIndex;
    private String postImageUrl;
    private LocalDate createdAt;

    public PostResponse() {
    }

    public PostResponse(Post post, boolean liked, String imageAccessUrl) {
        this.id = post.getId();
        this.writer = new PostWriterResponse(post, imageAccessUrl);
        this.liked = liked;
        this.temperatures = new TemperatureResponse(post.getMinTemperature(), post.getMaxTemperature());
        this.feelingIndex = post.getWeatherFeelingIndex();
        this.postImageUrl = imageAccessUrl + post.getPostImageUrl();
        this.createdAt = post.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public PostWriterResponse getWriter() {
        return writer;
    }

    public boolean isLiked() {
        return liked;
    }

    public TemperatureResponse getTemperatures() {
        return temperatures;
    }

    public long getFeelingIndex() {
        return feelingIndex;
    }

    public String getPostImageUrl() {
        return postImageUrl;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }
}