package fr.mael.fk.handler;

import org.bukkit.ChatColor;

public enum Step {
    LOBBY(true, ChatColor.GREEN + "Attente"),
    IN_GAME(false, "En jeu"),
    POST_GAME(false, ChatColor.RED + "Partie terminée");

    private static Step currentStep;

    public static boolean canJoin() {
        return Step.currentStep.canJoin;
    }

    public static String getMOTD() {
        return Step.currentStep.motd;
    }

    public static boolean isStep(Step step) {
        return Step.currentStep == step;
    }

    public static void setCurrentStep(Step currentStep) {
        Step.currentStep = currentStep;
    }

    private boolean canJoin;

    private String motd;

    Step(boolean canJoin, String motd) {
        this.canJoin = canJoin;
        this.motd = motd;
    }
}
