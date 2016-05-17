package net.nashlegend.sourcewall.db.gen;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 

/**
 * Entity mapped to table "MY_BASKET".
 */
public class MyBasket {

    private Long id;
    private int section;
    private int type;
    /**
     * Not-null value.
     */
    private String name;
    private String value;
    private String categoryId;
    private String categoryName;
    private boolean selected;
    private int order;

    public MyBasket() {
    }

    public MyBasket(Long id) {
        this.id = id;
    }

    public MyBasket(Long id, int section, int type, String name, String value, String categoryId, String categoryName, boolean selected, int order) {
        this.id = id;
        this.section = section;
        this.type = type;
        this.name = name;
        this.value = value;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.selected = selected;
        this.order = order;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    /**
     * Not-null value.
     */
    public String getName() {
        return name;
    }

    /**
     * Not-null value; ensure this value is available before it is saved to the database.
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

}