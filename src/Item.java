import java.util.Random;

public class Item
{
    int maxUses;
    Random rand = new Random();
    String name;
    int uses;
    int price;
    String icon;
    Rarity rarity;

    public Item(String name, int uses, Rarity rarity, String icon)
    {
        this.name = name;
        this.uses = uses;
        this.icon = icon;
        this.maxUses = uses;
        this.rarity = rarity;

        int minPrice = 0, maxPrice = 0;
        switch (rarity)
        {
            case COMMON ->
            {
                minPrice = 50;
                maxPrice = 75;
            }
            case UNCOMMON ->
            {
                minPrice = 75;
                maxPrice = 100;
            }
            case RARE ->
            {
                minPrice = 100;
                maxPrice = 125;
            }
            case EPIC ->
            {
                minPrice = 125;
                maxPrice = 150;
            }
            case LEGENDARY ->
            {
                minPrice = 150;
                maxPrice = 175;
            }
        }

        this.price = rand.nextInt(minPrice, maxPrice);
    }

    public String getInfo()
    {
        return String.format("%s %s", icon, name);
    }

    public String toString()
    {
        return "Uses: " + uses;
    }
}
