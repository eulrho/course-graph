package com.course_graph.entity;

import com.course_graph.dto.UserDTO;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 200)
    private String email;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false, length = 500)
    private String password;

    // user-subject join table
    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL)
    private List<HistoryEntity> historyEntityList = new ArrayList<>();

    @Builder
    public static UserEntity toUserEntity(UserDTO userDTO) {
        UserEntity userEntity = new UserEntity();
        userEntity.email = userDTO.getEmail();
        userEntity.password = userDTO.getPassword();
        userEntity.year = userDTO.getYear();
        return userEntity;
    }

    public void edit(String newPassword, int newYear) {
        this.password = newPassword;
        this.year = newYear;
    }

    public void addHistory(HistoryEntity historyEntity) {
        this.historyEntityList.add(historyEntity);
    }
}
