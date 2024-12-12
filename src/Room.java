import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Room
{
    final String WALL = "\uD83E\uDDF1";
    final String START = "\uD83C\uDFE0";
    final String PLAYER = "\uD83D\uDC6E";
    final String SHOP = "\uD83C\uDFEA";
    final String MONSTERS = "\uD83D\uDC80";
    final String MINI_BOSS = "\uD83D\uDC7F";
    final String BOSS = "\uD83D\uDC7A";
    final String ITEMS = "\uD83C\uDFF9";
    final String CLEAR = "\uD83D\uDEA9";

    int row;
    int col;
    int number;

    RoomType type;
    Dungeon dungeon;

    boolean startingRoom;
    boolean open;
    boolean playerIn;

    String status;
    String tempStatus = START;

    ArrayList<Item> items = new ArrayList<>();
    ArrayList<Monster> monsters = new ArrayList<>();

    public Room(boolean open, int number, int row, int col, Dungeon dungeon)
    {
        this.number = number;
        setOpen(open);
        this.row = row;
        this.col = col;
        this.dungeon = dungeon;
    }

    public boolean getOpen()
    {
        return open;
    }

    public void setOpen(boolean open)
    {
        this.open = open;
        status = open ? "" : WALL;
    }

    public void addItem(Item item)
    {
        items.add(item);
    }

    public void addMonster(Monster monster)
    {
        monsters.add(monster);
    }

    public void updateStatus()
    {
        if (playerIn) status = PLAYER;
        else if (startingRoom) status = START;
        else if (type == RoomType.SHOP) status = SHOP;
        else if (type == RoomType.MINI_BOSS) status = MINI_BOSS;
        else if (type == RoomType.BOSS) status = BOSS;
        else if (!monsters.isEmpty()) status = MONSTERS;
        else if (!items.isEmpty()) status = ITEMS;
        else status = CLEAR;
    }

    public void playerEnter()
    {
        tempStatus = status;
        playerIn = true;
        status = PLAYER;
    }

    public void playerLeave()
    {
        playerIn = false;
        status = tempStatus;
        tempStatus = null;
    }

    public String toString()
    {
        return status;
    }

    public void getRoomContents()
    {
        System.out.println("Current room contents:");
        if (monsters.isEmpty()) System.out.println("\tNo monsters in this room");
        else System.out.println("\tMonsters: " + IntStream.range(0, monsters.size()).mapToObj(i -> String.format(
                "%s [%d]",
                monsters.get(i).getInfo(),
                i)).collect(Collectors.joining(", ")));
        if (items.isEmpty()) System.out.println("\tNo items in this room");
        else System.out.println("\tItems: " + IntStream.range(0, items.size()).mapToObj(i -> String.format("%s [%d]",
                                                                                                           items.get(i).getInfo(),
                                                                                                           i)).collect(
                Collectors.joining(", ")));
        System.out.println();
    }
}
