package orderbook.orderbook.parameters.values;


public enum ItemSpecial {
    @SuppressWarnings("SpellCheckingInspection") MAINHAND,
    OFFHAND,
    NOTHING;


    public String nameLower() {
        return name().toLowerCase();
    }

    public static ItemSpecial matchParameter(String name) {
        try {
            return valueOf(name.toUpperCase());
        }catch(Exception e) {
            return null;
        }

    }
}