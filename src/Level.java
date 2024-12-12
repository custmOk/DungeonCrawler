import java.util.Random;

public enum Level
{
    HIGHEST(90, 100),
    HIGH(70, 85),
    LOW(40, 60),
    LOWEST(20, 35);

    final int low, high, value;
    final String stat;

    Level(int low, int high)
    {
        this.low = low;
        this.high = high;
        this.value = getRandomValue(this);
        this.stat = name();
    }

    public static int getRandomValue(Level level)
    {
        Random rand = new Random();
        return rand.nextInt(level.low, level.high + 1);
    }

    public String toString()
    {
        return stat;
    }
}
