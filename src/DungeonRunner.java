import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;

public class DungeonRunner
{
    private static Dungeon dungeon;

    public static void main(String[] args) throws IOException
    {
        start();
    }

    private static void start() throws IOException
    {
        Scanner sc = new Scanner(System.in);
        boolean run = true;
        while (run)
        {
            System.out.print("generate or load: ");
            String choice = sc.next();
            if (choice.equalsIgnoreCase("generate"))
            {
                dungeon = new Dungeon();
                run = false;

                startEventLoop();
            }
            else if (choice.equalsIgnoreCase("load"))
            {
                System.out.print("enter file name: ");
                String fileName = sc.nextInt() + ".txt";
                File file = new File(fileName);
                if (!file.exists()) TerminalColor.logError("file not found");
                else
                {
                    System.out.println("loading " + fileName);
                    Scanner fileScanner = new Scanner(file);
                    long seed = fileScanner.nextLong();
                    dungeon = new Dungeon(seed);
                    run = false;

                    startEventLoop();
                }
            }
            else TerminalColor.logError("invalid input");
        }
    }

    private static void startEventLoop()
    {
        Player player = new Player(dungeon);
        Scanner sc = new Scanner(System.in);

        System.out.print("""
                                 
                                 Welcome to the Dungeon!
                                 
                                 Dungeon Map Legend
                                 \uD83E\uDDF1 - Wall
                                 \uD83C\uDFE0 - Start and Exit Room
                                 \uD83D\uDC6E - Your Current Room
                                 \uD83C\uDFEA - Shop/Store Room
                                 \uD83D\uDC7F - Mini Boss Room
                                 \uD83D\uDC7A - Boss Room
                                 \uD83D\uDC80 - Monsters in Room (can have items)
                                 \uD83C\uDFF9 - Items in Room (no monsters)
                                 \uD83D\uDEA9 - No Monsters or Items in Room
                                 
                                 press enter to continue""");

        sc.nextLine();
        printSpacing();

        System.out.println("Select a Class");

        for (Class c : Class.values())
        {
            Map<String, List<String>> statRanges = c.getStatRanges();
            System.out.printf("""
                                      %s %s
                                          - Highest Stats: %s
                                          - High Stats: %s
                                          - Low Stats: %s
                                          - Lowest Stats: %s
                                      """,
                              c.icon,
                              c,
                              String.join(", ", statRanges.get("Highest")),
                              String.join(", ", statRanges.get("High")),
                              String.join(", ", statRanges.get("Low")),
                              String.join(", ", statRanges.get("Lowest")));
        }
        System.out.println();

        boolean classSelected = false;

        while (!classSelected)
        {
            System.out.printf("Enter class selection (%s, %s, %s, %s): ",
                              TerminalColor.color("Warrior", TerminalColor.RED),
                              TerminalColor.color("Thief", TerminalColor.BLUE),
                              TerminalColor.color("Mage", TerminalColor.MAGENTA),
                              TerminalColor.color("Ranger", TerminalColor.GREEN));
            String choice = sc.nextLine();

            switch (choice.toLowerCase())
            {
                case "warrior" -> player.selectClass(Class.WARRIOR);
                case "thief" -> player.selectClass(Class.THIEF);
                case "mage" -> player.selectClass(Class.MAGE);
                case "ranger" -> player.selectClass(Class.RANGER);
                default ->
                {
                    TerminalColor.logError("invalid choice");
                    continue;
                }
            }

            player.classInfo();

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
                else TerminalColor.logError("invalid input");
            }
        }

        printSpacing();

        System.out.println("Select Element Affinity\n");
        for (Element e : Element.values())
        {
            System.out.printf("%s %s (Strong against %s %s, Weak to %s %s)%n",
                              e.icon,
                              e,
                              Element.getElement(e.strong).icon,
                              Element.getElement(e.strong),
                              Element.getElement(e.weak).icon,
                              Element.getElement(e.weak));
        }
        System.out.println();

