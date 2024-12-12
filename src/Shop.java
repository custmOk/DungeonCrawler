import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Shop extends Room
{
    List<Item> contents = new ArrayList<>();

    public Shop(boolean open, int number, int row, int col, Dungeon dungeon)
    {
        super(open, number, row, col, dungeon);
        Random rand = new Random();
        for (int i = 0; i < 5; i++)
        {
            int choice = rand.nextInt(3);
            Rarity rarity = switch (rand.nextInt(3))
            {
                case 0 -> Rarity.COMMON;
                case 1 -> Rarity.UNCOMMON;
                case 2 -> Rarity.RARE;
                default -> null;
            };
            contents.add(switch (choice)
            {
                case 0 -> dungeon.generateRandomWeapon(rarity);
                case 1 -> dungeon.generateRandomPotion(rarity);
                case 2 -> dungeon.generateRandomSpell(rarity);
                default -> null;
            });
        }
    }
}