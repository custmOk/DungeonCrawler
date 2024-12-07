public class Weapon extends Item
{
    double damage;

    public Weapon(int uses, double damage, String weaponType, Rarity rarity)
    {
        super(weaponType, uses, rarity, "\uD83D\uDD2A");
        this.damage = damage;
    }

    public String toString()
    {
        return String.format("%s Weapon Type: %s | Rarity: %s | Damage: %.2f | Uses: %d",
                             icon,
                             name,
                             rarity,
                             damage,
                             uses);
    }
}
