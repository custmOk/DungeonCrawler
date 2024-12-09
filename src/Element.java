public enum Element
{
    FIRE("Fire", Color.RED, "Nature", "Water", "\uD83D\uDD25"),
    WATER("Water", Color.BLUE, "Fire", "Electric", "\uD83D\uDCA7"),
    NATURE("Nature", Color.GREEN, "Wind", "Fire", "\uD83C\uDF3B"),
    ELECTRIC("Electric", Color.YELLOW, "Water", "Wind", "\uD83C\uDF29️"),
    ICE("Ice", Color.CYAN, "Nature", "Fire", "\uD83E\uDDCA"),
    WIND("Wind", Color.WHITE, "Ice", "Electric", "\uD83C\uDF2A️");

    final String name, strong, weak, icon;
    final Color color;

    Element(String name, Color color, String strong, String weak, String icon)
    {
        this.name = name;
        this.color = color;
        this.strong = strong;
        this.weak = weak;
        this.icon = icon;
    }

    public String toString()
    {
        return Color.color(name, color);
    }
}
