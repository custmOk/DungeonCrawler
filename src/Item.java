public class Item
{
    String name;
    int uses;
    String icon;
    private final int maxUses;

    public Item(String name, int uses, String icon)
    {
        this.name = name;
        this.uses = uses;
        this.icon = icon;
        this.maxUses = uses;
    }

    public int getMaxUses()
    {
        return maxUses;
    }

    public String getInfo()
    {
        return String.format("%s %s", icon, name);
    }

    public String toString()
    {
        return "Uses: " + uses;
    }
}
