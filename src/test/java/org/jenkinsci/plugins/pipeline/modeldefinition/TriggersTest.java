/*
 * The MIT License
 *
 * Copyright (c) 2016, CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.jenkinsci.plugins.pipeline.modeldefinition;

import hudson.triggers.TimerTrigger;
import hudson.triggers.Trigger;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.jenkinsci.plugins.workflow.job.properties.PipelineTriggersJobProperty;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TriggersTest extends AbstractModelDefTest {
    @Test
    public void simpleTriggers() throws Exception {
        prepRepoWithJenkinsfile("simpleTriggers");

        WorkflowRun b = getAndStartBuild();
        j.assertBuildStatusSuccess(j.waitForCompletion(b));
        j.assertLogContains("[Pipeline] { (foo)", b);
        j.assertLogNotContains("[Pipeline] { (Post Build Actions)", b);
        j.assertLogNotContains("[Pipeline] { (Notifications)", b);
        j.assertLogContains("hello", b);

        WorkflowJob p = b.getParent();

        PipelineTriggersJobProperty triggersJobProperty = p.getTriggersJobProperty();
        assertNotNull(triggersJobProperty);
        assertEquals(1, triggersJobProperty.getTriggers().size());
        TimerTrigger.DescriptorImpl timerDesc = j.jenkins.getDescriptorByType(TimerTrigger.DescriptorImpl.class);

        Trigger trigger = triggersJobProperty.getTriggerForDescriptor(timerDesc);
        assertNotNull(trigger);

        assertTrue(trigger instanceof TimerTrigger);
        TimerTrigger timer = (TimerTrigger) trigger;
        assertEquals("@daily", timer.getSpec());
    }

}
