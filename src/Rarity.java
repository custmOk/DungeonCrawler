public enum Rarity
{
    COMMON("Common", TerminalColor.WHITE),
    UNCOMMON("Uncommon", TerminalColor.GREEN),
    RARE("Rare", TerminalColor.BLUE),
    EPIC("Epic", TerminalColor.MAGENTA),
    LEGENDARY("Legendary", TerminalColor.YELLOW);

    final String name;
    final TerminalColor color;

    Rarity(String name, TerminalColor color)
    {
        this.name = name;
        this.color = color;
    }

    public String toString()
    {
        return TerminalColor.color(name, color);
    }
}
