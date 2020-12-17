/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.benchmark.groupby;

import org.drools.model.Drools;
import org.drools.model.Global;
import org.drools.model.Variable;
import org.drools.modelcompiler.dsl.pattern.D;
import org.openjdk.jmh.infra.Blackhole;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScoreHolder;
import org.optaplanner.examples.cloudbalancing.domain.CloudComputer;
import org.optaplanner.examples.cloudbalancing.domain.CloudProcess;

public class Main {
    public static void main( String[] args ) {
        MyBenchmark benchmark = new MyBenchmark();
        benchmark.algo = "EM-GN";
//        benchmark.algo = "CS-D";
//        benchmark.algo = "DRL";

        Blackhole blackhole = new Blackhole("Today's password is swordfish. I understand instantiating Blackholes directly is dangerous.");

        benchmark.setUp();
        benchmark.swapComputers( blackhole );
    }

    public static org.drools.model.Rule rule_requiredCpuPowerTotal() {
        final Global<HardSoftScoreHolder> var_scoreHolder = D.globalOf( HardSoftScoreHolder.class, "" );
        final Variable<CloudComputer> var_$computer = D.declarationOf(CloudComputer.class);
        final Variable<Integer> var_$cpuPower = D.declarationOf(Integer.class, "$cpuPower");
        final Variable<CloudProcess> var_GENERATED_$pattern_CloudProcess$1$ = D.declarationOf(CloudProcess.class);
        final Variable<Integer> var_$requiredCpuPower = D.declarationOf(Integer.class, "$requiredCpuPower");
        final Variable<Integer> var_$requiredCpuPowerTotal = D.declarationOf(Integer.class);

        org.drools.model.Rule rule = D.rule("requiredCpuPowerTotal")
                .build(
                        D.pattern(var_$computer).bind(var_$cpuPower, ( CloudComputer _this) -> _this.getCpuPower()),
                        D.accumulate(D.pattern(var_GENERATED_$pattern_CloudProcess$1$)
                                        .expr("a", var_$computer, ( CloudProcess _this, CloudComputer $computer) -> org.drools.modelcompiler.util.EvaluationUtil.areNullSafeEquals(_this.getComputer(), $computer),
                                D.betaIndexedBy(CloudComputer.class, org.drools.model.Index.ConstraintType.EQUAL, 1, ( CloudProcess _this) -> _this.getComputer(), ( CloudComputer $computer) -> $computer))
                                .bind(var_$requiredCpuPower, ( CloudProcess _this) -> _this.getRequiredCpuPower()),
                                D.accFunction(org.drools.core.base.accumulators.IntegerSumAccumulateFunction::new, var_$requiredCpuPower).as(var_$requiredCpuPowerTotal)),
                        D.pattern(var_$requiredCpuPowerTotal).expr("b", var_$cpuPower, ( Integer $requiredCpuPowerTotal, Integer $cpuPower) -> org.drools.modelcompiler.util.EvaluationUtil.greaterThanNumbers($requiredCpuPowerTotal, $cpuPower)),
                        D.on(var_$cpuPower, var_$requiredCpuPowerTotal, var_scoreHolder).execute(( Drools drools, Integer $cpuPower, Integer $requiredCpuPowerTotal, HardSoftScoreHolder scoreHolder) -> {
                            {
                                scoreHolder.addHardConstraintMatch((org.kie.api.runtime.rule.RuleContext) drools, $cpuPower - $requiredCpuPowerTotal);
                            }
        }));
        return rule;
    }

    public static org.drools.model.Rule rule_requiredCpuPowerTotal_GroupBy() {
        final Global<HardSoftScoreHolder> var_scoreHolder = D.globalOf( HardSoftScoreHolder.class, "" );
        final Variable<CloudComputer> var_$computer = D.declarationOf(CloudComputer.class);
        final Variable<Integer> var_$cpuPower = D.declarationOf(Integer.class, "$cpuPower");
        final Variable<CloudProcess> var_$process = D.declarationOf(CloudProcess.class);
        final Variable<Integer> var_$requiredCpuPower = D.declarationOf(Integer.class, "$requiredCpuPower");
        final Variable<Integer> var_$requiredCpuPowerTotal = D.declarationOf(Integer.class);

        org.drools.model.Rule rule = D.rule("requiredCpuPowerTotal")
                .build(
                        D.groupBy(
                                D.pattern(var_$process).bind(var_$requiredCpuPower, ( CloudProcess _this) -> _this.getRequiredCpuPower()),
                                var_$process, var_$computer, CloudProcess::getComputer,
                                D.accFunction(org.drools.core.base.accumulators.IntegerSumAccumulateFunction::new, var_$requiredCpuPower).as(var_$requiredCpuPowerTotal)
                        ),
                        D.pattern(var_$requiredCpuPowerTotal).expr("b", var_$cpuPower, ( Integer $requiredCpuPowerTotal, Integer $cpuPower) -> org.drools.modelcompiler.util.EvaluationUtil.greaterThanNumbers($requiredCpuPowerTotal, $cpuPower)),
                        D.on(var_$cpuPower, var_$requiredCpuPowerTotal, var_scoreHolder).execute(( Drools drools, Integer $cpuPower, Integer $requiredCpuPowerTotal, HardSoftScoreHolder scoreHolder) -> {
                            {
                                scoreHolder.addHardConstraintMatch((org.kie.api.runtime.rule.RuleContext) drools, $cpuPower - $requiredCpuPowerTotal);
                            }
        }));
        return rule;
    }
}
