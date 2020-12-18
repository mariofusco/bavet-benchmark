package org.benchmark.join;

import java.util.function.Function;

import org.benchmark.Session;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.api.score.stream.Joiners;
import org.optaplanner.core.impl.score.director.stream.ConstraintStreamScoreDirectorFactory;
import org.optaplanner.core.impl.score.stream.ConstraintSession;
import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;
import org.optaplanner.examples.cloudbalancing.domain.CloudComputer;
import org.optaplanner.examples.cloudbalancing.domain.CloudProcess;

final class ConstraintStreamSession implements Session {

    private final ConstraintSession<CloudBalance, HardSoftScore> session;

    public ConstraintStreamSession(ConstraintStreamImplType constraintStreamImplType, boolean indexed) {
        ConstraintProvider constraintProvider = getConstraintProvider(indexed);
        ConstraintStreamScoreDirectorFactory<CloudBalance, ?> scoreDirectorFactory = getCSFactory(constraintProvider, constraintStreamImplType);
        session = (ConstraintSession<CloudBalance, HardSoftScore>) scoreDirectorFactory.newConstraintStreamingSession(false, MyBenchmark.FULL_SOLUTION);
    }

    @Override
    public int insert(Object object) {
        session.insert(object);
        return 0;
    }

    @Override
    public int update(Object object) {
        session.update(object);
        return 1;
    }

    @Override
    public HardSoftScore calculateScore() {
        return session.calculateScore(0);
    }

    @Override
    public void close() {
        session.close();
    }

    private static ConstraintProvider getConstraintProvider(boolean indexed) {
        if (indexed) {
            return constraintFactory -> new Constraint[]{
                    constraintFactory.fromUnfiltered( CloudComputer.class )
                            .join( CloudProcess.class, Joiners.equal( Function.identity(), CloudProcess::getComputer ) )
                            .penalize( "requiredCpuPowerTotal", HardSoftScore.ONE_HARD )
            };
        } else {
            return constraintFactory -> new Constraint[]{
                    constraintFactory.fromUnfiltered(CloudComputer.class)
                            .join(constraintFactory.fromUnfiltered(CloudProcess.class))
                            .filter((computer, process) -> org.drools.modelcompiler.util.EvaluationUtil.areNullSafeEquals(computer, process.getComputer()))
                    .penalize("requiredCpuPowerTotal", HardSoftScore.ONE_HARD)
            };
        }
    }

    private static ConstraintStreamScoreDirectorFactory<CloudBalance, ?> getCSFactory(ConstraintProvider constraintProvider, ConstraintStreamImplType csType) {
        return new ConstraintStreamScoreDirectorFactory<>( MyBenchmark.SOLUTION_DESCRIPTOR, constraintProvider, csType);
    }
}
