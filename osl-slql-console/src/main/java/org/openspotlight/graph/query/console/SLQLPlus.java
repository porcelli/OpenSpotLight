/*
 * OpenSpotLight - Open Source IT Governance Platform
 *  
 * Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA 
 * or third-party contributors as indicated by the @author tags or express 
 * copyright attribution statements applied by the authors.  All third-party 
 * contributions are distributed under license by CARAVELATECH CONSULTORIA E 
 * TECNOLOGIA EM INFORMATICA LTDA. 
 * 
 * This copyrighted material is made available to anyone wishing to use, modify, 
 * copy, or redistribute it subject to the terms and conditions of the GNU 
 * Lesser General Public License, as published by the Free Software Foundation. 
 * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * See the GNU Lesser General Public License  for more details. 
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this distribution; if not, write to: 
 * Free Software Foundation, Inc. 
 * 51 Franklin Street, Fifth Floor 
 * Boston, MA  02110-1301  USA 
 * 
 *********************************************************************** 
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto 
 *
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA 
 * EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta 
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor.
 * Todas as contribuições de terceiros estão distribuídas sob licença da
 * CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA. 
 * 
 * Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo sob os 
 * termos da Licença Pública Geral Menor do GNU conforme publicada pela Free Software 
 * Foundation. 
 * 
 * Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA 
 * GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA
 * FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU para mais detalhes.  
 * 
 * Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este
 * programa; se não, escreva para: 
 * Free Software Foundation, Inc. 
 * 51 Franklin Street, Fifth Floor 
 * Boston, MA  02110-1301  USA
 */
package org.openspotlight.graph.query.console;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.UnknownHostException;
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

/**
 * The Class SLQLPlus. This is the main application.
 * 
 * @author porcelli
 */
public class SLQLPlus {

    /** The Constant VERSION. */
    public final static String VERSION    = "0.5";                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  //$NON-NLS-1$

    /** The logos. */
    private static String[]    LOGO_TYPES = {
                                          " (   (         (      (              \n )\\ ))\\ )  (   )\\ )   )\\ ) (         \n(()/(()/(( )\\ (()/(  (()/( )\\  (     \n /(_))(_))((_) /(_))  /(_))(_)))\\ (  \n(_))(_))((_)_ (_))   (_))  _ /((_))\\ \n/ __| |  / _ \\| |    | _ \\| (_))(((_)\n\\__ \\ |__ (_) | |__  |  _/| | || (_-<\n|___/____\\__\\_\\____| |_|  |_|\\_,_/__/",
                                          " ______     __         ______     __            ______   __         __  __     ______   \n/\\  ___\\   /\\ \\       /\\  __ \\   /\\ \\          /\\  == \\ /\\ \\       /\\ \\/\\ \\   /\\  ___\\  \n\\ \\___  \\  \\ \\ \\____  \\ \\ \\/\\_\\  \\ \\ \\____     \\ \\  _-/ \\ \\ \\____  \\ \\ \\_\\ \\  \\ \\___  \\ \n \\/\\_____\\  \\ \\_____\\  \\ \\___\\_\\  \\ \\_____\\     \\ \\_\\    \\ \\_____\\  \\ \\_____\\  \\/\\_____\\\n  \\/_____/   \\/_____/   \\/___/_/   \\/_____/      \\/_/     \\/_____/   \\/_____/   \\/_____/",
                                          " ____  _     ___  _       ____  _           \n/ ___|| |   / _ \\| |     |  _ \\| |_   _ ___ \n\\___ \\| |  | | | | |     | |_) | | | | / __|\n ___) | |___ |_| | |___  |  __/| | |_| \\__ \\\n|____/|_____\\__\\_\\_____| |_|   |_|\\__,_|___/",
                                          "  ______   _____       ___      _____      _______  __                 \n.' ____ \\ |_   _|    .'   `.   |_   _|    |_   __ \\[  |                \n| (___ \\_|  | |     /  .-.  \\    | |        | |__) || | __   _   .--.  \n _.____`.   | |   _ | |   | |    | |   _    |  ___/ | |[  | | | ( (`\\] \n| \\____) | _| |__/ |\\  `-'  \\_  _| |__/ |  _| |_    | | | \\_/ |, `'.'. \n \\______.'|________| `.___.\\__||________| |_____|  [___]'.__.'_/[\\__) )",
                                          "   ___    _     ___     _                ___    _                        \n  / __|  | |   / _ \\   | |       o O O  | _ \\  | |   _  _    ___         \n  \\__ \\  | |__| (_) |  | |__    o       |  _/  | |  | +| |  (_-<         \n  |___/  |____|\\__\\_\\  |____|  TS__[O] _|_|_  _|_|_  \\_,_|  /__/_        \n_|\"\"\"\"\"||\"\"\"\"\"||\"\"\"\"\"||\"\"\"\"\"| {======|| \"\"\" ||\"\"\"\"\"||\"\"\"\"\"||\"\"\"\"\"|       \n\"`-0-0-'\"`-0-0-'\"`-0-0-'\"`-0-0-'./o--000'\"`-0-0-'\"`-0-0-'\"`-0-0-'\"`-0-0-'"};

