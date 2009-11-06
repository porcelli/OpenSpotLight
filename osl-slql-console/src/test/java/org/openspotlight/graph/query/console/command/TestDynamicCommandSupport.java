package org.openspotlight.graph.query.console.command;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

public class TestDynamicCommandSupport {

    @Test
    public void testDynamicCommandLoader() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<Command> commands = null;

        assertThat(commands, is(nullValue()));

        commands = DynamicCommandSupport.getRegisteredDynamicCommands();
        assertThat(commands, is(notNullValue()));

        assertThat(commands.size(), is(13));
    }
}
