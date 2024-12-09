import java.util.*;
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
                dungeon[i][j] = new Room(false, (i * SIZE) + (j + 1), i, j, this);
        rand = new Random();
        seed = rand.nextLong();
        rand.setSeed(seed);
        createDungeon();
    }

    public Dungeon(long seed) throws IOException
    {
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                dungeon[i][j] = new Room(false, (i * SIZE) + (j + 1), i, j, this);
        rand = new Random();
        this.seed = seed;
        rand.setSeed(seed);
        constructDungeon();
    }

    private void constructDungeon() throws IOException
    {
        int roomsOpen = 0;
        int shopsOpened = 0;
        int maxShops = 1;
        int row = rand.nextInt(SIZE);
        int col = rand.nextInt(SIZE);

        int targetRooms = (int) ((SIZE * SIZE) * (FILL_PERCENT / 100.0));

        while (roomsOpen < targetRooms)
        {
            switch (rand.nextInt(4))
            {
                // 0 = up, 1 = right, 2 = down, 3 = left
                case 0 ->
                {
                    if (row != 0) row--;
                }
                case 1 ->
                {
                    if (col != SIZE - 1) col++;
                }
                case 2 ->
                {
                    if (row != SIZE - 1) row++;
                }
                case 3 ->
                {
                    if (col != 0) col--;
                }
            }
            if (!dungeon[row][col].getOpen())
            {
                boolean isShop =
                        shopsOpened < maxShops && rand.nextDouble() < 0.1 || targetRooms - roomsOpen <= maxShops - shopsOpened;

                if (isShop)
                {
                    dungeon[row][col] = new Shop(true,
                                                 dungeon[row][col].number,
                                                 dungeon[row][col].row,
                                                 dungeon[row][col].col,
                                                 this);
                    dungeon[row][col].type = RoomType.SHOP;
                    shopsOpened++;
                }
                else
                {
                    dungeon[row][col].setOpen(true);
                    dungeon[row][col].type = RoomType.NORMAL;

                    for (int i = 0; i < rand.nextInt(4); i++)
                    {
                        double chance = rand.nextDouble();
                        Rarity rarity;
                        if (chance < 0.6) rarity = Rarity.COMMON;
                        else if (chance < 0.9) rarity = Rarity.UNCOMMON;
                        else rarity = Rarity.RARE;
                        dungeon[row][col].addItem(rand.nextBoolean() ?
                                                          generateRandomWeapon(rarity) :
                                                          generateRandomPotion(rarity));
                    }
                    for (int i = 0; i < rand.nextInt(4); i++)
                    {
                        double chance = rand.nextDouble();
                        Rarity rarity;
                        if (chance < 0.6) rarity = Rarity.COMMON;
                        else if (chance < 0.9) rarity = Rarity.UNCOMMON;
                        else rarity = Rarity.RARE;
                        dungeon[row][col].addMonster(generateRandomMonster(rarity));
                    }
                }

                roomsOpen++;
                dungeon[row][col].updateStatus();
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

    Weapon generateRandomWeapon(Rarity rarity)
    {
        InputStream is = getClass().getClassLoader().getResourceAsStream("weapons.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(is)));
        List<String> weaponTypes = reader.lines().toList();

        String weaponType = weaponTypes.get(rand.nextInt(weaponTypes.size()));
        double damage = switch (rarity)
        {
            case COMMON -> rand.nextDouble(4) + 1;
            case UNCOMMON -> rand.nextDouble(5) + 5;
            case RARE -> rand.nextDouble(5) + 10;
            default -> 0;
        };
        int durability = rand.nextInt(21) + 10;

        return new Weapon(durability, damage, weaponType, rarity);
    }

    Potion generateRandomPotion(Rarity rarity)
    {
        PotionType type = switch(rand.nextInt(3))
        {
            case 0 -> PotionType.HEALING;
            case 1 -> PotionType.REPAIRING;
            case 2 -> PotionType.MANA;
            default -> null;
        };
        double power = switch (rarity)
        {
            case COMMON -> rand.nextDouble(5) + 5;
            case UNCOMMON -> rand.nextDouble(5) + 10;
            case RARE -> rand.nextDouble(5) + 15;
            default -> 0;
        };
        int uses = rand.nextInt(5) + 1;

        return new Potion(uses, power, type, rarity);
    }

    Spell generateRandomSpell(Rarity rarity)
    {
        double damage = switch (rarity)
        {
            case COMMON -> rand.nextDouble(4) + 1;
            case UNCOMMON -> rand.nextDouble(5) + 5;
            case RARE -> rand.nextDouble(5) + 10;
            default -> 0;
        };
        int manaCost = rand.nextInt(8) + 3;

        return new Spell(manaCost, damage, generateRandomElement(), rarity);
    }

    Monster generateRandomMonster(Rarity rarity)
    {
        InputStream is = getClass().getClassLoader().getResourceAsStream("monsters.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(is)));
        List<String> monsterTypes = reader.lines().toList();

        String monsterType = monsterTypes.get(rand.nextInt(monsterTypes.size()));

        double health = switch (rarity)
        {
            case COMMON -> rand.nextDouble(10) + 10;
            case UNCOMMON -> rand.nextDouble(10) + 25;
            case RARE -> rand.nextDouble(10) + 30;
            case EPIC -> rand.nextDouble(10) + 40;
            case LEGENDARY -> rand.nextDouble(25) + 50;
        };
        Weapon weapon = generateRandomWeapon(rarity);

        return new Monster(monsterType, health, weapon, rand.nextBoolean() ? generateRandomElement() : null, rarity);
    }

    private Element generateRandomElement()
    {
        int num = rand.nextInt(6);
        return switch (num)
        {
            case 0 -> Element.FIRE;
            case 1 -> Element.WATER;
            case 2 -> Element.NATURE;
            case 3 -> Element.ELECTRIC;
            case 4 -> Element.ICE;
            default -> Element.WIND;
        };
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
