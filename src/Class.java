import java.util.List;
import java.util.Map;

public enum Class
{
    WARRIOR("Warrior", "💂", TerminalColor.RED), THIEF("Thief", "🥷", TerminalColor.BLUE), MAGE("Mage",
        "🧙",
        TerminalColor.MAGENTA), RANGER("Ranger", "🧝", TerminalColor.GREEN);

    final String name, icon;
    final TerminalColor color;
    int STR, DEF, DEX, AGI, INT, HP, LCK, MAT, MDF;

    Class(String name, String icon, TerminalColor color)
    {
        this.name = name;
        this.icon = icon;
        this.color = color;
    }

    public Map<String, List<String>> getStatRanges()
    {
        switch (this)
        {
            case WARRIOR ->
            {
                STR = Level.getRandomValue(Level.HIGHEST);
                DEF = Level.getRandomValue(Level.HIGHEST);
                DEX = Level.getRandomValue(Level.LOW);
                AGI = Level.getRandomValue(Level.LOW);
                INT = Level.getRandomValue(Level.LOWEST);
                HP = Level.getRandomValue(Level.HIGH);
                LCK = Level.getRandomValue(Level.LOWEST);
                MAT = Level.getRandomValue(Level.LOWEST);
                MDF = Level.getRandomValue(Level.LOW);

                return Map.of("Highest",
                        List.of("STR", "DEF"),
                        "High",
                        List.of("HP"),
                        "Low",
                        List.of("MDF", "DEX", "AGI"),
                        "Lowest",
                        List.of("MAT", "LCK", "INT"));
            }
            case THIEF ->
            {
                STR = Level.getRandomValue(Level.LOW);
                DEF = Level.getRandomValue(Level.LOWEST);
                DEX = Level.getRandomValue(Level.HIGHEST);
                AGI = Level.getRandomValue(Level.HIGHEST);
                INT = Level.getRandomValue(Level.LOW);
                HP = Level.getRandomValue(Level.LOW);
                LCK = Level.getRandomValue(Level.HIGH);
                MAT = Level.getRandomValue(Level.HIGH);
                MDF = Level.getRandomValue(Level.LOWEST);

                return Map.of("Highest",
                        List.of("DEX", "AGI"),
                        "High",
                        List.of("MAT", "LCK"),
                        "Low",
                        List.of("HP", "STR", "INT"),
                        "Lowest",
                        List.of("DEF", "MDF"));
            }
            case MAGE ->
            {
                STR = Level.getRandomValue(Level.LOWEST);
                DEF = Level.getRandomValue(Level.LOWEST);
                DEX = Level.getRandomValue(Level.LOW);
                AGI = Level.getRandomValue(Level.LOWEST);
                INT = Level.getRandomValue(Level.HIGHEST);
                HP = Level.getRandomValue(Level.HIGH);
                LCK = Level.getRandomValue(Level.LOW);
                MAT = Level.getRandomValue(Level.HIGHEST);
                MDF = Level.getRandomValue(Level.HIGHEST);

                return Map.of("Highest",
                        List.of("INT", "MAT", "MDF"),
                        "High",
                        List.of("HP"),
                        "Low",
                        List.of("LCK", "DEX"),
                        "Lowest",
                        List.of("STR", "AGI", "DEF"));
            }
            case RANGER ->
            {
                STR = Level.getRandomValue(Level.LOWEST);
                DEF = Level.getRandomValue(Level.LOW);
                DEX = Level.getRandomValue(Level.HIGH);
                AGI = Level.getRandomValue(Level.HIGHEST);
                INT = Level.getRandomValue(Level.HIGH);
                HP = Level.getRandomValue(Level.LOW);
                LCK = Level.getRandomValue(Level.HIGHEST);
                MAT = Level.getRandomValue(Level.LOW);
                MDF = Level.getRandomValue(Level.LOWEST);

                return Map.of("Highest",
                        List.of("LCK", "AGI"),
                        "High",
                        List.of("DEX", "INT"),
                        "Low",
                        List.of("DEF", "HP", "MAT"),
                        "Lowest",
                        List.of("MDF", "STR"));
            }
            default ->
            {
                return Map.of("Nothing", List.of("Nothing"));
            }
        }
    }

    public String toString()
    {
        return TerminalColor.color(name, color);
    }
}
