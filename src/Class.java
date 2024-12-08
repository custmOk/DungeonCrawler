public enum Class
{
    WARRIOR("Warrior",
            Color.RED,
            Level.HIGHEST,
            Level.HIGHEST,
            Level.LOW,
            Level.LOW,
            Level.LOWEST,
            Level.HIGH,
            Level.LOWEST,
            Level.LOWEST,
            Level.LOW),
    THIEF("Thief",
          Color.BLUE,
          Level.LOW,
          Level.LOWEST,
          Level.HIGHEST,
          Level.HIGHEST,
          Level.LOW,
          Level.LOW,
          Level.HIGH,
          Level.HIGH,
          Level.LOWEST),
    MAGE("Mage",
         Color.MAGENTA,
         Level.LOWEST,
         Level.LOWEST,
         Level.LOW,
         Level.LOWEST,
         Level.HIGHEST,
         Level.HIGH,
         Level.LOW,
         Level.HIGHEST,
         Level.HIGHEST),
    RANGER("Ranger",
           Color.GREEN,
           Level.LOWEST,
           Level.LOW,
           Level.HIGH,
           Level.HIGHEST,
           Level.HIGH,
           Level.LOW,
           Level.HIGHEST,
           Level.LOW,
           Level.LOWEST);

    final String name;
    final Color color;
    final int STR, DEF, DEX, AGI, INT, HP, LCK, MAT, MDF;

    Class(String name, Color color, Level STR, Level DEF, Level DEX, Level AGI, Level INT, Level HP, Level LCK,
          Level MAT, Level MDF)
    {
        this.name = name;
        this.color = color;
        this.STR = Level.getRandomValue(STR);
        this.DEF = Level.getRandomValue(DEF);
        this.DEX = Level.getRandomValue(DEX);
        this.AGI = Level.getRandomValue(AGI);
        this.INT = Level.getRandomValue(INT);
        this.HP = Level.getRandomValue(HP);
        this.LCK = Level.getRandomValue(LCK);
        this.MAT = Level.getRandomValue(MAT);
        this.MDF = Level.getRandomValue(MDF);
    }

    public String toString()
    {
        return Color.color(name, color);
    }
}
