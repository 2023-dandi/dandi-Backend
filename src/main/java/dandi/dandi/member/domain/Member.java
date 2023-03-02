package dandi.dandi.member.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PostPersist;
import org.springframework.data.domain.AbstractAggregateRoot;

@Entity
public class Member extends AbstractAggregateRoot<Member> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String oAuthId;

    @Embedded
    @Column(nullable = false)
    private Nickname nickname;

    @Embedded
    private Location location;

    private String profileImgUrl;

    protected Member() {
    }

    private Member(Long id, String oAuthId, Nickname nickname, Location location, String profileImgUrl) {
        this.id = id;
        this.oAuthId = oAuthId;
        this.nickname = nickname;
        this.location = location;
        this.profileImgUrl = profileImgUrl;
    }

    @PostPersist
    private void registerNewMemberCreatedEvent() {
        registerEvent(new NewMemberCreatedEvent(id));
    }

    public static Member initial(String oAuthId, String nickname, String initialProfileImageUrl) {
        return new Member(null, oAuthId, Nickname.from(nickname), Location.initial(), initialProfileImageUrl);
    }

    public Long getId() {
        return id;
    }

    public String getNickname() {
        return nickname.getValue();
    }

    public double getLatitude() {
        return location.getLatitude();
    }

    public double getLongitude() {
        return location.getLongitude();
    }

    public void updateNickname(String nickname) {
        this.nickname = Nickname.from(nickname);
    }

    public void updateLocation(double latitude, double longitude) {
        this.location = new Location(latitude, longitude);
    }

    public boolean hasProfileImgUrl(String profileImgUrl) {
        return this.profileImgUrl.equals(profileImgUrl);
    }

    public String getProfileImgUrl() {
        return profileImgUrl;
    }
}
