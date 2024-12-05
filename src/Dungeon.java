import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.io.*;

public class Dungeon
{
    final int FILL_PERCENT = 50;
    final int SIZE = 10;

    Room currentRoom;

    List<Room> openRooms = new ArrayList<>();
    Room[][] dungeon = new Room[SIZE][SIZE];

    Random rand;
    long seed;

    File data = new File("dungeon_data.txt");

    public Dungeon() throws IOException
    {
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                dungeon[i][j] = new Room(false, (i * SIZE) + (j + 1), i, j);
        rand = new Random();
        seed = rand.nextLong();
        rand.setSeed(seed);
        createDungeon();
    }

    public Dungeon(long seed) throws IOException
    {
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                dungeon[i][j] = new Room(false, (i * SIZE) + (j + 1), i, j);
        rand = new Random();
        this.seed = seed;
        rand.setSeed(seed);
        constructDungeon();
    }

    private void constructDungeon() throws IOException
    {
        int roomsOpen = 1;
        int row = rand.nextInt(SIZE);
        int col = rand.nextInt(SIZE);

        while (roomsOpen < (int) ((SIZE * SIZE) * (FILL_PERCENT / 100.0)))
        {
            switch (rand.nextInt(4))
            {
                // 0 = up, 1 = right, 2 = down, 3 = left
                case 0 ->
                {
                    if (row != 0)
                        row--;
                }
                case 1 ->
                {
                    if (col != SIZE - 1)
                        col++;
                }
                case 2 ->
                {
                    if (row != SIZE - 1)
                        row++;
                }
                case 3 ->
                {
                    if (col != 0)
                        col--;
                }
            }
            if (!dungeon[row][col].getOpen())
            {
                dungeon[row][col].setOpen(true);
                for (int i = 0; i < rand.nextInt(4); i++)
                    dungeon[row][col].addItem(rand.nextBoolean() ? generateRandomWeapon() : generateRandomPotion());
                for (int i = 0; i < rand.nextInt(4); i++)
                    dungeon[row][col].addMonster(generateRandomMonster());
                dungeon[row][col].updateStatus();
                roomsOpen++;
                openRooms.add(dungeon[row][col]);
            }
        }
    }

    // random walk until half the space is opened
    public void createDungeon() throws IOException
    {
        System.out.println("Generating dungeon...");

        constructDungeon();

        File dungeonFile = writeFiles();

        System.out.println("Saved to " + dungeonFile.getName());

        // getDungeonInfo();
    }

    File writeFiles() throws IOException
    {
        int fileNumber;
        try (Scanner sc = new Scanner(data))
        {
            fileNumber = sc.hasNextLine() ? Integer.parseInt(sc.nextLine()) + 1 : 1;
        }
        catch (Exception e)
        {
            fileNumber = 1;
        }

        try (FileWriter dataWriter = new FileWriter(data))
        {
            dataWriter.write(fileNumber + "");
        }

        File dungeonFile = new File(fileNumber + ".txt");
        try (FileWriter dungeonWriter = new FileWriter(dungeonFile))
        {
            dungeonWriter.write(getDungeon());
        }

        return dungeonFile;
    }

    Weapon generateRandomWeapon() throws IOException
    {
        List<String> weaponTypes = Files.readAllLines(new File("weapons.txt").toPath());

        String weaponType = weaponTypes.get(rand.nextInt(weaponTypes.size()));
        double damage = rand.nextDouble(9) + 1;
        int durability = rand.nextInt(21) + 10;

        return new Weapon(durability, damage, weaponType);
    }

    Potion generateRandomPotion()
    {
        String effectType = rand.nextBoolean() ? "Healing" : "Repairing";
        double power = rand.nextDouble(9) + 1;
        int uses = rand.nextInt(10) + 1;

        return new Potion(uses, power, effectType);
    }

    Monster generateRandomMonster() throws IOException
    {
        List<String> monsterTypes = Files.readAllLines(new File("monsters.txt").toPath());

        String monsterType = monsterTypes.get(rand.nextInt(monsterTypes.size()));
        double health = rand.nextDouble(10) + 10;
        Weapon weapon = generateRandomWeapon();

        return new Monster(monsterType, health, weapon);
    }

    String getDungeon()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(seed).append("\n");
        sb.append("⬛".repeat(SIZE + 2)).append("\n");
        for (Room[] row : dungeon)
        {
            sb.append("⬛");
            for (Room room : row)
                sb.append(room);
            sb.append("⬛").append("\n");
        }
        sb.append("⬛".repeat(SIZE + 2)).append("\n");
        return sb.toString();
    }
}
