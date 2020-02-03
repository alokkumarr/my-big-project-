package sncr.bda.conf;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public static enum DSCategory {

    @SerializedName("default")
    DEFAULT("default"),
    @SerializedName("Category_1")
    CATEGORY_1("Category_1"),
    @SerializedName("Category_2")
    CATEGORY_2("Category_2");

    private final String value;
    private static Map<String, DSCategory> dsCategoryMap = new HashMap<String, DSCategory>();

    static {
        for (DSCategory category: values()) {
            dsCategoryMap.put(category.value, category);
        }
    }

    private DSCategory(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public static DSCategory fromValue(String value) {
        DSCategory dsCategory = dsCategoryMap.get(value);
        if (dsCategory == null) {
            throw new IllegalArgumentException(value);
        } else {
            return dsCategory;
        }
    }

}
