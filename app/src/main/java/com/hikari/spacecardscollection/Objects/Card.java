package com.hikari.spacecardscollection.Objects;

public class Card {

    private int agility;
    private int attackDmg;
    private String cardImg;
    private int cardLvl;
    private String cardName;
    private String cardType;
    private int hp;

    /**
     *
     * @param name
     * @param type
     * @param cardImg
     * @param agility
     * @param attackDamage
     * @param cardLevel
     * @param hp
     */
    public Card(String name, String type, String cardImg, int agility, int attackDamage, int cardLevel, int hp) {
        this.cardName = name;
        this.cardType = type;
        this.cardImg = cardImg;
        this.agility = agility;
        this.attackDmg = attackDamage;
        this.cardLvl = cardLevel;
        this.hp = hp;
    }

    public Card() {
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardImg() {
        return cardImg;
    }

    public void setCardImg(String cardImg) {
        this.cardImg = cardImg;
    }

    public int getAgility() {
        return agility;
    }

    public void setAgility(int agility) {
        this.agility = agility;
    }

    public int getAttackDmg() {
        return attackDmg;
    }

    public void setAttackDmg(int attackDmg) {
        this.attackDmg = attackDmg;
    }

    public int getCardLvl() {
        return cardLvl;
    }

    public void setCardLvl(int cardLvl) {
        this.cardLvl = cardLvl;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }
}
