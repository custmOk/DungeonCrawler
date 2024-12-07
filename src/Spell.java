public class Spell extends Item
{
    Element element;
    double damage;
    int manaCost;

    public Spell(int manaCost, double damage, Element element, Rarity rarity)
    {
        super(element.name, 1, rarity, element.icon);
        this.damage = damage;
        this.manaCost = manaCost;
        this.element = element;
    }

    public String toString()
    {
        return String.format("%s Spell Type: %s | Rarity: %s | Damage: %.2f | Mana Cost: %d",
                             icon,
                             name,
                             rarity,
                             damage,
                             manaCost);
    }
}
