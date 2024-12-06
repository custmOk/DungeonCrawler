import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Player
{
    Dungeon dungeon;
    List<Item> inventory;
    Item equipped;
    Class cls;
    Element affinity;
    Random rand = new Random();
    double health;
    int defeatedMonsters;
    int coins;
    double mana;
    boolean dead;
    private double MAX_HEALTH;
    private double MAX_MANA;

    public Player(Dungeon dungeon) throws IOException
    {
        inventory = new ArrayList<>();
        this.dungeon = dungeon;
        inventory.add(dungeon.generateRandomWeapon());
        inventory.add(dungeon.generateRandomPotion());
        inventory.add(dungeon.generateRandomSpell());

        equipped = inventory.getFirst();
        MAX_HEALTH = 100;
        health = MAX_HEALTH;
        MAX_MANA = 50;
        mana = MAX_MANA;

        Random rand = new Random(dungeon.seed);
        dungeon.currentRoom = dungeon.openRooms.get(rand.nextInt(dungeon.openRooms.size()));
        dungeon.currentRoom.startingRoom = true;
        dungeon.currentRoom.playerIn = true;
        dungeon.currentRoom.updateStatus();
    }

    private double levelScaleStrDefHpIntMatMdf(int level)
    {
        return level * 0.02;
    }

    private double levelScaleDexAgiLck(int level)
    {
        return level * 0.4;
    }

    public void selectClass(Class classType)
    {
        cls = classType;
        MAX_HEALTH = 100 * levelScaleStrDefHpIntMatMdf(cls.HP);
        health = MAX_HEALTH;
        MAX_MANA = 50 * levelScaleStrDefHpIntMatMdf(cls.INT);
        mana = MAX_MANA;
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
        else Color.logError("invalid move");

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
            case "W" ->
                    dungeon.currentRoom.row - 1 >= 0 && dungeon.dungeon[dungeon.currentRoom.row - 1][dungeon.currentRoom.col].getOpen();
            case "S" ->
                    dungeon.currentRoom.row + 1 < dungeon.SIZE && dungeon.dungeon[dungeon.currentRoom.row + 1][dungeon.currentRoom.col].getOpen();
            case "A" ->
                    dungeon.currentRoom.col - 1 >= 0 && dungeon.dungeon[dungeon.currentRoom.row][dungeon.currentRoom.col - 1].getOpen();
            case "D" ->
                    dungeon.currentRoom.col + 1 < dungeon.SIZE && dungeon.dungeon[dungeon.currentRoom.row][dungeon.currentRoom.col + 1].getOpen();
            default -> false;
        };
    }

    public void map()
    {
        System.out.println(dungeon.getDungeon());
    }

    public void pouch()
    {
        System.out.printf("\uD83E\uDE99 Coins Collected: %d%n", coins);
    }

    public void health()
    {
        System.out.printf("\uD83D\uDC96 Player health: %.2f%n", health);
    }

    public void mana()
    {
        System.out.printf("\uD83E\uDE84 Player mana: %.2f%n", mana);
    }

    public void status()
    {
        health();
        mana();
    }

    public void player()
    {
        classInfo();
        System.out.printf("Element Affinity: %s %s%n", affinity.icon, affinity.name);
    }

    public void classInfo()
    {
        System.out.printf("Selected Class: %s%nStats:%n\tStrength - %d%n\tDefense - %d%n\tDexterity - %d%n" +
                                  "\tAgility - %d%n\tIntelligence - %d%n\tHealth Points - %d%n" + "\tLuck - " + "%d%n"
                                  + "\tMagic Attack - %d%n\tMagic Defense - %d%n",
                          cls.name,
                          cls.STR,
                          cls.DEF,
                          cls.DEX,
                          cls.AGI,
                          cls.INT,
                          cls.HP,
                          cls.LCK,
                          cls.MAT,
                          cls.MDF);
    }

    public void durability()
    {
        System.out.printf("\uD83D\uDD2A Weapon uses: %d%n", equipped.uses);
    }

    public void contents()
    {
        dungeon.currentRoom.getRoomContents();
    }

    public void examineMonster(int index)
    {
        if (index < 0 || index >= dungeon.currentRoom.monsters.size()) Color.logError("invalid index");
        else System.out.println(dungeon.currentRoom.monsters.get(index));
    }

    public void examineItem(int index)
    {
        if (index < 0 || index >= dungeon.currentRoom.items.size()) Color.logError("invalid index");
        else System.out.println(dungeon.currentRoom.items.get(index));
    }

    public void take(int index)
    {
        if (index < 0 || index >= dungeon.currentRoom.items.size()) Color.logError("invalid index");
        else
        {
            inventory.add(dungeon.currentRoom.items.remove(index));

            monstersAttack();
        }
    }

    public void inventory(int index)
    {
        if (index < 0 || index >= inventory.size()) Color.logError("invalid index");
        else System.out.println(inventory.get(index));
        System.out.println();
    }

    public void inventory()
    {
        for (int i = 0; i < inventory.size(); i++)
        {
            Item item = inventory.get(i);
            System.out.printf((item == equipped ? "\uD83C\uDF1F " : "") + item.getInfo() + " [%d]%n", i);
        }
        System.out.println();
    }

    public void use(int index)
    {
        if (index < 0 || index >= inventory.size()) Color.logError("invalid index");
        else if (inventory.get(index) instanceof Weapon weapon)
        {
            equipped = weapon;

            monstersAttack();
        }
        else if (inventory.get(index) instanceof Spell spell)
        {
            equipped = spell;

            monstersAttack();
        }
        else if (inventory.get(index) instanceof Potion potion)
        {
            if (potion.uses > 0)
            {
                if (potion.effectType.equalsIgnoreCase("healing"))
                {
                    double changeBy = Math.min(MAX_HEALTH - health, potion.power);
                    if (changeBy == 0)
                    {
                        System.out.println("\uD83D\uDCAF Already at max health");
                        return;
                    }
                    health += changeBy;
                    System.out.printf("\uD83D\uDCC8 Healed for %.2f%n", changeBy);
                    health();
                }
                else
                {
                    int changeBy = (int) Math.min(equipped.getMaxUses() - equipped.uses, potion.power);
                    if (changeBy == 0)
                    {
                        System.out.println("\uD83D\uDCAF Already at max uses");
                        return;
                    }
                    equipped.uses += changeBy;
                    System.out.printf("\uD83D\uDCC8 Uses raised by %d%n", changeBy);
                    durability();
                }
                potion.uses--;

                monstersAttack();
            }
            System.out.printf("\uD83D\uDCC9 %s uses left: %d%n", potion.effectType, potion.uses);
            if (potion.uses <= 0) inventory.remove(index);
        }
    }

    public void attack(int index)
    {
        if (index < 0 || index >= dungeon.currentRoom.monsters.size()) Color.logError("invalid index");
        else
        {
            Monster monster = dungeon.currentRoom.monsters.get(index);
            switch (equipped)
            {
                case null ->
                {
                    monster.health--;
                    System.out.println("\uD83D\uDCA5 You slapped the monster for 1 damage");
                }
                case Weapon weapon ->
                {
                    double baseChange = weapon.damage * levelScaleStrDefHpIntMatMdf(cls.STR);
                    boolean isCritical = rand.nextInt(100) < levelScaleDexAgiLck(cls.DEX);
                    double changeBy = baseChange * (isCritical ? 2 : 1);
                    monster.health -= changeBy;
                    weapon.uses--;
                    System.out.printf("\uD83D\uDCA5 You hit %s for %.2f%s damage%n",
                                      monster.type,
                                      changeBy,
                                      isCritical ? " " + Color.critical("critical") : "");
                    if (weapon.uses <= 0)
                    {
                        inventory.remove(weapon);
                        System.out.println("\uD83D\uDCA2 Weapon broken");
                        equipped = null;
                    }
                }
                case Spell spell ->
                {
                    if (mana < spell.manaCost) System.out.println("Not enough mana");
                    else
                    {
                        double baseChange = spell.damage * levelScaleStrDefHpIntMatMdf(cls.MAT);
                        String effectiveness = switch (monster.element)
                        {
                            case FIRE, WATER, NATURE -> spell.element.name.equals(monster.element.name) ?
                                    "normal" :
                                    spell.element.strong.equals(monster.element.name) ? "strengthened" : "weakened";
                            case null -> "boosted";
                        };
                        double changeFactor = switch (effectiveness)
                        {
                            case "strengthened" -> 2;
                            case "weakened" -> 0.5;
                            case "boosted" -> 1.25;
                            default -> 1;
                        };
                        double changeBy = baseChange * changeFactor;
                        monster.health -= changeBy;
                        mana -= spell.manaCost;
                        System.out.printf("%s You hit %s for %.2f %s damage%n",
                                          spell.icon,
                                          monster.type,
                                          changeBy,
                                          effectiveness);
                    }
                }
                default -> System.out.println("How did you get here?");
            }
            if (monster.health <= 0)
            {
                dungeon.currentRoom.monsters.remove(monster);
                defeatedMonsters++;
                System.out.println("\uD83D\uDD2A Monster slain!");
                int coinsDropped = rand.nextInt(6) + 5;
                coins += coinsDropped;
                System.out.printf("\uD83E\uDE99 Earned %d coins%n", coinsDropped);
                boolean isLucky = rand.nextInt(100) < levelScaleDexAgiLck(cls.LCK);
                if (isLucky)
                {
                    int extraCoins = rand.nextInt(3) + 3;
                    coins += extraCoins;
                    System.out.printf("\uD83E\uDE99 You earned a bonus %d coins%n", extraCoins);
                }
            }
            else System.out.printf("\uD83D\uDC9C %s health: %.2f%n", monster.type, monster.health);

            monstersAttack();
        }
    }

    public boolean escape()
    {
        if (dungeon.currentRoom.startingRoom)
        {
            System.out.printf(
                    "\uD83C\uDFC6 Successfully Escaped!%n\uD83D\uDC7FMonsters Defeated: %d%n\uD83E\uDE99Coins " +
                            "Collected: %d%n",
                    defeatedMonsters,
                    coins);
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
        for (Monster monster : dungeon.currentRoom.monsters)
        {
            String effectiveness = switch (monster.element)
            {
                case FIRE, WATER, NATURE -> affinity.name.equals(monster.element.name) ?
                        "normal" :
                        affinity.weak.equals(monster.element.name) ? "strengthened" : "weakened";
                case null -> "normal";
            };
            double affinityScaling = switch (effectiveness)
            {
                case "strengthened" -> 2;
                case "weakened" -> 0.5;
                default -> 1;
            };
            double baseChange = monster.element == null ?
                    monster.weapon.damage * levelScaleStrDefHpIntMatMdf(cls.DEF) :
                    monster.weapon.damage * levelScaleStrDefHpIntMatMdf(cls.MDF) * affinityScaling;
            boolean dodged = rand.nextInt(100) < levelScaleDexAgiLck(cls.AGI);
            double changeBy = baseChange * (dodged ? 0 : 1);
            health -= changeBy;
            if (dodged)
                System.out.printf("\uD83D\uDCA8 You dodged %s's attack for %.2f damage%n", monster.type, baseChange);
            else System.out.printf("\uD83D\uDC98 %s dealt %.2f %s damage%n", monster.type, changeBy, effectiveness);
        }

        if (!dungeon.currentRoom.monsters.isEmpty()) health();
        if (health <= 0)
        {
            System.out.println("\uD83D\uDC94 You were defeated :(");
            dead = true;
        }
    }
}
