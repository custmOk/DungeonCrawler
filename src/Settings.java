public class Settings
{
    static int fileNumber;
    static String playerName;
    static Class classType;
    static Element elementType;
    static long dungeonSeed;
    static int startingRoomNumber;

    public static String formatSettings()
    {
        return String.format("%d - %s %s %s (%d-%d)%n",
                fileNumber,
                elementType.name,
                classType.name,
                playerName,
                dungeonSeed,
                startingRoomNumber);
    }
}
