package org.openspotlight.domain;

import java.util.concurrent.Callable;

public final class RepositoryBuilder {

    public class ArtifactMappingBuilder {

        private final ArtifactSourceMapping sourceMapping;
        private final Object                parent;

        public ArtifactMappingBuilder(final String lookup, final ArtifactSourceBuilder artifactSourceBuilder) {
            sourceMapping = new ArtifactSourceMapping();
            sourceMapping.setFrom(lookup);
            sourceMapping.setSource(artifactSourceBuilder.artifactSource);
            artifactSourceBuilder.artifactSource.addArtifactMapping(sourceMapping);
            parent = artifactSourceBuilder;
        }

        public ArtifactMappingBuilder(final String lookup, final InnerArtifactSourceGroupBuilder innerArtifactSourceGroupBuilder) {
            sourceMapping = new ArtifactSourceMapping();
            sourceMapping.setFrom(lookup);
            sourceMapping.setSource(innerArtifactSourceGroupBuilder.artifactSource);
            sourceMapping.setGroup(innerArtifactSourceGroupBuilder.group);
            innerArtifactSourceGroupBuilder.group.addArtifactMapping(sourceMapping);
            innerArtifactSourceGroupBuilder.artifactSource.addArtifactMapping(sourceMapping);
            parent = innerArtifactSourceGroupBuilder;
        }

        public GroupBuilder back2Group() {
            if (parent instanceof ArtifactSourceBuilder) { return ((ArtifactSourceBuilder) parent).parent; }
            throw new IllegalStateException("Active parent is " + parent.getClass().getName());
        }

        public InnerArtifactSourceGroupBuilder back2SubGroup() {
            if (parent instanceof InnerArtifactSourceGroupBuilder) { return (InnerArtifactSourceGroupBuilder) parent; }
            throw new IllegalStateException("Active parent is " + parent.getClass().getName());
        }

        public ArtifactMappingBuilder andWithAnotherArtifactMapping(final String lookup) {
            if (parent instanceof InnerArtifactSourceGroupBuilder) {
                return new ArtifactMappingBuilder(lookup, (InnerArtifactSourceGroupBuilder) parent);
            } else {
                return new ArtifactMappingBuilder(lookup, (ArtifactSourceBuilder) parent);
            }
        }

        public ArtifactSourceBuilder andWithAnotherArtifactSource(final String url, final String initialLookup) {
            if (parent instanceof InnerArtifactSourceGroupBuilder) { throw new IllegalStateException(); }
            return new ArtifactSourceBuilder(url, initialLookup, ((ArtifactSourceBuilder) parent).parent);
        }

        public GroupBuilder andWithAnotherGroup(final String groupName) {
            if (parent instanceof InnerArtifactSourceGroupBuilder) { throw new IllegalStateException(); }
            return new GroupBuilder(groupName, (ArtifactSourceBuilder) parent);
        }

        public GroupBuilder andWithAnotherGroup(final String groupName, final boolean active) {
            if (parent instanceof InnerArtifactSourceGroupBuilder) { throw new IllegalStateException(); }
            return new GroupBuilder(groupName, active, (ArtifactSourceBuilder) parent);
        }

        public ArtifactMappingBuilder to(final String virtualPath) {
            sourceMapping.setTo(virtualPath);
            return this;
        }

        public ArtifactMappingBuilder withEncoding(final String enconding) {
            sourceMapping.setEncoding(enconding);
            return this;
        }

        public ArtifactMappingBuilder withExcludes(final String... excludes) {
            for (final String exclude: excludes) {
                sourceMapping.addExcludes(exclude);
            }
            return this;
        }

        public ArtifactMappingBuilder withIncludes(final String... includes) {
            for (final String include: includes) {
                sourceMapping.addIncludes(include);
            }
            return this;
        }

        public ArtifactMappingBuilder withTask(final Class<? extends Callable<Void>> task) {
            sourceMapping.addTask(task);
            return this;
        }

        public ArtifactMappingBuilder withTasks(final Class<? extends Callable<Void>>... tasks) {
            for (final Class<? extends Callable<Void>> task: tasks) {
                sourceMapping.addTask(task);
            }
            return this;
        }

        public Repository andCreate() {
            return repository;
        }

    }

    public class ArtifactSourceBuilder {

        private final ArtifactSource artifactSource;
        private final GroupBuilder   parent;

        public ArtifactSourceBuilder(final String url, final String initialLookup, final GroupBuilder groupBuilder) {
            artifactSource = new ArtifactSource();
            artifactSource.setUrl(url);
            artifactSource.setInitialLookup(initialLookup);
            groupBuilder.group.addArtifactSource(artifactSource);
            artifactSource.setGroup(groupBuilder.group);
            parent = groupBuilder;
        }

