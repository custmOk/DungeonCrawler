public enum PotionType
{
    HEALING("Healing"),
    REPAIRING("Repairing"),
    MANA("Mana");

    final String name;

    PotionType(String name)
    {
        this.name = name;
    }
}
