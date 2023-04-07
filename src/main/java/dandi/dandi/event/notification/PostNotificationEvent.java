package dandi.dandi.event.notification;

import dandi.dandi.notification.domain.NotificationType;

public class PostNotificationEvent {

    private final Long targetMemberId;
    private final Long publisherId;
    private final Long postId;
    private final NotificationType notificationType;

    public PostNotificationEvent(Long targetMemberId, Long publisherId, Long postId,
                                 NotificationType notificationType) {
        this.targetMemberId = targetMemberId;
        this.publisherId = publisherId;
        this.postId = postId;
        this.notificationType = notificationType;
    }

    public static PostNotificationEvent postLike(Long targetMemberId, Long publisherId, Long postId) {
        return new PostNotificationEvent(targetMemberId, publisherId, postId, NotificationType.POST_LIKE);
    }
}
