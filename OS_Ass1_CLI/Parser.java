/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OS_Ass1_CLI;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

import static OS_Ass1_CLI.Terminal.concatenate;
import static OS_Ass1_CLI.Terminal.initializeListOfCommands;
import static OS_Ass1_CLI.Terminal.lookup;
import static OS_Ass1_CLI.Terminal.validate;

public class Parser {

    public static void parser(File file, String command, Scanner input) {
        initializeListOfCommands();
        while (true) {
            System.out.print(file.getAbsolutePath() + " ");
            command = input.nextLine(); // the input
            String[] arrOfCommands = command.split(";");
            for (int j = 0; j < arrOfCommands.length; j++) {
                command = arrOfCommands[j];
                boolean isValid = false;
                int idx = -1;
                String argument = new String(); // for the argument
                String mainCommand = new String(); // for the commands itself(cd, ls, etc.)
                for (int i = 0; i < command.length(); i++) {
                    if (command.charAt(i) == ' ') {
                        mainCommand = command.substring(0, i);
                        if (validate(mainCommand)) {
                            idx = i + 1;
                            argument = command.substring(idx, command.length());
                            isValid = true;
                            break;
                        }
                    } else if (i == command.length() - 1) {
                        mainCommand = command.substring(0, i + 1);
                        if (validate(mainCommand)) {
                            isValid = true;
                            break;
                        } 
                    }
                }

                if (!isValid) {
                    System.out.println("command not exist!");
                    continue;
                }

                if (command.indexOf('>') != -1) {
                    if (command.indexOf('>') != command.lastIndexOf('>') && command.lastIndexOf('>') - command.indexOf('>') != 1) { // if neither two consecutive nor only one
                        System.out.println("Invalid usage of operators (>) and (>>)");
                        continue;
                    }
                    File arr[] = file.listFiles();
                    
                    String file2Name = command.substring(command.lastIndexOf(' ') + 1, command.length());
                    String checker = lookup(arr, file2Name);
                    if (checker.equals("Not found")) {
                        System.out.println("No such file named: " + file2Name);
                        continue;
                    }
                    File f2 = new File(checker);
                    switch (mainCommand) {
                        case "pwd":
                            if (command.indexOf('>') == command.lastIndexOf('>')) // only one then truncate
                            {
                                try {
                                    Files.write(f2.toPath(), file.getAbsolutePath().getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    Files.write(f2.toPath(), file.getAbsolutePath().getBytes(), StandardOpenOption.APPEND);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                        case "ls":
                            if (command.indexOf('>') == command.lastIndexOf('>')) {
                                try {
                                    Files.write(f2.toPath(), Arrays.toString(arr).getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    Files.write(f2.toPath(), Arrays.toString(arr).getBytes(), StandardOpenOption.APPEND);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                        case "cat":
                            String file1Name = command.substring(command.indexOf(' ') + 1, command.indexOf('>') - 1);
                            String checker1 = lookup(arr, file1Name);
                            if (checker1.equals("Not found")) {
                                System.out.println("No such file named: " + file1Name);
                                continue;
                            }
                            File f1 = new File(checker1);
                            StringBuilder file1Content = new StringBuilder();
                            try {
                        try (Scanner input1 = new Scanner(f1)) {
                            while (input1.hasNextLine()) {
                                file1Content.append(input1.nextLine());
                                file1Content.append("\n");
                            }
                        }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            if (command.indexOf('>') == command.lastIndexOf('>')) {
                                try {
                                    Files.write(f2.toPath(), file1Content.toString().getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    Files.write(f2.toPath(), file1Content.toString().getBytes(), StandardOpenOption.APPEND);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                        default:
                            break;
                    }
                } else if (mainCommand.equals("cd")) {
                    if (file.equals(Terminal.changeDirectory(argument,
                            file.getAbsolutePath())) && idx != -1) {
                        System.out.println("invalid path!");
                    } else if (idx == -1) // means that cd doesn't have arguments, so we change to default directory(C:\)
                    {
                        file = Terminal.changeDirectory("C:\\", file.getAbsolutePath());
                    } else // argument is valid so change the directory to it
                    {
                        file = Terminal.changeDirectory(argument, file.getAbsolutePath());
                    }
                } else if (mainCommand.equals("pwd")) {
                    if (argument.length() > 0) // to handle if pwd is entered with argument
                    {
                        System.out.println("pwd takes no args!");
                    } else {
                        Terminal.pwd(file); // display the absolute path of current directory
                    }
                } else if (mainCommand.equals("exit")) {
                    Terminal.terminate();
                    return;
                } else if (command.equals("man ls")) {
                    System.out.println("Name");
                    System.out.println("ls - list directory contents");
                    System.out.println("description");
                    System.out.println("Sort all files and directories in current directory alphabetically "
                            + "and list them\n-a do not ignore enteries starting with .(hidden files)");
                } else if (mainCommand.equals("ls")) {
                    if (argument.length() > 0) {
                        if (!argument.equals("-a")) {
                            System.out.println("Invalid argument");
                        } else {
                            Terminal.listAllFiles(file.getAbsolutePath(), true);
                        }
                    } else {
                        Terminal.listAllFiles(file.getAbsolutePath(), false);
                    }
                } else if (mainCommand.equals("cp")) {
                    File arr[] = file.listFiles();
                    try {
                        Terminal.copyFile(arr, argument);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (mainCommand.equals("mv")) {
                    File arr[] = file.listFiles();
                    try {
                        Terminal.moveFile(arr, argument);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (mainCommand.equals("clear")) {
                    Terminal.clearConsole();
                } else if (mainCommand.equals("cat")) {
                    File arr[] = file.listFiles();
                    if (command.indexOf(' ') == command.lastIndexOf(' ')) {
                        Terminal.cat(arr, argument);
                    } else {
                        String file1Name = argument.substring(0, argument.indexOf(' '));
                        String file2Name = argument.substring(argument.indexOf(' ') + 1, argument.length());
                        Terminal.cat(arr, concatenate(arr, file1Name, file2Name));
                    }
                } else if (command.equals("date")) {
                    Date date = new Date();
                    System.out.println(date.toString());
                } else if (command.equals("help")) {
                    Terminal.displayHelp();
                } else if (command.equals("args")) {
                    Terminal.displayArgs();
                } else if (mainCommand.equals("mkdir")) {
                    if (idx == -1) {
                        System.out.println("missing directory name!");
                    } else {
                        if (!Terminal.makeDir(argument, file.getAbsolutePath())) {
                            System.out.println("already exist! or Woring path!");
                        }
                    }
                } else if (mainCommand.equals("rm")) {
                    if (!Terminal.rm(argument, file.getAbsolutePath())) {
                        System.out.println("Wrong Pramaters!");
                    }
                } else if (mainCommand.equals("rmdir")) {
                    if (!Terminal.rm("-d " + argument, file.getAbsolutePath())) {
                        System.out.println("Wrong pramater!");
                    }
                } else if (mainCommand.equals("more")) {
                    Terminal.more(argument, file.getAbsolutePath());
                } 
            }
        }
    }

}
