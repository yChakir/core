package ma.tiwtiw.core.model;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BaseModel<ID> {

  @Id
  private ID id;

  @CreatedDate
  private LocalDate created;

  @LastModifiedDate
  private LocalDate lastModified;

}
