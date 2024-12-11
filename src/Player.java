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

    public Player(Dungeon dungeon)
    {
        inventory = new ArrayList<>();
        this.dungeon = dungeon;
        inventory.add(dungeon.generateRandomWeapon(Rarity.COMMON));
        inventory.add(dungeon.generateRandomPotion(Rarity.COMMON));
        inventory.add(dungeon.generateRandomSpell(Rarity.COMMON));

        equipped = inventory.getFirst();
        MAX_HEALTH = 100;
        health = MAX_HEALTH;
        MAX_MANA = 50;
        mana = MAX_MANA;

        Random rand = new Random(dungeon.seed);
        do
        {
            dungeon.currentRoom = dungeon.openRooms.get(rand.nextInt(dungeon.openRooms.size()));
        } while (dungeon.currentRoom.type != RoomType.NORMAL);

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
        else TerminalColor.logError("invalid move");

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
        System.out.printf("❤️ Player health: %.2f / %.2f%n", health, MAX_HEALTH);
    }

    public void mana()
    {
        System.out.printf("\uD83E\uDE84 Player mana: %.2f / %.2f%n", mana, MAX_MANA);
    }

    public void durability()
    {
        System.out.printf("\uD83D\uDD2A Weapon uses: %d / %d%n", equipped.uses, equipped.maxUses);
    }

    public void status()
    {
        health();
        mana();
    }

    public void player()
    {
        classInfo();
        System.out.printf("Element Affinity: %s %s%n", affinity.icon, affinity);
    }

    public void classInfo()
    {
        System.out.printf("Selected Class: %s%nStats:%n\tStrength - %d%n\tDefense - %d%n\tDexterity - %d%n" +
                                  "\tAgility - %d%n\tIntelligence - %d%n\tHealth Points - %d%n" + "\tLuck - " + "%d%n"
                                  + "\tMagic Attack - %d%n\tMagic Defense - %d%n",
                          cls,
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

    public void contents()
    {
        dungeon.currentRoom.getRoomContents();
    }

    public void examineMonster(int index)
    {
        if (index < 0 || index >= dungeon.currentRoom.monsters.size()) TerminalColor.logError("invalid index");
        else System.out.println(dungeon.currentRoom.monsters.get(index));
    }

    public void examineItem(int index)
    {
        if (index < 0 || index >= dungeon.currentRoom.items.size()) TerminalColor.logError("invalid index");
        else System.out.println(dungeon.currentRoom.items.get(index));
    }

    public void take(int index)
    {
        if (index < 0 || index >= dungeon.currentRoom.items.size()) TerminalColor.logError("invalid index");
        else
        {
            Item item = dungeon.currentRoom.items.remove(index);
            inventory.add(item);
            System.out.printf("%s added to inventory%n", item.getInfo());

            monstersAttack();
        }
    }

    public void inventory(int index)
    {
        if (index < 0 || index >= inventory.size()) TerminalColor.logError("invalid index");
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
        if (index < 0 || index >= inventory.size()) TerminalColor.logError("invalid index");
        else if (inventory.get(index) instanceof Weapon weapon)
        {
            equipped = weapon;
            System.out.printf("%s equipped%n", equipped.getInfo());

            monstersAttack();
        }
        else if (inventory.get(index) instanceof Spell spell)
        {
            equipped = spell;
            System.out.printf("%s equipped%n", equipped.getInfo());

            monstersAttack();
        }
        else if (inventory.get(index) instanceof Potion potion)
        {
            if (potion.uses > 0)
            {
                switch (potion.type)
                {
                    case HEALING ->
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
                    case REPAIRING ->
                    {
                        int changeBy = (int) Math.min(equipped.maxUses - equipped.uses, potion.power);
                        if (changeBy == 0)
                        {
                            System.out.println("\uD83D\uDCAF Already at max uses");
                            return;
                        }
                        equipped.uses += changeBy;
                        System.out.printf("\uD83D\uDCC8 Uses raised by %d%n", changeBy);
                        durability();
                    }
                    case MANA ->
                    {
                        int changeBy = (int) Math.min(MAX_MANA - mana, potion.power);
                        if (changeBy == 0)
                        {
                            System.out.println("\uD83D\uDCAF Already at max mana");
                            return;
                        }
                        mana += changeBy;
                        System.out.printf("\uD83D\uDCC8 Mana raised by %d%n", changeBy);
                        mana();
                    }
                }
                potion.uses--;

                monstersAttack();
            }
            System.out.printf("\uD83D\uDCC9 %s uses left: %d%n", potion.type, potion.uses);
            if (potion.uses <= 0) inventory.remove(index);
        }
    }

    public void attack(int index)
    {
        if (index < 0 || index >= dungeon.currentRoom.monsters.size()) TerminalColor.logError("invalid index");
        else
        {
            Monster monster = dungeon.currentRoom.monsters.get(index);
            boolean magic = false, hit = false;
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
                                      isCritical ? " " + TerminalColor.critical("critical") : "");
                    if (weapon.uses == 1) System.out.printf("%s has one more use%n", weapon.getInfo());
                    if (weapon.uses <= 0)
                    {
                        inventory.remove(weapon);
                        System.out.println("\uD83D\uDCA2 Weapon broken");
                        equipped = null;
                    }
                }
                case Spell spell ->
                {
                    magic = true;
                    if (mana < spell.manaCost) System.out.println("Not enough mana");
                    else
                    {
                        hit = true;
                        double baseChange = spell.damage * levelScaleStrDefHpIntMatMdf(cls.MAT);
                        String effectiveness = monster.element == null ?
                                "boosted" :
                                spell.element.name.equals(monster.element.name) ?
                                        "normal" :
                                        spell.element.strong.equals(monster.element.name) ? "strengthened" : "weakened";
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

            if (!magic || hit) monstersAttack();
        }
    }

    public void shop()
    {
        if (dungeon.currentRoom.type == RoomType.SHOP)
        {
            Shop room = (Shop) dungeon.currentRoom;
            for (int i = 0; i < room.contents.size(); i++)
            {
                Item item = room.contents.get(i);
                System.out.printf("%s %s - %d coins [%d]%n", item.icon, item.name, item.price, i);
            }
        }
        else System.out.printf("❗You must be in a shop room! (%s)%n", dungeon.currentRoom.SHOP);
    }

    public void shop(int index)
    {
        if (dungeon.currentRoom.type == RoomType.SHOP)
        {
            Shop room = (Shop) dungeon.currentRoom;
            Item item = room.contents.get(index);
            System.out.printf(item + " | \uD83E\uDE99 Price: %d%n", item.price);
        }
        else System.out.printf("❗You must be in a shop room! (%s)%n", dungeon.currentRoom.SHOP);
    }

    public void buy(int index)
    {
        if (dungeon.currentRoom.type == RoomType.SHOP)
        {
            Shop room = (Shop) dungeon.currentRoom;
            if (index < 0 || index >= room.contents.size()) TerminalColor.logError("invalid index");
            else
            {
                Item item = room.contents.get(index);
                if (coins < item.price) System.out.println("Not enough coins");
                else
                {
                    inventory.add(item);
                    room.contents.remove(item);
                    int changeBy = Math.max(0, coins - item.price);
                    coins -= changeBy;
                    System.out.printf(item.getInfo() + " bought for %d coins%n", item.price);
                }
            }
        }
        else System.out.printf("❗You must be in a shop room! (%s)%n", dungeon.currentRoom.SHOP);
    }

    public void sell(int index)
    {
        if (dungeon.currentRoom.type == RoomType.SHOP)
        {
            if (index < 0 || index >= inventory.size()) TerminalColor.logError("invalid index");
            else
            {
                Item item = inventory.get(index);
                int sellValue = ((item.price + 1) / 2);
                System.out.printf("%s sold for %d coins%n", item.getInfo(), sellValue);
                coins += sellValue;
                if (item == equipped) equipped = null;
                inventory.remove(item);
            }
        }
        else System.out.printf("❗You must be in a shop room! (%s)%n", dungeon.currentRoom.SHOP);
    }

    public boolean escape()
    {
        if (dungeon.currentRoom.startingRoom)
        {
            System.out.printf(
                    "\uD83C\uDFC6 Successfully Escaped!%n\uD83D\uDC7F Monsters Defeated: %d%n\uD83E\uDE99 Coins " +
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

    public void cheat()
    {
        coins = 100000;
        health = 100000;
        ((Weapon) equipped).damage = 100000;
        equipped.uses = 100000;
    }

    private void monstersAttack()
    {
        for (Monster monster : dungeon.currentRoom.monsters)
        {
            String effectiveness = monster.element == null ?
                    "normal" :
                    affinity.name.equals(monster.element.name) ?
                            "normal" :
                            affinity.weak.equals(monster.element.name) ? "strengthened" : "weakened";
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