        boolean affinitySelected = false;

        while (!affinitySelected)
        {
            System.out.printf("Enter affinity selection (%s, %s, %s, %s, %s, %s): ",
                              Element.FIRE,
                              Element.WATER,
                              Element.NATURE,
                              Element.ELECTRIC,
                              Element.ICE,
                              Element.WIND);
            String choice = sc.nextLine();

            switch (choice.toLowerCase())
            {
                case "fire" -> player.affinity = Element.FIRE;
                case "water" -> player.affinity = Element.WATER;
                case "nature" -> player.affinity = Element.NATURE;
                case "electric" -> player.affinity = Element.ELECTRIC;
                case "ice" -> player.affinity = Element.ICE;
                case "wind" -> player.affinity = Element.WIND;
                default ->
                {
                    TerminalColor.logError("invalid choice");
                    continue;
                }
            }

            boolean confirmed = false;
            while (!confirmed)
            {
                System.out.print("Would you like to continue with this affinity? (y/n): ");
                String confirm = sc.nextLine();
                if (confirm.equalsIgnoreCase("y"))
                {
                    affinitySelected = true;
                    confirmed = true;
                }
                else if (confirm.equalsIgnoreCase("n"))
                {
                    System.out.println("Restarting affinity selection.");
                    confirmed = true;
                }
                else TerminalColor.logError("invalid input");
            }
        }

        printSpacing();

        System.out.println(dungeon.getDungeon());
        dungeon.currentRoom.getRoomContents();

        boolean quit = false;

        while (!quit)
        {
            System.out.printf("""
                                      Available Actions:
                                      %s
                                      %s
                                      %s
                                      %s
                                      %s
                                      %s
                                      %s
                                      %s
                                      """,
                              tableDivider(0),
                              tableRow(TerminalColor.color("🔸 General", TerminalColor.YELLOW),
                                       TerminalColor.color("🔸 Information", TerminalColor.YELLOW),
                                       TerminalColor.color("🔸 Room", TerminalColor.YELLOW),
                                       TerminalColor.color("🔸 Player", TerminalColor.YELLOW),
                                       TerminalColor.color("🔸 Shop", TerminalColor.YELLOW)),
                              tableRow("🧭 [WASD]", "💰 pouch", "📄 contents", "🎒 inventory", "💵 shop"),
                              tableRow("🌎 map", "📊 status", "🔍 examine monster #", "📕 inventory #", "💴 shop #"),
                              tableRow("🏃 escape",
                                       "\uD83E\uDEAA player",
                                       "🔎 examine item #",
                                       "👋 use #",
                                       "💶 shop buy #"),
                              tableRow("📋 descriptions",
                                       "\uD83D\uDD39",
                                       "📂 take #",
                                       "\uD83D\uDD39",
                                       "💷 shop sell " + "#"),
                              tableRow("\uD83D\uDD39", "\uD83D\uDD39", "💥 attack #", "\uD83D\uDD39", "\uD83D\uDD39"),
                              tableDivider(1));

            System.out.print("Action: ");
            String[] actions = sc.nextLine().split("\\s+");
            String action = actions[0].toLowerCase();

            switch (action)
            {
                case "w", "a", "s", "d" -> player.move(action);
                case "map" -> player.map();
                case "pouch" -> player.pouch();
                case "status" -> player.status();
                case "player" -> player.player();
                case "contents" -> player.contents();
                case "examine" -> handleExamine(player, actions);
                case "take" -> handleActionWithIndex(player::take, actions);
                case "inventory" ->
                {
                    if (actions.length < 2) player.inventory();
                    else handleActionWithIndex(player::inventory, actions);
                }
                case "use" -> handleActionWithIndex(player::use, actions);
                case "attack" -> handleActionWithIndex(player::attack, actions);
                case "shop" ->
                {
                    if (actions.length < 2) player.shop();
                    else if (actions.length < 3) handleActionWithIndex(player::shop, actions);
                    else handleShop(player, actions);
                }
                case "escape" ->
                {
                    if (player.escape()) quit = true;
                }
                case "cheat" -> player.cheat();
                case "descriptions" -> System.out.println("""
                                                                  map - displays the map
                                                                  pouch - prints amount of coins collected
                                                                  status - prints player health and mana count
                                                                  player - prints class details about player
                                                                  contents - prints current room contents
                                                                  examine monster # - prints monster info at index #
                                                                  examine item # - prints item info at index #
                                                                  take # - takes item from current room at index # (if in shop, buys item)
                                                                  inventory # - prints item info from inventory at index #
                                                                  inventory - prints items and indices in inventory
                                                                  use # - uses potion, equips weapon, or equips spell from inventory at index #
                                                                  attack # - attacks monster in current room at index #
                                                                  shop - prints items and cost in shop
                                                                  shop # - prints item from shop at index #
                                                                  shop buy # - buys item at index #
                                                                  shop sell # - sells item from inventory for half price at index #
                                                                  escape - escapes the dungeon if in starting room""");
                default -> TerminalColor.logError("illegal action");
            }

            System.out.println();
            if (player.dead) quit = true;
        }

