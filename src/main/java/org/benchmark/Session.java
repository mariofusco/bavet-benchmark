package org.benchmark;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

public interface Session {

    int insert(Object object);

    int update(Object object);

    HardSoftScore calculateScore();

    void close();
}