    /**
     * Gets the prompt.
     * 
     * @return the prompt
     */
    private static String getPrompt() {
        return "slql> "; //$NON-NLS-1$
    }

    /**
     * Login.
     * 
     * @param reader the reader
     * @param out the out
     * @return the pair< boolean, sl graph session>
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws SLException the SL exception
     * @throws ClassNotFoundException the class not found exception
     */
    private static Pair<Boolean, SLGraphSession> login( final ConsoleReader reader,
                                                        final PrintWriter out )
        throws IOException, SLException, ClassNotFoundException {
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
            final String password = reader.readLine(Messages.getString("SLQLPlus.9"), '*'); //$NON-NLS-1$
            try {
                result = validateCredentials(serverName, userName, password, out);

                loginAtemptsCount++;
                if (!result.getK1() && loginAtemptsCount == 3) {
                    out.println(Messages.getString("SLQLPlus.14")); //$NON-NLS-1$
                    out.println(Messages.getString("SLQLPlus.11")); //$NON-NLS-1$
                    out.flush();
                    break;
                }
                if (result.getK1()) {
                    break;
                }
            } catch (UnknownHostException e) {
                out.println(Messages.getString("SLQLPlus.10")); //$NON-NLS-1$
                out.println(Messages.getString("SLQLPlus.11")); //$NON-NLS-1$
                out.flush();
                break;
            }
        }
        return result;
    }

    /**
     * The main method.
     * 
     * @param args the arguments
     * @throws Exception the exception
     */
    public static void main( final String[] args ) throws Exception {
        final ConsoleReader reader = new ConsoleReader();
        reader.setBellEnabled(false);

        final PrintWriter out = new PrintWriter(System.out);

        reader.clearScreen();
        printLogo(out);

        final Pair<Boolean, SLGraphSession> loginState = login(reader, out);

        if (loginState.getK1() && loginState.getK2() != null) {
            out.println();
            out.println();
            out.println(Messages.getString("SLQLPlus.1")); //$NON-NLS-1$
            out.flush();

            final List<Command> commands = DynamicCommandSupport.getRegisteredDynamicCommands();

            final String[] consoleCommands = new String[commands.size()];

            int i = 0;
            for (final Command command : commands) {
                consoleCommands[i] = command.getAutoCompleteCommand();
                i++;
            }

            reader.addCompletor(new SimpleCompletor(consoleCommands));
            reader.addCompletor(new SLQLFileNameCompletor(commands));

            final ConsoleState state = new ConsoleState(loginState.getK2());
            String input;
            while ((input = reader.readLine(getPrompt())) != null) {
                state.setInput(input.trim());
                boolean inputAccepted = false;
                for (final Command activeCommand : commands) {
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

    /**
     * Prints the logo.
     * 
     * @param out the out
     */
    public static void printLogo( final PrintWriter out ) {
        final Random x = new Random();
        out.print(LOGO_TYPES[x.nextInt(LOGO_TYPES.length)]);
        out.println();
        out.println();
        out.flush();
    }

    /**
     * Validate credentials.
     * 
     * @param serverName the server name
     * @param userName the user name
     * @param password the password
     * @param out the out
     * @return the pair< boolean, sl graph session>
     * @throws SLException the SL exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ClassNotFoundException the class not found exception
     */
    private static Pair<Boolean, SLGraphSession> validateCredentials( final String serverName,
                                                                      final String userName,
                                                                      final String password,
                                                                      final PrintWriter out )
        throws SLException, IOException, ClassNotFoundException {
        if (userName.equalsIgnoreCase("sa")) { //$NON-NLS-1$
            final GraphConnection connection = new GraphConnection();
            return new Pair<Boolean, SLGraphSession>(true, connection.connect(serverName, userName, password));
        }

        out.println(Messages.getString("SLQLPlus.13")); //$NON-NLS-1$
        out.flush();
        return new Pair<Boolean, SLGraphSession>(false, null);
    }

}
