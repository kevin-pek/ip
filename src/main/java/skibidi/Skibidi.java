package skibidi;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

import skibidi.command.AbstractCommand;

public class Skibidi {
    TaskList taskList;
    Storage storage;
    Ui ui;
    CommandParser parser;

    public Skibidi(String dataPath) {
        this.storage = new Storage(dataPath);
        this.taskList = new TaskList(storage.loadTasksFromDisk());
        this.ui = new Ui();
        this.parser = new CommandParser();
    }

    public void start() {
        ui.displayWelcome();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print("> ");
            try {
                String line = reader.readLine();
                if (parser.isExit(line)) {
                    ui.printExitMessage();
                    break;
                }
                Optional<AbstractCommand> command = parser.parseCommand(line);
                Optional<String> result = command.flatMap(cmd -> cmd.execute(taskList, storage, ui));
                result.ifPresent(msg -> System.out.println(msg));
                storage.saveTasksToDisk(taskList);
            } catch (IOException err) {
                System.out.println(err.toString());
            }
            ui.printSeparator();
        }
    }

    public static void main(String[] args) {
        Skibidi bot = new Skibidi("data/tasks.txt");
        bot.start();
    }
}
