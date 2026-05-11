package net.qacidp.goldrush.client;

public class PlayerGoldData {
    private float totalGold = 0f;
    private float totalMoney = 0f;

    public void addGold(float amount) { this.totalGold += amount; }
    public float getTotalGold() { return totalGold; }
    // später: addMoney, getMoney etc.
}