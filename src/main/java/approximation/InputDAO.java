package approximation;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InputDAO {
    private double[][] functionValues;

    public InputDAO(InputDAO inputDAO) {
        this.functionValues = inputDAO.getFunctionValues();
    }
}
