package com.solovev.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.solovev.dto.DTO;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements DTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NonNull
    @Column(unique = true, nullable = false)
    private String login;

    private String password;

    private String name;

    @Column(name = "registration_date", updatable = false)
    private final LocalDate registrationDate = LocalDate.now();

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "user",orphanRemoval = true,cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private final List<Category> categories = new ArrayList<>();

}
