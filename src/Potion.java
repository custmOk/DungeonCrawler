public class Potion extends Item
{
    double power;
    PotionType type;

    public Potion(int uses, double power, PotionType type, Rarity rarity)
    {
        super("Potion of " + type.name,
              uses,
              rarity,
              type == PotionType.HEALING ? "\uD83C\uDF7E" : "\uD83D\uDD0B");
        this.type = type;
        this.power = power;
    }

    public String toString()
    {
        return String.format("%s %s %s | Power: %.2f | Uses: %d",
                             icon,
                             rarity,
                             type.name,
                             power,
                             uses);
    }
}
