package org.openspotlight.graph.query.console;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Random;

import jline.ConsoleReader;
import jline.SimpleCompletor;

import org.openspotlight.common.Pair;
import org.openspotlight.common.exception.SLException;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.query.console.command.Command;
import org.openspotlight.graph.query.console.command.DynamicCommandSupport;
import org.openspotlight.graph.query.console.completor.SLQLFileNameCompletor;
import org.openspotlight.graph.query.console.util.Messages;

public class SLQLPlus {

    public final static String VERSION    = "0.5"; //$NON-NLS-1$
    private static String[]    LOGO_TYPES =
                                              {" (   (         (      (              \n )\\ ))\\ )  (   )\\ )   )\\ ) (         \n(()/(()/(( )\\ (()/(  (()/( )\\  (     \n /(_))(_))((_) /(_))  /(_))(_)))\\ (  \n(_))(_))((_)_ (_))   (_))  _ /((_))\\ \n/ __| |  / _ \\| |    | _ \\| (_))(((_)\n\\__ \\ |__ (_) | |__  |  _/| | || (_-<\n|___/____\\__\\_\\____| |_|  |_|\\_,_/__/",
                                          " ______     __         ______     __            ______   __         __  __     ______   \n/\\  ___\\   /\\ \\       /\\  __ \\   /\\ \\          /\\  == \\ /\\ \\       /\\ \\/\\ \\   /\\  ___\\  \n\\ \\___  \\  \\ \\ \\____  \\ \\ \\/\\_\\  \\ \\ \\____     \\ \\  _-/ \\ \\ \\____  \\ \\ \\_\\ \\  \\ \\___  \\ \n \\/\\_____\\  \\ \\_____\\  \\ \\___\\_\\  \\ \\_____\\     \\ \\_\\    \\ \\_____\\  \\ \\_____\\  \\/\\_____\\\n  \\/_____/   \\/_____/   \\/___/_/   \\/_____/      \\/_/     \\/_____/   \\/_____/   \\/_____/",
                                          " ____  _     ___  _       ____  _           \n/ ___|| |   / _ \\| |     |  _ \\| |_   _ ___ \n\\___ \\| |  | | | | |     | |_) | | | | / __|\n ___) | |___ |_| | |___  |  __/| | |_| \\__ \\\n|____/|_____\\__\\_\\_____| |_|   |_|\\__,_|___/",
                                          "  ______   _____       ___      _____      _______  __                 \n.' ____ \\ |_   _|    .'   `.   |_   _|    |_   __ \\[  |                \n| (___ \\_|  | |     /  .-.  \\    | |        | |__) || | __   _   .--.  \n _.____`.   | |   _ | |   | |    | |   _    |  ___/ | |[  | | | ( (`\\] \n| \\____) | _| |__/ |\\  `-'  \\_  _| |__/ |  _| |_    | | | \\_/ |, `'.'. \n \\______.'|________| `.___.\\__||________| |_____|  [___]'.__.'_/[\\__) )",
                                          "   ___    _     ___     _                ___    _                        \n  / __|  | |   / _ \\   | |       o O O  | _ \\  | |   _  _    ___         \n  \\__ \\  | |__| (_) |  | |__    o       |  _/  | |  | +| |  (_-<         \n  |___/  |____|\\__\\_\\  |____|  TS__[O] _|_|_  _|_|_  \\_,_|  /__/_        \n_|\"\"\"\"\"||\"\"\"\"\"||\"\"\"\"\"||\"\"\"\"\"| {======|| \"\"\" ||\"\"\"\"\"||\"\"\"\"\"||\"\"\"\"\"|       \n\"`-0-0-'\"`-0-0-'\"`-0-0-'\"`-0-0-'./o--000'\"`-0-0-'\"`-0-0-'\"`-0-0-'\"`-0-0-'"
                                              };

