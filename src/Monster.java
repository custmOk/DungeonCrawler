public class Monster
{
    String type;
    double health;
    Weapon weapon;
    String icon = "\uD83D\uDC7F";

    public Monster(String type, double health, Weapon weapon)
    {
        this.type = type;
        this.health = health;
        this.weapon = weapon;
    }

    public String getInfo()
    {
        return String.format("%s %s", icon, type);
    }

    public String toString()
    {
        return String.format("%s Monster Type: %s | Health: %.2f | " + weapon, icon, type, health);
    }
}
