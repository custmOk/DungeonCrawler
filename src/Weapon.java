public class Weapon extends Item
{
    double damage;

    public Weapon(int uses, double damage, String weaponType)
    {
        super(weaponType, uses, "\uD83D\uDD2A");
        this.damage = damage;
    }

    public String toString()
    {
        return String.format("%s Weapon Type: %s | Damage: %.2f | Uses: %d", icon, name, damage, uses);
    }
}
