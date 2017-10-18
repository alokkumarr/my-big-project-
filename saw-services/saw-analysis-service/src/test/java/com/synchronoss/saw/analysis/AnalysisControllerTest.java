package com.synchronoss.saw.analysis;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class AnalysisControllerTest {
    @Test
    public void testHealth() {
        AnalysisController analysis = new AnalysisController();
        assertThat(analysis.health()).isEqualTo("OK");
    }
}
