package com.andersenlab.etalon.userservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Getter
@Setter
@Table(name = "roles")
@Accessors(chain = true)
public class RoleEntity {

  @Id
  @Column(name = "id")
  @SequenceGenerator(name = "role_sequence", sequenceName = "role_sequence", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_sequence")
  private Long id;

  @Column(name = "role_name")
  private String roleName;

  @ManyToMany
  @JoinTable(
      name = "user_to_role",
      joinColumns = @JoinColumn(name = "role_id"),
      inverseJoinColumns = @JoinColumn(name = "user_id"))
  private Set<UserEntity> users;
}