    public static void main( String[] args ) throws Exception {
        ConsoleReader reader = new ConsoleReader();
        reader.setBellEnabled(false);

        PrintWriter out = new PrintWriter(System.out);

        reader.clearScreen();
        printLogo(out);

        Pair<Boolean, SLGraphSession> loginState = login(reader, out);

        if (loginState.getK1() && loginState.getK2() != null) {
            out.println();
            out.println();
            out.println(Messages.getString("SLQLPlus.1")); //$NON-NLS-1$
            out.flush();

            List<Command> commands = DynamicCommandSupport.getRegisteredDynamicCommands();

            String[] consoleCommands = new String[commands.size()];

            int i = 0;
            for (Command command : commands) {
                consoleCommands[i] = command.getAutoCompleteCommand();
                i++;
            }

            reader.addCompletor(new SimpleCompletor(consoleCommands));
            reader.addCompletor(new SLQLFileNameCompletor(commands));

            ConsoleState state = new ConsoleState(loginState.getK2());
            String input;
            while ((input = reader.readLine(getPrompt())) != null) {
                state.setInput(input.trim());
                boolean inputAccepted = false;
                for (Command activeCommand : commands) {
                    if (activeCommand.accept(state)) {
                        inputAccepted = true;
                        activeCommand.execute(reader, out, state);
                        break;
                    }
                }
                if (!inputAccepted && state.getInput().length() > 0) {
                    out.println(Messages.getString("SLQLPlus.2")); //$NON-NLS-1$
                    state.clearBuffer();
                    out.flush();
                } else if (state.quitApplication()) {
                    break;
                }
            }
            loginState.getK2().close();
        }
    }

    private static Pair<Boolean, SLGraphSession> login( ConsoleReader reader,
                                                        PrintWriter out ) throws IOException, SLException, ClassNotFoundException {
        out.println("Please enter server name, user and password.");
        out.println();
        out.flush();

        Pair<Boolean, SLGraphSession> result = new Pair<Boolean, SLGraphSession>(false, null);
        int loginAtemptsCount = 0;
        int failCount = 0;
        start: while (true) {
            String serverName;
            while (true) {
                serverName = reader.readLine(Messages.getString("SLQLPlus.3")); //$NON-NLS-1$
                if (serverName.trim().length() == 0) {
                    failCount++;
                } else {
                    failCount = 0;
                    break;
                }
                if (failCount == 3) {
                    out.println(Messages.getString("SLQLPlus.4")); //$NON-NLS-1$
                    out.flush();
                    break start;
                }
            }
            String userName;
            while (true) {
                userName = reader.readLine(Messages.getString("SLQLPlus.7")); //$NON-NLS-1$
                if (userName.trim().length() == 0) {
                    failCount++;
                } else {
                    failCount = 0;
                    break;
                }
                if (failCount == 3) {
                    out.println(Messages.getString("SLQLPlus.8")); //$NON-NLS-1$
                    out.flush();
                    break start;
                }
            }
            String password = reader.readLine(Messages.getString("SLQLPlus.9"), '*'); //$NON-NLS-1$
            result = validateCredentials(serverName, userName, password, out);
            loginAtemptsCount++;
            if (!result.getK1() && loginAtemptsCount == 3) {
                out.println(Messages.getString("SLQLPlus.10")); //$NON-NLS-1$
                out.println(Messages.getString("SLQLPlus.11")); //$NON-NLS-1$
                out.flush();
                break;
            }
            if (result.getK1()) {
                break;
            }
        }
        return result;
    }

    private static Pair<Boolean, SLGraphSession> validateCredentials( String serverName,
                                                                      String userName,
                                                                      String password,
                                                                      PrintWriter out )
        throws SLException, IOException, ClassNotFoundException {
        if (userName.equalsIgnoreCase("sa")) { //$NON-NLS-1$
            GraphConnection connection = new GraphConnection();
            return new Pair<Boolean, SLGraphSession>(true, connection.connect(serverName, userName, password));
        }

        out.println(Messages.getString("SLQLPlus.13")); //$NON-NLS-1$
        out.flush();
        return new Pair<Boolean, SLGraphSession>(false, null);
    }

    private static String getPrompt() {
        return "slql> "; //$NON-NLS-1$
    }

    public static void printLogo( PrintWriter out ) {
        Random x = new Random();
        out.print(LOGO_TYPES[x.nextInt(LOGO_TYPES.length)]);
        out.println();
        out.println();
        out.flush();
    }

}