        public GroupBuilder back2Group() {
            return parent;
        }

        public ArtifactSourceBuilder andWithAnotherArtifactSource(final String url, final String initialLookup) {
            return new ArtifactSourceBuilder(url, initialLookup, parent);
        }

        public GroupBuilder andWithAnotherGroup(final String groupName) {
            return new GroupBuilder(groupName, this);
        }

        public GroupBuilder andWithAnotherGroup(final String groupName, final boolean active) {
            return new GroupBuilder(groupName, active, this);
        }

        public ArtifactMappingBuilder withArtifactMapping(final String lookup) {
            return new ArtifactMappingBuilder(lookup, this);
        }

        public ArtifactSourceBuilder withEncoding(final String enconding) {
            artifactSource.setEncodingForFileContent(enconding);
            return this;
        }

        public ArtifactSourceBuilder withProperty(final String name, final String value) {
            artifactSource.addProperty(name, value);
            return this;
        }

        public InnerArtifactSourceGroupBuilder withSubGroup(final String groupName) {
            return new InnerArtifactSourceGroupBuilder(groupName, this);
        }

        public InnerArtifactSourceGroupBuilder withSubGroup(final String groupName, final boolean active) {
            return new InnerArtifactSourceGroupBuilder(groupName, active, this);
        }

        public ArtifactSourceBuilder withTask(final Class<? extends Callable<Void>> task) {
            artifactSource.addTask(task);
            return this;
        }

        public ArtifactSourceBuilder withTasks(final Class<? extends Callable<Void>>... tasks) {
            for (final Class<? extends Callable<Void>> task: tasks) {
                artifactSource.addTask(task);
            }
            return this;
        }

        public Repository andCreate() {
            return repository;
        }
    }

    public class GroupBuilder {

        private final static boolean OMMITED_GROUP_ACTIVE_VALUE = true;
        private final Group          group;
        private final Object         parent;

        public GroupBuilder(final String groupName, final RepositoryBuilder repositoryBuilder) {
            this(groupName, OMMITED_GROUP_ACTIVE_VALUE, repositoryBuilder);
        }

        public GroupBuilder(final String groupName, final GroupBuilder groupBuilder) {
            this(groupName, OMMITED_GROUP_ACTIVE_VALUE, groupBuilder);
        }

        public GroupBuilder(final String groupName, final ArtifactSourceBuilder artifactSourceBuilder) {
            this(groupName, OMMITED_GROUP_ACTIVE_VALUE, artifactSourceBuilder);
        }

        public GroupBuilder(final String groupName, final boolean active, final RepositoryBuilder repositoryBuilder) {
            group = new Group();
            group.setName(groupName);
            group.setActive(active);
            repository.addGroup(group);
            parent = repositoryBuilder;
        }

        public GroupBuilder(final String groupName, final boolean active, final GroupBuilder groupBuilder) {
            group = new Group();
            group.setName(groupName);
            group.setActive(active);
            groupBuilder.group.addGroup(group);
            group.setParent(groupBuilder.group);
            parent = groupBuilder;
        }

        public GroupBuilder(final String groupName, final boolean active, final ArtifactSourceBuilder artifactSourceBuilder) {
            group = new Group();
            group.setName(groupName);
            group.setActive(active);
            artifactSourceBuilder.artifactSource.getGroup().getParent().addGroup(group);
            group.setParent(artifactSourceBuilder.artifactSource.getGroup().getParent());
            parent = artifactSourceBuilder;
        }

        public RepositoryBuilder back2Root() {
            if (parent instanceof RepositoryBuilder) { return (RepositoryBuilder) parent; }
            throw new IllegalStateException();
        }

        public GroupBuilder back2Group() {
            if (parent instanceof GroupBuilder) { return (GroupBuilder) parent; }
            throw new IllegalStateException();
        }

        public ArtifactSourceBuilder back2Source() {
            if (parent instanceof ArtifactSourceBuilder) { return (ArtifactSourceBuilder) parent; }
            throw new IllegalStateException();
        }

        public GroupBuilder andWithAnotherGroup(final String groupName) {
            return new GroupBuilder(groupName, this);
        }

        public GroupBuilder andWithAnotherGroup(final String groupName, final boolean active) {
            return new GroupBuilder(groupName, active, this);
        }

        public ArtifactSourceBuilder withArtifactSource(final String sourceUrl, final String initialLookup) {
            return new ArtifactSourceBuilder(sourceUrl, initialLookup, this);
        }

        public GroupBuilder withProperty(final String name, final String value) {
            group.addProperty(name, value);
            return this;
        }

