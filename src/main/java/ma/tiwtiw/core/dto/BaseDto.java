package ma.tiwtiw.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.tiwtiw.core.model.BaseModel;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseDto<T extends BaseModel<ID>, ID> {

  private ID id;
}
