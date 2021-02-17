package com.chutneytesting.design.infra.storage.scenario.compose;

import static java.util.stream.Collectors.toList;

import com.chutneytesting.design.domain.scenario.compose.ComposableStep;
import com.chutneytesting.design.domain.scenario.compose.Strategy;
import com.chutneytesting.design.infra.storage.scenario.compose.wrapper.StepVertex;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.record.OElement;
import com.orientechnologies.orient.core.record.OVertex;
import java.util.List;
import java.util.Optional;

public class OrientComposableStepMapper {

    // SAVE
    static StepVertex composableStepToVertex(final ComposableStep composableStep, OVertex oVertex, ODatabaseSession dbSession) {
        return StepVertex.builder()
            .from(oVertex)
            .usingSession(dbSession)
            .withId(composableStep.id)
            .withName(composableStep.name)
            .withTags(composableStep.tags)
            .withImplementation(composableStep.implementation)
            .withStrategy(composableStep.strategy)
            .withDefaultParameters(composableStep.defaultParameters)
            .withExecutionParameters(composableStep.executionParameters)
            .withSteps(composableStep.steps)
            .withRelations(composableStep.steps.stream().map(s -> composableStepToVertex(s, null, dbSession)).collect(toList()))
            .build();
    }

    // GET
    public static ComposableStep vertexToComposableStep(final StepVertex vertex) {
        vertex.reloadIfDirty();

        ComposableStep.ComposableStepBuilder builder = ComposableStep.builder()
            .withId(vertex.id())
            .withName(vertex.name())
            .withTags(vertex.tags())
            .withImplementation(vertex.implementation())
            .withDefaultParameters(vertex.defaultParameters())
            .withExecutionParameters(vertex.executionParameters());

        OElement strategy = vertex.strategy();
        Optional.ofNullable(strategy).ifPresent( s ->
            builder.withStrategy(new Strategy(strategy.getProperty("name"), strategy.getProperty("parameters")))
        );

        builder.withSteps(
            vertexToComposableStep(vertex.listChildrenSteps())
        );

        return builder.build();
    }

    public static List<ComposableStep> vertexToComposableStep(List<StepVertex> subSteps) {
        return subSteps.stream()
            .map(OrientComposableStepMapper::vertexToComposableStep)
            .collect(toList());
    }

}
