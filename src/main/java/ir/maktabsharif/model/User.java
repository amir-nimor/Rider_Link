package ir.maktabsharif.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "APP_USERS",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_app_user_username",
                columnNames = "username"
        )
)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "User_Type",discriminatorType = DiscriminatorType.STRING)
public class User extends BaseModel<Long> {

    @Column(nullable = false, length = 20)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, length = 15)
    private String phoneNumber;
}
