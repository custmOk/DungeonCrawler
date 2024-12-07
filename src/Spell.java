public class Spell extends Item
{
    Element element;
    double damage;
    int manaCost;

    public Spell(int manaCost, double damage, Element element, Rarity rarity)
    {
        super(element.name + " Spell", 1, rarity, element.icon);
        this.damage = damage;
        this.manaCost = manaCost;
        this.element = element;
    }

    public String toString()
    {
        return String.format("%s %s %s | Damage: %.2f | Mana Cost: %d",
                             icon,
                             Color.color(rarity.name, rarity.color),
                             name,
                             damage,
                             manaCost);
    }
}
