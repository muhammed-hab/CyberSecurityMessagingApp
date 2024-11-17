package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ConsoleReaderImpl implements ConsoleReader {

    @Override
    public void listenForConsole(ConsoleCommandHandler callback) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input;

        System.out.println("Console is ready. Type your command:");

        while (true) {
            System.out.print("> ");
            input = reader.readLine(); // Read user input from stdin
            if (input == null || input.equalsIgnoreCase("exit")) {
                System.out.println("Exiting console...");
                break;
            }

            try {
                // Process the input using the callback (ConsoleCommandHandler)
                String output = callback.handleConsoleCommand(input);
                System.out.println(output); // Print the output to stdout
            } catch (Exception e) {
                System.err.println("Error processing command: " + e.getMessage());
            }
        }
    }
}
