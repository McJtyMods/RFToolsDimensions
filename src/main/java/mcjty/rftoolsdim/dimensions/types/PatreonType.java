package mcjty.rftoolsdim.dimensions.types;

public enum PatreonType {
    PATREON_FIREWORKS(0),
    PATREON_SICKMOON(1),
    PATREON_SICKSUN(2),
    PATREON_PINKPILLARS(3),
    PATREON_RABBITMOON(4),
    PATREON_RABBITSUN(5),
    PATREON_PUPPETEER(6),
    PATREON_LAYEREDMETA(7),
    PATREON_COLOREDPRISMS(8),
    PATREON_DARKCORVUS(9),
    PATREON_TOMWOLF(10),
    PATREON_KENNEY(11);

    private final int bit;

    PatreonType(int bit) {
        this.bit = bit;
    }

    public int getBit() {
        return bit;
    }
}
