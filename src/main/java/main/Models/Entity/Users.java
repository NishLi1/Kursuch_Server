package main.Models.Entity;

import main.Enums.Roles;
import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "users")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "login", unique = true, nullable = false)
    private String login;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "role_id")
    private String role;

    @Enumerated(EnumType.STRING)
    @Transient
    private Roles roleEnum;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserProfile userProfile;

    public User() {}


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) {
        this.role = role;
        this.roleEnum = Roles.valueOf(role);
    }

    public Roles getRoleEnum() { return roleEnum; }
    public void setRoleEnum(Roles roleEnum) {
        this.roleEnum = roleEnum;
        this.role = roleEnum != null ? roleEnum.name() : null;
    }

    public UserProfile getUserProfile() { return userProfile; }
    public void setUserProfile(UserProfile userProfile) { this.userProfile = userProfile; }
}