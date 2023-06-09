package az.unibank.authserver.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Builder
@Entity
@Table(name = "users")
@JsonIgnoreProperties(value = {"password"}, allowSetters = true)
public class User {

    public User(int id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @Column(unique = true)
    private String username;

    private String password;

    private String phone;

    // 0 - inactive, 1 - active
    private int status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    private String activationToken;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdDate;

    private LocalDateTime lastLoginDate;

    @Column(nullable = false)
    private int isBlocked;

    private LocalDateTime blockedDate;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", status=" + status +
                ", role=" + role.getName() +
                '}';
    }
}