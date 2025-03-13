import java.util.List;
import java.util.ArrayList;

public class Recycler
        extends Room
        implements Purchasable
{
    List<Item> contents = new ArrayList<>();

    public Recycler(boolean open, int number, int row, int col, Dungeon dungeon)
    {
        super(open, number, row, col, dungeon);
    }

    public void recycleItem(Item item)
    {
        contents.add(item);
    }

    public List<Item> getContents()
    {
        return contents;
    }
}
