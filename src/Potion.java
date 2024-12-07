public class Potion extends Item
{
    double power;
    String effectType;

    public Potion(int uses, double power, String effectType, Rarity rarity)
    {
        super("Potion of " + effectType,
              uses,
              rarity,
              effectType.equalsIgnoreCase("healing") ? "\uD83C\uDF7E" : "\uD83D\uDD0B");
        this.effectType = effectType;
        this.power = power;
    }

    public String toString()
    {
        return String.format("%s Effect Type: %s | Rarity: %s | Power: %.2f | Uses: %d",
                             icon,
                             effectType,
                             rarity,
                             power,
                             uses);
    }
}
