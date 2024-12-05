import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.function.Consumer;

public class DungeonRunner
{
    private static Dungeon dungeon;

    public static void main(String[] args) throws IOException
    {
        Scanner sc = new Scanner(System.in);
        System.out.print("generate or load: ");
        String choice = sc.next();
        if (choice.equalsIgnoreCase("generate"))
        {
            dungeon = new Dungeon();

            startEventLoop();
        }
        else if (choice.equalsIgnoreCase("load"))
        {
            System.out.print("enter file name: ");
            String fileName = sc.nextInt() + ".txt";
            File file = new File(fileName);
            if (!file.exists())
                Color.logError("file not found");
            else
            {
                System.out.println("loading " + fileName);
                Scanner fileScanner = new Scanner(file);
                long seed = fileScanner.nextLong();
                dungeon = new Dungeon(seed);

                startEventLoop();
            }
        }
        else
            Color.logError("invalid input");
    }

    private static void startEventLoop() throws IOException
    {
        Player player = new Player(dungeon);
        Scanner sc = new Scanner(System.in);

        System.out.print("""
                                   
                                   Welcome to the Dungeon!
                                   
                                   Dungeon Map Legend
                                   ⬛ - Wall
                                   \uD83C\uDFE0 - Start and Exit Room
                                   \uD83D\uDC6E - Your Current Room
                                   \uD83D\uDC80 - Monsters in Room (can have items)
                                   \uD83C\uDFF9 - Items in Room (no monsters)
                                   \uD83D\uDEA9 - No Monsters or Items in Room
                                   
                                   press enter to continue""");

        sc.nextLine();

        System.out.println("""
                                
                                Select a Class
                                
                                💂 Warrior
                                    - Highest Stats: STR, DEF
                                    - High Stats: HP
                                    - Low Stats: MDF, DEX, AGI
                                    - Lowest Stats: MAT, LCK, INT
                                👤 Thief
                                    - Highest Stats: DEX, AGI
                                    - High Stats: MAT, LCK
                                    - Low Stats: HP, STR, INT
                                    - Lowest Stats: DEF, MDF
                                👸 Mage
                                    - Highest Stats: INT, MAT, MDF
                                    - High Stats: HP
                                    - Low Stats: LCK, DEX
                                    - Lowest Stats: STR, AGI, DEF
                                👰 Ranger
                                    - Highest Stats: LCK, AGI
                                    - High Stats: DEX, INT
                                    - Low Stats: DEF, HP, MAT
                                    - Lowest Stats: MDF, STR""");

        boolean classSelected = false;

        while (!classSelected)
        {
            System.out.print("Enter class selection (Warrior, Thief, Mage, Ranger): ");
            String choice = sc.nextLine();

            switch (choice.toLowerCase())
            {
                case "warrior" -> player.selectClass(Class.WARRIOR);
                case "thief" -> player.selectClass(Class.THIEF);
                case "mage" -> player.selectClass(Class.MAGE);
                case "ranger" -> player.selectClass(Class.RANGER);
                default -> {
                    Color.logError("invalid choice");
                    continue;
                }
            }

            Class cls = player.selectedClass;
            System.out.printf("Selected Class: %s%n" +
                                      "Stats:%n" +
                                      "\tStrength - %d%n\tDefense - %d%n\tDexterity - %d%n" +
                                      "\tAgility - %d%n\tIntelligence - %d%n\tHealth Points - %d%n" +
                                      "\tLuck - %d%n\tMagic Attack - %d%n\tMagic Defense - %d%n",
                              cls.name, cls.STR, cls.DEF, cls.DEX, cls.AGI, cls.INT, cls.HP, cls.LCK, cls.MAT, cls.MDF);

            boolean confirmed = false;
            while (!confirmed)
            {
                System.out.print("Would you like to continue with this class? (y/n): ");
                String confirm = sc.nextLine();
                if (confirm.equalsIgnoreCase("y"))
                {
                    classSelected = true;
                    confirmed = true;
                }
                else if (confirm.equalsIgnoreCase("n"))
                {
                    System.out.println("Restarting class selection.");
                    confirmed = true;
                }
                else
                    Color.logError("invalid input");
            }
        }

        System.out.println(dungeon.getDungeon());
        dungeon.currentRoom.getRoomContents();

        boolean quit = false;

        while (!quit)
        {
            System.out.println("""
            Available Actions:
                🧭 Enter [WASD] to move
                🌎 map
                💖 health
                📄 contents
                💭 simulate
                🔍 examine monster [index]
                🔎 examine item [index]
                📂 take [index]
                📕 inventory [index]
                📚 inventory
                👋 use [index]
                💥 attack [index]
                🏃 escape (only works in starting room)
                📋 descriptions
            """);

            String[] actions = sc.nextLine().split("\\s+");
            String action = actions[0].toLowerCase();

            switch (action)
            {
                case "w", "a", "s", "d" -> player.move(action);
                case "map" -> player.map();
                case "health" -> player.health();
                case "contents" -> player.contents();
                case "simulate" -> player.simulate();
                case "examine" -> handleExamine(player, actions);
                case "take" -> handleActionWithIndex(player::take, actions);
                case "inventory" ->
                {
                    if (actions.length < 2)
                        player.inventory();
                    else
                        handleActionWithIndex(player::inventory, actions);
                }
                case "use" -> handleActionWithIndex(player::use, actions);
                case "attack" -> handleActionWithIndex(player::attack, actions);
                case "escape" ->
                {
                    if (player.escape())
                        quit = true;
                }
                case "descriptions" -> System.out.println("""
                                           map - displays the map
                                           health - prints current health
                                           contents - prints current room contents
                                           simulate - prints current damage to be taken and results
                                           examine monster [index] - prints monster info at index
                                           examine item [index] - prints item info at index
                                           take [index] - takes item from current room at index
                                           inventory [index] - prints item info from inventory at index
                                           inventory - prints items and indices in inventory
                                           use [index] - uses potion from inventory or equips weapon from inventory at index
                                           attack [index] - attacks monster in current room at index
                                           escape - escapes the dungeon if in starting room""");
                default -> Color.logError("illegal action");
            }

            System.out.println();
            if (player.dead)
                quit = true;
        }
    }

    private static void handleExamine(Player player, String[] actions)
    {
        if (actions.length < 3)
        {
            Color.logError("illegal input");
            return;
        }
        try
        {
            int index = Integer.parseInt(actions[2]);
            switch (actions[1].toLowerCase())
            {
                case "monster" -> player.examineMonster(index);
                case "item" -> player.examineItem(index);
                default -> Color.logError("illegal input");
            }
        }
        catch (NumberFormatException e)
        {
            Color.logError("illegal input");
        }
    }

    private static void handleActionWithIndex(Consumer<Integer> action, String[] actions)
    {
        if (actions.length < 2)
        {
            Color.logError("illegal input");
            return;
        }
        try
        {
            int index = Integer.parseInt(actions[1]);
            action.accept(index);
        }
        catch (NumberFormatException e)
        {
            Color.logError("illegal input");
        }
    }
}
