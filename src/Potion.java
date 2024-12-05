public class Potion extends Item
{
    double power;
    String effectType;

    public Potion(int uses, double power, String effectType)
    {
        super("Potion of " + effectType, uses, effectType.equalsIgnoreCase("healing") ? "\uD83C\uDF7E" : "\uD83D\uDD0B");
        this.effectType = effectType;
        this.power = power;
    }

    public String toString()
    {
        return String.format("%s Effect Type: %s | Power: %.2f | Uses: %d", icon, effectType, power, uses);
    }
}
