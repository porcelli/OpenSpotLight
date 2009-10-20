package org.openspotlight.graph.query.console.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.clapper.util.classutil.AbstractClassFilter;
import org.clapper.util.classutil.AndClassFilter;
import org.clapper.util.classutil.ClassFilter;
import org.clapper.util.classutil.ClassFinder;
import org.clapper.util.classutil.ClassInfo;
import org.clapper.util.classutil.InterfaceOnlyClassFilter;
import org.clapper.util.classutil.NotClassFilter;
import org.clapper.util.classutil.SubclassClassFilter;
import org.openspotlight.graph.query.console.command.system.ClearSystemCommand;
import org.openspotlight.graph.query.console.command.system.ExitSystemCommand;
import org.openspotlight.graph.query.console.command.system.HelpSystemCommand;
import org.openspotlight.graph.query.console.command.system.VersionSystemCommand;

public class DynamicCommandSupport {

    public static List<Command> getRegisteredDynamicCommands()
        throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<Command> result = new LinkedList<Command>();
        ClassFinder finder = new ClassFinder();
        finder.addClassPath();

        ClassFilter filter =
            new AndClassFilter(
                                       //Must not be an interface
        new NotClassFilter(new InterfaceOnlyClassFilter()),
                                       //Must implement the ClassFilter interface
        new SubclassClassFilter(DynamicCommand.class),
                                       // Must not be abstract
        new NotClassFilter(new AbstractClassFilter()));

        Collection<ClassInfo> foundClasses = new ArrayList<ClassInfo>();
        finder.findClasses(foundClasses, filter);

        for (ClassInfo classInfo : foundClasses) {
            Class<?> clasz = Class.forName(classInfo.getClassName());

            DynamicCommand generatedCommand = (DynamicCommand)clasz.newInstance();
            result.add(generatedCommand);
        }
        Command clear = new ClearSystemCommand();
        result.add(clear);

        ExitSystemCommand exit = new ExitSystemCommand();
        result.add(exit);

        VersionSystemCommand version = new VersionSystemCommand();
        result.add(version);

        Command help = new HelpSystemCommand(result);
        result.add(help);

        return result;
    }
}
