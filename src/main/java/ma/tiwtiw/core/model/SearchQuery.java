package ma.tiwtiw.core.model;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchQuery {

  private List<SearchCriteria> criteriaList = new ArrayList<>();

  public enum SearchOperator {
    IS, NE, LT, LTE, GT, GTE, IN, NIN, ALL, EXISTS, REGEX
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public class SearchCriteria {

    private String field;
    private Object value;
    private SearchOperator operator;
  }
}
