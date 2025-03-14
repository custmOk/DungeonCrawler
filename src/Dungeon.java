import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.util.*;
import java.io.*;

public class Dungeon
{
    final int FILL_PERCENT = 50;
    final int SIZE = 10;

    Room currentRoom;
    Room recyclerRoom;

    List<Room> openRooms = new ArrayList<>();
    Room[][] dungeon = new Room[SIZE][SIZE];

    Random rand;
    long seed;
    String dungeonName;

    public Dungeon(String name)
    {
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                dungeon[i][j] = new Room(false, (i * SIZE) + (j + 1), i, j, this);
        rand = new Random();
        seed = rand.nextLong();
        rand.setSeed(seed);
        Settings.dungeonSeed = seed;
        dungeonName = name;
        createDungeon();
    }

    public Dungeon(String name, long seed)
    {
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                dungeon[i][j] = new Room(false, (i * SIZE) + (j + 1), i, j, this);
        rand = new Random();
        this.seed = seed;
        rand.setSeed(seed);
        Settings.dungeonSeed = seed;
        dungeonName = name;
        constructDungeon();
    }

    private void constructDungeon()
    {
        int roomsOpen = 0;
        int shops = 0;
        int maxShops = 1;
        int miniBosses = 0;
        int maxMiniBosses = 3;
        int bosses = 0;
        int maxBosses = 1;
        int recyclers = 0;
        int maxRecyclers = 1;
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
                int roomsRequired = (maxShops - shops) + (maxMiniBosses - miniBosses) + (maxBosses - bosses) + (maxRecyclers - recyclers);
                boolean isShop = shops < maxShops && rand.nextDouble() < 0.1 || targetRooms - roomsOpen <= roomsRequired;
                boolean isMiniBoss = !isShop && miniBosses < maxMiniBosses && rand.nextDouble() < 0.25 || targetRooms - roomsOpen <= roomsRequired;
                boolean isBoss = !isShop && !isMiniBoss && bosses < maxBosses && rand.nextDouble() < 0.1 || targetRooms - roomsOpen <= roomsRequired;
                boolean isRecycler = !isShop && !isMiniBoss && !isBoss && recyclers < maxRecyclers && rand.nextDouble() < 0.1 || targetRooms - roomsOpen <= roomsRequired;

                if (isShop)
                {
                    dungeon[row][col] = new Shop(true,
                            dungeon[row][col].number,
                            dungeon[row][col].row,
                            dungeon[row][col].col,
                            this);
                    dungeon[row][col].type = RoomType.SHOP;
                    shops++;
                }
                else if (isMiniBoss)
                {
                    dungeon[row][col] = new MiniBoss(true,
                            dungeon[row][col].number,
                            dungeon[row][col].row,
                            dungeon[row][col].col,
                            this);
                    dungeon[row][col].type = RoomType.MINI_BOSS;
                    for (int i = 0; i < rand.nextInt(2); i++)
                    {
                        dungeon[row][col].addItem(rand.nextBoolean() ? generateRandomWeapon(Rarity.EPIC) : generateRandomPotion(
                                Rarity.EPIC));
                    }
                    for (int i = 0; i < rand.nextInt(3); i++)
                    {
                        double chance = rand.nextDouble();
                        Rarity rarity;
                        if (chance < 0.3) rarity = Rarity.COMMON;
                        else if (chance < 0.8) rarity = Rarity.UNCOMMON;
                        else rarity = Rarity.RARE;
                        dungeon[row][col].addMonster(generateRandomMonster(rarity));
                    }
                    dungeon[row][col].addMonster(generateRandomMonster(Rarity.EPIC));
                    miniBosses++;
                }
                else if (isBoss)
                {
                    dungeon[row][col] = new Boss(true,
                            dungeon[row][col].number,
                            dungeon[row][col].row,
                            dungeon[row][col].col,
                            this);
                    dungeon[row][col].type = RoomType.BOSS;
                    dungeon[row][col].addItem(rand.nextBoolean() ? generateRandomWeapon(Rarity.LEGENDARY) : generateRandomPotion(
                            Rarity.LEGENDARY));
                    for (int i = 0; i < rand.nextInt(2); i++)
                    {
                        double chance = rand.nextDouble();
                        Rarity rarity;
                        if (chance < 0.6) rarity = Rarity.UNCOMMON;
                        else if (chance < 0.9) rarity = Rarity.RARE;
                        else rarity = Rarity.EPIC;
                        dungeon[row][col].addMonster(generateRandomMonster(rarity));
                    }
                    dungeon[row][col].addMonster(generateRandomMonster(Rarity.LEGENDARY));
                    bosses++;
                }
                else if (isRecycler)
                {
                    dungeon[row][col] = new Recycler(true,
                            dungeon[row][col].number,
                            dungeon[row][col].row,
                            dungeon[row][col].col,
                            this);
                    dungeon[row][col].type = RoomType.RECYCLER;
                    recyclers++;
                    recyclerRoom = dungeon[row][col];
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
                        dungeon[row][col].addItem(rand.nextBoolean() ? generateRandomWeapon(rarity) : generateRandomPotion(
                                rarity));
                    }
                    for (int i = 0; i < rand.nextInt(5); i++)
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

    public void createDungeon()
    {
        System.out.println("Generating dungeon...");

        constructDungeon();
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
            case EPIC -> rand.nextDouble(5) + 15;
            case LEGENDARY -> rand.nextDouble(5) + 20;
        };
        int durability = rand.nextInt(21) + 10;

        return new Weapon(durability, damage, weaponType, rarity);
    }

