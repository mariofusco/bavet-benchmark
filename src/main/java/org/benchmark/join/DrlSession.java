package org.benchmark.join;

import java.util.IdentityHashMap;
import java.util.Map;

import org.benchmark.Session;
import org.drools.modelcompiler.ExecutableModelProject;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.builder.conf.PropertySpecificOption;
import org.kie.internal.utils.KieHelper;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.director.drools.DroolsScoreDirectorFactory;
import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;

final class DrlSession implements Session {

   private final Map<Object, FactHandle> fhMap = new IdentityHashMap<>();

    private final KieSession session;

    public DrlSession(boolean indexed) {
        DroolsScoreDirectorFactory<CloudBalance, ?> SDF = new DroolsScoreDirectorFactory<>( MyBenchmark.SOLUTION_DESCRIPTOR, buildKieBase(indexed));
        session = SDF.newKieSession();
    }

    private static KieBase buildKieBase(boolean indexed) {
        return new KieHelper(PropertySpecificOption.DISABLED)
                .addContent(getDrl(indexed), ResourceType.DRL)
                .build(ExecutableModelProject.class);
    }

    @Override
    public int insert(Object object) {
//        session.insert(object);
        fhMap.put(object, session.insert(object));
        return 0;
    }

    @Override
    public int update(Object object) {
//        FactHandle handle = session.getFactHandle(object);
        FactHandle handle = fhMap.get(object);
        session.update(handle,object);
        return 1;
    }

    @Override
    public HardSoftScore calculateScore() {
        int fireCount = session.fireAllRules();
        return HardSoftScore.ofHard(fireCount);
    }

    @Override
    public void close() {
        session.dispose();
    }

    public static String getDrl(boolean indexed) {
        return "import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScoreHolder;\n" +
                "import org.optaplanner.examples.cloudbalancing.domain.CloudComputer;\n" +
                "import org.optaplanner.examples.cloudbalancing.domain.CloudProcess;\n" +
                "global HardSoftScoreHolder scoreHolder;\n" +
                "rule \"requiredCpuPowerTotal\"\n" +
                "    when\n" +
                "        $computer : CloudComputer()\n" +
                (indexed ?
                "        CloudProcess(computer == $computer)\n" :
                "        CloudProcess(org.drools.modelcompiler.util.EvaluationUtil.areNullSafeEquals(computer, $computer))\n"
                ) +
                "    then\n" +
                "        // don't do anything\n" +
                "end\n";
    }
}
