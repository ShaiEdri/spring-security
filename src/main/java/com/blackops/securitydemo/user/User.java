package com.blackops.securitydemo.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")//mysql has user
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)//Default
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String password;

}
