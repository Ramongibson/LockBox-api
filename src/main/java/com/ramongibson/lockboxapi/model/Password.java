package com.ramongibson.lockboxapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "passwords")
@Data
@NoArgsConstructor
public class Password {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(hidden = true)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String value;

    @Column(nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "category_name", referencedColumnName = "name")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "username", nullable = false)
    @Schema(hidden = true)
    @JsonIgnore
    private User user;
}