        public GroupBuilder withSubGroup(final String groupName) {
            return new GroupBuilder(groupName, this);
        }

        public GroupBuilder withSubGroup(final String groupName, final boolean active) {
            return new GroupBuilder(groupName, active, this);
        }

        public GroupBuilder withTask(final Class<? extends Callable<Void>> task) {
            group.addTask(task);
            return this;
        }

        public GroupBuilder withTasks(final Class<? extends Callable<Void>>... tasks) {
            for (final Class<? extends Callable<Void>> task: tasks) {
                group.addTask(task);
            }
            return this;
        }

        public Repository andCreate() {
            return repository;
        }
    }

    public class InnerArtifactSourceGroupBuilder {

        private final Group          group;
        private final ArtifactSource artifactSource;
        private final Object         parent;

        public InnerArtifactSourceGroupBuilder(final String groupName, final ArtifactSourceBuilder artifactSourceBuilder) {
            this(groupName, GroupBuilder.OMMITED_GROUP_ACTIVE_VALUE, artifactSourceBuilder);
        }

        public InnerArtifactSourceGroupBuilder(final String groupName,
                                                       final InnerArtifactSourceGroupBuilder innerArtifactSourceGroupBuilder) {
            this(groupName, GroupBuilder.OMMITED_GROUP_ACTIVE_VALUE, innerArtifactSourceGroupBuilder);
        }

        public InnerArtifactSourceGroupBuilder(final String groupName, final boolean active,
                                                       final InnerArtifactSourceGroupBuilder innerArtifactSourceGroupBuilder) {
            group = new Group();
            group.setName(groupName);
            group.setActive(active);
            group.setParent(innerArtifactSourceGroupBuilder.group);
            innerArtifactSourceGroupBuilder.group.addGroup(group);
            parent = innerArtifactSourceGroupBuilder;
            artifactSource = innerArtifactSourceGroupBuilder.artifactSource;
        }

        public InnerArtifactSourceGroupBuilder(final String groupName, final boolean active,
                                                       final ArtifactSourceBuilder artifactSourceBuilder) {
            group = new Group();
            group.setName(groupName);
            group.setActive(active);
            artifactSourceBuilder.artifactSource.getGroup().addGroup(group);
            group.setParent(artifactSourceBuilder.artifactSource.getGroup());
            parent = artifactSourceBuilder;
            artifactSource = artifactSourceBuilder.artifactSource;
        }

        public ArtifactSourceBuilder back2Source() {
            if (parent instanceof ArtifactSourceBuilder) { return (ArtifactSourceBuilder) parent; }
            throw new IllegalStateException();
        }

        public InnerArtifactSourceGroupBuilder back2SubGroup() {
            if (parent instanceof InnerArtifactSourceGroupBuilder) { return (InnerArtifactSourceGroupBuilder) parent; }
            throw new IllegalStateException();
        }

        public ArtifactMappingBuilder withArtifactMapping(final String lookup) {
            return new ArtifactMappingBuilder(lookup, this);
        }

        public InnerArtifactSourceGroupBuilder withProperty(final String name, final String value) {
            group.addProperty(name, value);
            return this;
        }

        public InnerArtifactSourceGroupBuilder withTask(final Class<? extends Callable<Void>> task) {
            group.addTask(task);
            return this;
        }

        public InnerArtifactSourceGroupBuilder withTasks(final Class<? extends Callable<Void>>... tasks) {
            for (final Class<? extends Callable<Void>> task: tasks) {
                group.addTask(task);
            }
            return this;
        }

        public InnerArtifactSourceGroupBuilder withSubGroup(final String groupName, final boolean active) {
            return new InnerArtifactSourceGroupBuilder(groupName, active, this);
        }

        public InnerArtifactSourceGroupBuilder withSubGroup(final String groupName) {
            return new InnerArtifactSourceGroupBuilder(groupName, this);
        }

        public Repository andCreate() {
            return repository;
        }
    }

    private final Repository repository;

    private RepositoryBuilder(final String name) {
        this(name, true);
    }

    private RepositoryBuilder(final String name, final boolean active) {
        repository = new Repository();
        repository.setName(name);
        repository.setActive(active);
    }

    public static RepositoryBuilder newRepositoryNamed(final String name) {
        return new RepositoryBuilder(name);
    }

    public static RepositoryBuilder newRepositoryNamed(final String name, final boolean active) {
        return new RepositoryBuilder(name, active);
    }

    public Repository andCreate() {
        return repository;
    }

    public GroupBuilder withGroup(final String groupName) {
        return new GroupBuilder(groupName, this);
    }

    public GroupBuilder withGroup(final String groupName, final boolean active) {
        return new GroupBuilder(groupName, active, this);
    }
}
