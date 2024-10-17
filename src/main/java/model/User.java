package model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Or GenerationType.AUTO if you want the database to handle it
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column
    private Integer companyId;

    @Column
    private Date createDate;

    public User() {
    }

    public User(String email) {
        this.email = email;
    }

    public User(Long id, String email, Integer companyId) {
        this.id = id;
        this.email = email;
        this.companyId = companyId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", email='" + email + '\'' + '}';
    }

}
