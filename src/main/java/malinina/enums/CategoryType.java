package malinina.enums;

import lombok.Getter;
import malinina.dto.Category;

public enum CategoryType {
    FOOD("Food", 1),
    ELECTRONIC("Electronic", 2),
    FURNITURE("Furniture", 3);

    @Getter
    private final String title;
    @Getter
    private final Integer id;

    CategoryType(String title, Integer id) {
        this.title = title;
        this.id = id;
    }

}
