public enum Element
{
    FIRE("Fire", "Nature", "Water", "\uD83D\uDD25"),
    WATER("Water", "Fire", "Nature", "\uD83D\uDCA7"),
    NATURE("Nature", "Water", "Fire", "\uD83E\uDEA8");

    final String name, strong, weak, icon;

    Element(String name, String strong, String weak, String icon)
    {
        this.name = name;
        this.strong = strong;
        this.weak = weak;
        this.icon = icon;
    }
}
