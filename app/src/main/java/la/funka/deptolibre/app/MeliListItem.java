package la.funka.deptolibre.app;

public class MeliListItem {
    private String image;
    private int price;
    private String title;
    private String description;

    public MeliListItem () {
        super();
    }

    public MeliListItem (String image, int price, String title, String description) {
        super();
        this.image = image;
        this.price = price;
        this.title = title;
        this.description = description;
    }

    public String getImageURL() {
        return image;
    }

    public void setImageURL(String image) {
        this.image = image;
    }

    public int getPrecio() {
        return price;
    }

    public void setPrecio(int precio) {
        this.price = precio;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
