package fr.mael.fk.handler;

public enum State {
    NONE("Attente", false),
    PREPARATION("Préparation", false),
    PVP("PvP", true),
    ASSAULT("Assaut", true),
    DEATHMATCH("Deathmatch", true);

    private static State state;

    private String name;
    private boolean pvp;

    public static State getState() {
        return State.state;
    }

    public static void setState(State state) {
        State.state = state;
    }

    public static boolean isState(State state) {
        return State.state == state;
    }

    private State(String name, boolean pvp) {
        this.name = name;
        this.pvp = pvp;
    }

    public String getName() {
        return name;
    }

    public boolean isPvp() {
        return pvp;
    }
}
