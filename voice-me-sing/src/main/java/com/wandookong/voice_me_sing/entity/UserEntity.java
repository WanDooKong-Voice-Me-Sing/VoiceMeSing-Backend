package com.wandookong.voice_me_sing.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@Table(name = "testUserTable")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) /* 자동으로 생성되고 관리. IDENTITY 해야 id 값이 겹치지 않고 잘 생성*/
    private int id;

    @Column
    private String email;

    @Column
    private String username;

    @Column
    private String password;

    @Column
    private String role;

}
