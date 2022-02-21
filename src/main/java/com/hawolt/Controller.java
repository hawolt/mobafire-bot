package com.hawolt;

import com.hawolt.action.Bot;
import com.hawolt.action.view.ViewBot;
import com.hawolt.action.vote.VoteBot;
import com.hawolt.error.AntiCaptchaException;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Controller {

    public static final String MOBAFIRE_SITEKEY = "6LdvsQcaAAAAACOlH4ZL6SDtWY2FDSuNjwLdl2QJ";

    public static AntiCaptcha ANTI_CAPTCHA;

    private static final ExecutorService CACHED_SERVICE = Executors.newCachedThreadPool();
    private static final List<Bot> list = new ArrayList<>();

    public static JsonSource CONFIG;

    static {
        try {
            CONFIG = JsonSource.of(Paths.get("config.json"));
        } catch (IOException e) {
            Logger.error("Unable to locate config.json");
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        System.out.println("Mobafire Bot - V1.0");
        Scanner scanner = new Scanner(System.in);
        int option;
        do {
            print();
            option = readInt(scanner);
            switch (option) {
                case 1:
                    startViewBot(scanner);
                    break;
                case 2:
                case 3:
                    if (ANTI_CAPTCHA == null) {
                        System.out.print("AntiCaptcha: ");
                        try {
                            ANTI_CAPTCHA = new AntiCaptcha(scanner.nextLine());
                            System.out.format("Current Balance $%05.2f\n", ANTI_CAPTCHA.getCurrentBalance());
                        } catch (AntiCaptchaException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (ANTI_CAPTCHA != null) {
                        startVoteBot(option == 2, scanner);
                    }
                    break;
                case 4:
                    printTaskList();
                    break;
                case 5:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Unknown option " + option);
                    break;
            }
        } while (true);
    }

    private static int readInt(Scanner scanner) {
        String line = scanner.nextLine();
        if (!line.matches("[0-9]+")) return 0;
        return Integer.parseInt(line);
    }

    private static void startViewBot(Scanner scanner) {
        System.out.print("Link to guide: ");
        String target = scanner.nextLine();
        System.out.print("Amount of Views: ");
        int amount = readInt(scanner);
        System.out.print("Max delay between Views in seconds: ");
        int max = readInt(scanner);
        ViewBot bot = ViewBot.init(amount, target, max);
        list.add(bot);
        CACHED_SERVICE.execute(bot);
    }

    private static void startVoteBot(boolean up, Scanner scanner) {
        System.out.print("Link to guide: ");
        String target = scanner.nextLine();
        System.out.print("Amount of Votes: ");
        int amount = readInt(scanner);
        System.out.print("Max delay between Votes in minutes: ");
        int max = readInt(scanner);
        VoteBot bot = VoteBot.init(up, amount, target, max);
        list.add(bot);
        CACHED_SERVICE.execute(bot);
    }

    private static void printTaskList() {
        for (Bot task : list) {
            String name = task.getTaskType().name();
            String progress = String.format("%05.2f%%", task.getPercentProgress());
            String target = task.getTarget();
            System.out.format("%-4s %-7s %06d/%06d %s\n", name, progress, task.getCurrent(), task.getGoal(), target);
        }
    }

    private static void print() {
        System.out.println("1) Bot Views");
        System.out.println("2) Bot Upvotes");
        System.out.println("3) Bot Downvotes");
        System.out.println("4) View Tasks");
        System.out.println("5) Shutdown");
        System.out.print("Selection: ");
    }
}
