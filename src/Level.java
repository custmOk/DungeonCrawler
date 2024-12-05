import java.util.Random;

public enum Level
{
    HIGHEST(90, 100, 0),
    HIGH(70, 85, 0),
    LOW(40, 60, 0),
    LOWEST(20, 35, 0);

    final int low, high, value;

    Level(int low, int high, int value)
    {
        this.low = low;
        this.high = high;
        this.value = value;
    }

    public static int getRandomValue(Level level)
    {
        Random rand = new Random();
        return rand.nextInt(level.low, level.high + 1);
    }
}
