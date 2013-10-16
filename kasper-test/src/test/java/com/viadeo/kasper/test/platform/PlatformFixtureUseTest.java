// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.context.impl.DefaultKasperId;
import com.viadeo.kasper.platform.configuration.*;
import com.viadeo.kasper.test.platform.use_case_1.TestChangeNameCommandHandler;
import com.viadeo.kasper.test.platform.use_case_1.TestEntity;
import org.junit.Test;

import java.util.UUID;

import static com.viadeo.kasper.test.platform.use_case_1.TestChangeNameCommandHandler.TestChangeNameCommand;
import static com.viadeo.kasper.test.platform.use_case_1.TestCreateCommandHandler.TestCreateCommand;
import static com.viadeo.kasper.test.platform.use_case_1.TestEntity.*;
import static com.viadeo.kasper.test.platform.use_case_1.TestGetAllEntitiesQueryService.TestGetAllEntitiesQuery;

public class PlatformFixtureUseTest {

    @Test
    public void testPlatformFixture() {

        final PlatformFixture fixture = PlatformFixture.from(new PlatformFactory().getPlatform(true));

        final KasperID id_1 = new DefaultKasperId(UUID.randomUUID());
        final KasperID id_2 = new DefaultKasperId(UUID.randomUUID());
        final KasperID id_3 = new DefaultKasperId(UUID.randomUUID());

        fixture
            .scan("com.viadeo.kasper.test.platform.use_case_1")
            .given()
                      .mocked(TestEntityRepository.class)
                .and().has(TestEntity.class).withId(id_1).withField("name", "foo")
            .when()
                      .sendCommand(new TestChangeNameCommand(id_1, "bar"))
                .and().sendCommand(new TestCreateCommand(id_2, "kasper"))
            .then()
                      .assertUpdated(TestEntity.class).withId(id_1).withField("name", "bar")
                .and().assertAdded(TestEntity.class).withId(id_2).withField("name", "kasper")
                .and().assertEvent(new TestCreatedEvent(fixture.context(), id_2, "kasper"))
                .and().assertEvent(new TestNameChangedEvent(fixture.context(), id_1, "foo", "bar"))
            .ensure()
            .withNoOtherUpdates()
            .withNoOtherAdds()
            .withNoOtherEvents()
            .withNoOtherListenedEvents();

        final PlatformFixture fixture2 = PlatformFixture.defaults();

        fixture2
            .given()
                .with(fixture)
                .sent(new TestCreatedEvent(fixture.context(), id_3, "viadeo"))
            .when()
                .sendQuery(new TestGetAllEntitiesQuery()).as("ALL")
            .then()
                     .assertResult("ALL")
                             .contains(TestEntity.class).withId(id_1)
                        .and.contains(TestEntity.class).withField("name", "kasper")
                        .and.contains(TestEntity.class).withField("name", "viadeo")
            .ensure()
            .withNoOtherResult()
            .withNoOtherEntryForResult("ALL");

    }

}
