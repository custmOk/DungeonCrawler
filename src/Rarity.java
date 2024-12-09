public enum Rarity
{
    COMMON("Common", Color.WHITE),
    UNCOMMON("Uncommon", Color.GREEN),
    RARE("Rare", Color.BLUE),
    EPIC("Epic", Color.MAGENTA),
    LEGENDARY("Legendary", Color.YELLOW);

    final String name;
    final Color color;

    Rarity(String name, Color color)
    {
        this.name = name;
        this.color = color;
    }

    public String toString()
    {
        return Color.color(name, color);
    }
}
