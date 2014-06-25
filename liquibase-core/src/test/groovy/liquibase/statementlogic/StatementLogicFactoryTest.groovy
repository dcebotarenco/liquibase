package liquibase.statementlogic

import liquibase.ExecutionEnvironment
import liquibase.database.core.H2Database
import liquibase.sqlgenerator.SqlGenerator
import liquibase.sqlgenerator.core.AddAutoIncrementGenerator
import liquibase.sqlgenerator.core.AddAutoIncrementGeneratorDB2
import liquibase.sqlgenerator.core.AddAutoIncrementGeneratorHsqlH2
import liquibase.sqlgenerator.core.AddColumnGenerator
import liquibase.statement.core.AddAutoIncrementStatement
import spock.lang.Specification

public class StatementLogicFactoryTest extends Specification {

    def setup() {
        StatementLogicFactory.reset();
    }

    def getInstance() {
        when:
        StatementLogicFactory.getInstance() != null

        then:
        StatementLogicFactory.getInstance() == StatementLogicFactory.getInstance()
    }

    def register() {
        when:
        StatementLogicFactory.getInstance().getGenerators().clear();
        then:
        StatementLogicFactory.getInstance().getGenerators().size() == 0

        when:
        StatementLogicFactory.getInstance().register(new MockStatementLogic(1, "A1"));
        then:
        StatementLogicFactory.getInstance().getGenerators().size() == 1
    }

    def unregister_instance() {
        when:
        StatementLogicFactory factory = StatementLogicFactory.getInstance();
        factory.getGenerators().clear();

        then:
        factory.getGenerators().size() == 0

        when:
        AddAutoIncrementGeneratorHsqlH2 sqlGenerator = new AddAutoIncrementGeneratorHsqlH2();

        factory.register(new AddAutoIncrementGenerator());
        factory.register(sqlGenerator);
        factory.register(new AddAutoIncrementGeneratorDB2());

        then:
        factory.getGenerators().size() == 3

        when:
        factory.unregister(sqlGenerator);
        then:
        factory.getGenerators().size() == 2
    }

    def unregister_class() {
        when:
        StatementLogicFactory factory = StatementLogicFactory.getInstance();

        factory.getGenerators().clear();

        then:
        factory.getGenerators().size() == 0

        when:
        AddAutoIncrementGeneratorHsqlH2 sqlGenerator = new AddAutoIncrementGeneratorHsqlH2();

        factory.register(new AddAutoIncrementGenerator());
        factory.register(sqlGenerator);
        factory.register(new AddAutoIncrementGeneratorDB2());
        then:
        factory.getGenerators().size() == 3

        when:
        factory.unregister(AddAutoIncrementGeneratorHsqlH2.class);
        then:
        factory.getGenerators().size() == 2
    }

     def unregister_class_doesNotExist() {
         when:
        StatementLogicFactory factory = StatementLogicFactory.getInstance();

        factory.getGenerators().clear();

         then:
        factory.getGenerators().size() == 0

         when:
        factory.register(new AddAutoIncrementGenerator());
        factory.register(new AddAutoIncrementGeneratorHsqlH2());
        factory.register(new AddAutoIncrementGeneratorDB2());

         then:
        factory.getGenerators().size() == 3

         when:
        factory.unregister(AddColumnGenerator.class);
         then:
        factory.getGenerators().size() == 3
    }

   def void reset() {
       when:
        StatementLogicFactory instance1 = StatementLogicFactory.getInstance();
        StatementLogicFactory.reset();
       then:
        instance1 != StatementLogicFactory.getInstance()
    }

    def builtInGeneratorsAreFound() {
        expect:
        StatementLogicFactory.getInstance().getGenerators().size() > 10
    }

    def getGenerators() {
        when:
        SortedSet<SqlGenerator> allGenerators = StatementLogicFactory.getInstance().getGenerators(new AddAutoIncrementStatement(null, null, "person", "name", "varchar(255)", null, null), new ExecutionEnvironment(new H2Database()));

        then:
        allGenerators != null
        allGenerators.size() == 1        
    }
}