    Potion generateRandomPotion(Rarity rarity)
    {
        PotionType type = switch (rand.nextInt(3))
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
            case EPIC -> rand.nextDouble(5) + 20;
            case LEGENDARY -> rand.nextDouble(5) + 25;
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
            case EPIC -> rand.nextDouble(5) + 15;
            case LEGENDARY -> rand.nextDouble(5) + 20;
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

        return new Monster(monsterType,
                health,
                weapon,
                rand.nextBoolean() ? generateRandomElement() : null,
                rarity);
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
        sb.append("\uD83E\uDDF1".repeat(SIZE + 2)).append("\n");
        for (Room[] row : dungeon)
        {
            sb.append("\uD83E\uDDF1");
            for (Room room : row)
                sb.append(room);
            sb.append("\uD83E\uDDF1").append("\n");
        }
        sb.append("\uD83E\uDDF1".repeat(SIZE + 2)).append("\n");
        return sb.toString();
    }
}

class RandomTypeAdapter
        extends TypeAdapter<Random>
{
    @Override
    public void write(JsonWriter out, Random value) throws IOException
    {
        out.value(value.nextLong());
    }

    @Override
    public Random read(JsonReader in) throws IOException
    {
        return new Random(in.nextLong());
    }
}

class FileTypeAdapter
        extends TypeAdapter<File>
{
    @Override
    public void write(JsonWriter out, File file) throws IOException
    {
        out.value(file.getPath());
    }

    @Override
    public File read(JsonReader in) throws IOException
    {
        return new File(in.nextString());
    }
}

class ClassTypeAdapter
        extends TypeAdapter<Class>
{
    @Override
    public void write(JsonWriter out, Class value) throws IOException
    {
        // Serialize the enum and its fields
        out.beginObject();
        out.name("name").value(value.name()); // Serialize the enum name
        out.name("STR").value(value.STR);
        out.name("DEF").value(value.DEF);
        out.name("DEX").value(value.DEX);
        out.name("AGI").value(value.AGI);
        out.name("INT").value(value.INT);
        out.name("HP").value(value.HP);
        out.name("LCK").value(value.LCK);
        out.name("MAT").value(value.MAT);
        out.name("MDF").value(value.MDF);
        out.endObject();
    }

    @Override
    public Class read(JsonReader in) throws IOException
    {
        in.beginObject();
        String enumName;
        Class enumValue = null;

        while (in.hasNext())
        {
            String fieldName = in.nextName();
            if ("name".equals(fieldName))
            {
                enumName = in.nextString();
                enumValue = Class.valueOf(enumName);
            }
            else if (enumValue != null)
            {
                switch (fieldName)
                {
                    case "STR" -> enumValue.STR = in.nextInt();
                    case "DEF" -> enumValue.DEF = in.nextInt();
                    case "DEX" -> enumValue.DEX = in.nextInt();
                    case "AGI" -> enumValue.AGI = in.nextInt();
                    case "INT" -> enumValue.INT = in.nextInt();
                    case "HP" -> enumValue.HP = in.nextInt();
                    case "LCK" -> enumValue.LCK = in.nextInt();
                    case "MAT" -> enumValue.MAT = in.nextInt();
                    case "MDF" -> enumValue.MDF = in.nextInt();
                    default -> in.skipValue();
                }
            }
            else in.skipValue();
        }
        in.endObject();
        return enumValue;
    }
}