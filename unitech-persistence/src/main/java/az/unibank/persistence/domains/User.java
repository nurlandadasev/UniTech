package az.unibank.persistence.domains;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Builder
@Entity
@EqualsAndHashCode
@Table(name = "users")
@JsonIgnoreProperties(value = {"password"}, allowSetters = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @Column(unique = true, name = "pin")
    private String pin;

    private String password;

    private String phone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdDate;

    private LocalDateTime lastLoginDate;

    @Column(nullable = false)
    private int isBlocked;

    private LocalDateTime blockedDate;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Account> accountList = new ArrayList<>();




    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", pin='" + pin + '\'' +
                ", role=" + role.getName() +
                '}';
    }
}