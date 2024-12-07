public class Monster
{
    String type;
    Rarity rarity;
    Element element;
    double health;
    Weapon weapon;
    String icon = "\uD83D\uDC7F";

    public Monster(String type, double health, Weapon weapon, Element element, Rarity rarity)
    {
        this.type = type;
        this.health = health;
        this.weapon = weapon;
        this.element = element;
        this.rarity = rarity;
    }

    public String getInfo()
    {
        return String.format("%s %s", icon, type);
    }

    public String toString()
    {
        return String.format("%s Monster Type: %s | Rarity: %s | Element Type: %s | Health: %.2f | " + weapon,
                             icon,
                             type,
                             rarity,
                             element == null ? "None" : element.name,
                             health);
    }
}
