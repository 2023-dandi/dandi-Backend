package dandi.dandi.notification.application;

import dandi.dandi.comment.domain.CommentCreatedEvent;
import dandi.dandi.notification.application.port.out.NotificationPersistencePort;
import dandi.dandi.notification.domain.Notification;
import dandi.dandi.notification.domain.PostCommentNotification;
import org.springframework.transaction.event.TransactionalEventListener;

public class CommentCreatedEventHandler extends NotificationEventHandler {

    public CommentCreatedEventHandler(NotificationPersistencePort notificationPersistencePort) {
        super(notificationPersistencePort);
    }

    @TransactionalEventListener
    public void handleCommentCreatedNotificationEvent(CommentCreatedEvent commentCreatedEvent) {
        Notification notification = PostCommentNotification.initial(commentCreatedEvent.getTargetMemberId(),
                commentCreatedEvent.getPostId(), commentCreatedEvent.getCommentId());
        saveNotification(notification);
    }
}
