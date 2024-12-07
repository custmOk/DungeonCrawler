public enum Element
{
    FIRE("Fire", Color.RED, "Nature", "Water", "\uD83D\uDD25"),
    WATER("Water", Color.CYAN, "Fire", "Nature", "\uD83D\uDCA7"),
    NATURE("Nature", Color.GREEN, "Water", "Fire", "\uD83C\uDF3B");

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
}
