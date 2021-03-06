package cn.kastner.oj.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@Table(name = "user_group")
public class Group {

  @Id
  @Column(length = 40)
  private String id = UUID.randomUUID().toString();

  @Column(updatable = false, unique = true)
  private Long idx;

  @Column(length = 50)
  private String name;

  @Fetch(FetchMode.JOIN)
  @ManyToOne(fetch = FetchType.EAGER)
  private User author;

  @ManyToMany(fetch = FetchType.LAZY)
  @Fetch(FetchMode.SUBSELECT)
  @JoinTable(
      name = "user_group_user",
      joinColumns = {@JoinColumn(name = "group_id", referencedColumnName = "id")},
      inverseJoinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")})
  @JsonIgnore
  private Set<User> userSet = new HashSet<>();
  private LocalDateTime createDate = LocalDateTime.now();
  private Boolean userGenerated = false;

}
