import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Player
{
    Dungeon dungeon;
    List<Item> inventory;
    Weapon equipped;
    Class selectedClass;

    private double MAX_HEALTH;
    double health;
    int defeatedMonsters;
    boolean dead;

    public Player(Dungeon dungeon) throws IOException
    {
        inventory = new ArrayList<>();
        this.dungeon = dungeon;
        inventory.add(dungeon.generateRandomWeapon());
        inventory.add(dungeon.generateRandomPotion());

        equipped = (Weapon) inventory.getFirst();
        MAX_HEALTH = 100;
        health = MAX_HEALTH;

        Random rand = new Random(dungeon.seed);
        dungeon.currentRoom = dungeon.openRooms.get(rand.nextInt(dungeon.openRooms.size()));
        dungeon.currentRoom.startingRoom = true;
        dungeon.currentRoom.playerIn = true;
        dungeon.currentRoom.updateStatus();
    }

    private double levelScale(int level)
    {
        return level * 0.02;
    }

    public void selectClass(Class classType)
    {
        selectedClass = classType;
        MAX_HEALTH = 100 * levelScale(selectedClass.HP);
        health = MAX_HEALTH;
    }

    public void move(String direction)
    {
        if (validateMove(direction))
        {
            monstersAttack();
            dungeon.currentRoom.playerLeave();
            Room prevRoom = dungeon.currentRoom;

            switch (direction.toUpperCase())
            {
                case "W" -> dungeon.currentRoom = dungeon.dungeon[dungeon.currentRoom.row - 1][dungeon.currentRoom.col];
                case "S" -> dungeon.currentRoom = dungeon.dungeon[dungeon.currentRoom.row + 1][dungeon.currentRoom.col];
                case "A" -> dungeon.currentRoom = dungeon.dungeon[dungeon.currentRoom.row][dungeon.currentRoom.col - 1];
                case "D" -> dungeon.currentRoom = dungeon.dungeon[dungeon.currentRoom.row][dungeon.currentRoom.col + 1];
            }

            dungeon.currentRoom.playerEnter();
            prevRoom.updateStatus();
            dungeon.currentRoom.updateStatus();
        }
        else
            Color.logError("invalid move");

        if (!dead)
        {
            System.out.println(dungeon.getDungeon());
            dungeon.currentRoom.getRoomContents();
        }
    }

    boolean validateMove(String direction)
    {
        return switch (direction.toUpperCase())
        {
            case "W" -> dungeon.currentRoom.row - 1 >= 0 && dungeon.dungeon[dungeon.currentRoom.row - 1][dungeon.currentRoom.col].getOpen();
            case "S" -> dungeon.currentRoom.row + 1 < dungeon.SIZE && dungeon.dungeon[dungeon.currentRoom.row + 1][dungeon.currentRoom.col].getOpen();
            case "A" -> dungeon.currentRoom.col - 1 >= 0 && dungeon.dungeon[dungeon.currentRoom.row][dungeon.currentRoom.col - 1].getOpen();
            case "D" -> dungeon.currentRoom.col + 1 < dungeon.SIZE && dungeon.dungeon[dungeon.currentRoom.row][dungeon.currentRoom.col + 1].getOpen();
            default -> false;
        };
    }

    public void map()
    {
        System.out.println(dungeon.getDungeon());
    }
    public void health()
    {
        System.out.printf("\uD83D\uDC96 Player health: %.2f%n", health);
    }
    public void durability()
    {
        System.out.printf("\uD83D\uDD2A Weapon uses: %d%n", equipped.uses);
    }
    public void contents()
    {
        dungeon.currentRoom.getRoomContents();
    }
    public void simulate()
    {
        double totalDamage = 0;
        for (Monster monster: dungeon.currentRoom.monsters)
            totalDamage += (monster.weapon.damage * levelScale(selectedClass.STR));
        System.out.printf("\uD83D\uDC98 simulated damage dealt: %.2f%n", totalDamage);
        if (health - totalDamage > 0)
            System.out.printf("✅ You survive with %.2f health%n", health - totalDamage);
        else
        {
            System.out.println("❌ You won't survive");
            health();
        }
    }
    public void examineMonster(int index)
    {
        if (index < 0 || index >= dungeon.currentRoom.monsters.size())
            Color.logError("invalid index");
        else
            System.out.println(dungeon.currentRoom.monsters.get(index));
    }
    public void examineItem(int index)
    {
        if (index < 0 || index >= dungeon.currentRoom.items.size())
            Color.logError("invalid index");
        else
            System.out.println(dungeon.currentRoom.items.get(index));
    }
    public void take(int index)
    {
        if (index < 0 || index >= dungeon.currentRoom.items.size())
            Color.logError("invalid index");
        else
        {
            inventory.add(dungeon.currentRoom.items.remove(index));

            monstersAttack();
        }
    }
    public void inventory(int index)
    {
        if (index < 0 || index >= inventory.size())
            Color.logError("invalid index");
        else
            System.out.println(inventory.get(index));
        System.out.println();
    }
    public void inventory()
    {
        for (int i = 0; i < inventory.size(); i++)
        {
            Item item = inventory.get(i);
            System.out.printf(item.getInfo() + " [%d]%n", i);
        }
        System.out.println();
    }
    public void use(int index)
    {
        if (index < 0 || index >= inventory.size())
            Color.logError("invalid index");
        else if (inventory.get(index) instanceof Weapon)
        {
            equipped = (Weapon) inventory.get(index);

            monstersAttack();
        }
        else
        {
            Potion potion = (Potion) inventory.get(index);
            if (potion.uses > 0)
            {
                if (potion.effectType.equalsIgnoreCase("healing"))
                {
                    double changeBy = Math.min(MAX_HEALTH - health, potion.power);
                    health += changeBy;
                    System.out.printf("\uD83D\uDCC8 Healed for %.2f%n", changeBy);
                    health();
                }
                else
                {
                    int changeBy = (int) Math.min(equipped.getMaxUses() - equipped.uses, potion.power);
                    equipped.uses += changeBy;
                    System.out.printf("\uD83D\uDCC8 Uses raised by %d%n", changeBy);
                    durability();
                }
                potion.uses--;

                monstersAttack();
            }
            System.out.printf("\uD83D\uDCC9 %s uses left: %d%n", potion.effectType, potion.uses);
            if (potion.uses <= 0)
                inventory.remove(index);
        }
    }
    public void attack(int index)
    {
        if (index < 0 || index >= dungeon.currentRoom.monsters.size())
            Color.logError("invalid index");
        else
        {
            Monster monster = dungeon.currentRoom.monsters.get(index);
            if (equipped == null)
            {
                monster.health--;
                System.out.println("\uD83D\uDCA5 You slapped the monster for 1 damage");
            }
            else
            {
                double changeBy = equipped.damage * levelScale(selectedClass.STR);
                monster.health -= changeBy;
                equipped.uses--;
                System.out.printf("\uD83D\uDCA5 You hit %s for %.2f damage%n", monster.type, changeBy);
                if (equipped.uses <= 0)
                {
                    inventory.remove(equipped);
                    System.out.println("\uD83D\uDCA2 Weapon broken");
                    equipped = null;
                }
            }
            if (monster.health <= 0)
            {
                dungeon.currentRoom.monsters.remove(monster);
                defeatedMonsters++;
                System.out.println("\uD83D\uDD2A Monster slain!");
            }
            else
                System.out.printf("\uD83D\uDC9C %s health: %.2f%n", monster.type, monster.health);

            monstersAttack();
        }
    }
    public boolean escape()
    {
        if (dungeon.currentRoom.startingRoom)
        {
            System.out.println("\uD83C\uDFC6 Successfully escaped! Monsters defeated: " + defeatedMonsters);
            return true;
        }
        else
        {
            System.out.printf("❗You must escape from the starting room! (%s)%n", dungeon.currentRoom.START);
            return false;
        }
    }

    private void monstersAttack()
    {
        for (Monster monster: dungeon.currentRoom.monsters)
        {
            double changeBy = monster.weapon.damage * levelScale(selectedClass.DEF);
            health -= changeBy;
            System.out.printf("\uD83D\uDC98 %s dealt %.2f damage%n", monster.type, changeBy);
        }

        if (!dungeon.currentRoom.monsters.isEmpty())
            health();
        if (health <= 0)
        {
            System.out.println("\uD83D\uDC94 You were defeated :(");
            dead = true;
        }
    }
}