        System.out.print("Enter to leave");
        sc.nextLine();
    }

    private static void handleExamine(Player player, String[] actions)
    {
        if (actions.length < 3)
        {
            TerminalColor.logError("illegal input");
            return;
        }
        try
        {
            int index = Integer.parseInt(actions[2]);
            switch (actions[1].toLowerCase())
            {
                case "monster" -> player.examineMonster(index);
                case "item" -> player.examineItem(index);
                default -> TerminalColor.logError("illegal input");
            }
        }
        catch (NumberFormatException e)
        {
            TerminalColor.logError("illegal input");
        }
    }

    private static void handleShop(Player player, String[] actions)
    {
        if (actions.length < 3)
        {
            TerminalColor.logError("illegal input");
            return;
        }
        try
        {
            int index = Integer.parseInt(actions[2]);
            switch (actions[1].toLowerCase())
            {
                case "buy" -> player.buy(index);
                case "sell" -> player.sell(index);
                default -> TerminalColor.logError("illegal input");
            }
        }
        catch (NumberFormatException e)
        {
            TerminalColor.logError("illegal input");
        }
    }

    private static void handleActionWithIndex(Consumer<Integer> action, String[] actions)
    {
        if (actions.length < 2)
        {
            TerminalColor.logError("illegal input");
            return;
        }
        try
        {
            int index = Integer.parseInt(actions[1]);
            action.accept(index);
        }
        catch (NumberFormatException e)
        {
            TerminalColor.logError("illegal input");
        }
    }

    private static void printSpacing()
    {
        System.out.println();
        System.out.println("----------------------------------------------------");
        System.out.println();
    }

    private static String tableRow(String col1, String col2, String col3, String col4, String col5)
    {
        return String.format("▪️│ %-21s │ %-21s │ %-21s │ %-21s │ %-21s │",
                             pad(col1),
                             pad(col2),
                             pad(col3),
                             pad(col4),
                             pad(col5));
    }

    private static String tableDivider(int place)
    {
        String line = "───────────────────────";
        String format = switch (place)
        {
            case 0 -> "\uD83D\uDD3B┌%-23s┬%-23s┬%-23s┬%-23s┬%-23s┐\uD83D\uDD3B";
            case 1 -> "\uD83D\uDD3A└%-23s┴%-23s┴%-23s┴%-23s┴%-23s┘\uD83D\uDD3A";
            default -> "";
        };
        return String.format(format, line, line, line, line, line);
    }

    private static String pad(String text)
    {
        String plainText = text.replaceAll("\u001B\\[[;\\d]*m", "");
        int padding = 20 - plainText.length() + 1;
        return text + " ".repeat(Math.max(0, padding));
    }
}
