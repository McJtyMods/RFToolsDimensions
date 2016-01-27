package mcjty.rftoolsdim.api.dimlet;

public interface ISettingsBuilder {

    /**
     * Set the rarity (between 0 and 6) for this rule.
     *
     * @param rarity
     * @return
     */
    ISettingsBuilder rarity(int rarity);

    /**
     * Set the creation cost for this rule.
     *
     * @param createCost
     * @return
     */
    ISettingsBuilder createCost(int createCost);

    /**
     * Set the maintenance cost for this rule. If this is negative
     * then this is interpreted as a percentage cost to subtract from the dimension.
     *
     * @param maintainCost
     * @return
     */
    ISettingsBuilder maintainCost(int maintainCost);

    /**
     * Set the tick cost that using dimlets that match this rule add to the
     * total creation time of the dimension.
     *
     * @param tickCost
     * @return
     */
    ISettingsBuilder tickCost(int tickCost);

    /**
     * If true then dimlets matching this rule can be included in worldgen (random
     * dimensions). If false then this is not allowed and only manual inserted
     * dimlets will work (if that's allowed too).
     *
     * @param worldgen
     * @return
     */
    ISettingsBuilder worldgen(boolean worldgen);

    /**
     * If true then it is possible to create (and find) dimlets that match this
     * rule. If false then this is not possible and the dimlets can only be generated
     * in random RFTools dimensions (unless 'worldgen' is disabled too).
     *
     * @param dimlet
     * @return
     */
    ISettingsBuilder dimlet(boolean dimlet);
}
