public class Settings
{
    static String fileName;
    static String playerName;
    static Class classType;
    static Element elementType;
    static long dungeonSeed;
    static int startingRoomNumber;

    public static String formatSettings()
    {
        return String.format("%s - %s %s %s (%d-%d)%n",
                fileName,
                elementType.name,
                classType.name,
                playerName,
                dungeonSeed,
                startingRoomNumber);
    }
}
