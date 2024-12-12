public enum Element
{
    FIRE("Fire", TerminalColor.RED, "Nature", "Water", "\uD83D\uDD25"),
    WATER("Water", TerminalColor.BLUE, "Fire", "Electric", "\uD83D\uDCA7"),
    NATURE("Nature", TerminalColor.GREEN, "Wind", "Fire", "\uD83C\uDF3B"),
    ELECTRIC("Electric", TerminalColor.YELLOW, "Water", "Wind", "\uD83C\uDF29️"),
    ICE("Ice", TerminalColor.CYAN, "Nature", "Fire", "\uD83E\uDDCA"),
    WIND("Wind", TerminalColor.WHITE, "Ice", "Electric", "\uD83C\uDF2A️");

    final String name, strong, weak, icon;
    final TerminalColor color;

    Element(String name, TerminalColor color, String strong, String weak, String icon)
    {
        this.name = name;
        this.color = color;
        this.strong = strong;
        this.weak = weak;
        this.icon = icon;
    }

    public String toString()
    {
        return TerminalColor.color(name, color);
    }

    public static Element getElement(String name)
    {
        return switch (name)
        {
            case "Fire" -> Element.FIRE;
            case "Water" -> Element.WATER;
            case "Nature" -> Element.NATURE;
            case "Electric" -> Element.ELECTRIC;
            case "Ice" -> Element.ICE;
            default -> Element.WIND;
        };
    }
}
