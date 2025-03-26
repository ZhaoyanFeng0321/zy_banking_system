package zycode.web.app.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bank_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String uid;

    private String firstname;
    private String lastname;

    @Column(nullable = false, unique = true)
    private String username;

    private Date dob;
    private long tel;
    private String tag;
    private String password;
    private String gender;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private List<String> roles;

    @OneToOne(mappedBy = "owner")
    private Card card;

    /*
        The "owner" refers to the field name in the Transaction entity that maps back to this entity.

        cascade = CascadeType.ALL
        - This means that any operation performed on the owner entity (like persist, merge, remove) will also be applied to the related Transaction entities.
        - For example: If an Account is deleted, all its Transaction records will also be deleted.

        fetch = FetchType.LAZY
        - This means transactions will not be loaded from the database immediately when the owner entity is fetched.
        - Instead, they will only be fetched on demand (when accessed).
        - This improves performance by preventing unnecessary data retrieval.
     */
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Account> accounts;
}

