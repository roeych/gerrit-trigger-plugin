/*
 * The MIT License
 *
 * Copyright 2013 Ericsson
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

package com.sonyericsson.hudson.plugins.gerrit.trigger;

//CS IGNORE LineLength FOR NEXT 1 LINES. REASON: static import.
import static com.sonyericsson.hudson.plugins.gerrit.gerritevents.GerritDefaultValues.DEFAULT_NR_OF_RECEIVING_WORKER_THREADS;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import com.sonyericsson.hudson.plugins.gerrit.gerritevents.GerritEventListener;
import com.sonyericsson.hudson.plugins.gerrit.gerritevents.dto.events.PatchsetCreated;
import com.sonyericsson.hudson.plugins.gerrit.trigger.events.ManualPatchsetCreated;
import com.sonyericsson.hudson.plugins.gerrit.trigger.events.lifecycle.GerritEventLifecycleListener;
import com.sonyericsson.hudson.plugins.gerrit.trigger.mock.Setup;

/**
 * Tests for {@link GerritHandlerLifecycle}.
 *
 * @author Hugo Arès &lt;hugo.ares@ericsson.com&gt;
 */
public class GerritHandlerLifecycleTest {

    private GerritHandlerLifecycle gerritHandler;

    /**
     * Creates a GerritHandlerLifecycle.
     */
    @Before
    public void setUp() {
        gerritHandler = new GerritHandlerLifecycle(DEFAULT_NR_OF_RECEIVING_WORKER_THREADS);
    }

    /**
     * Shuts down the GerritHandlerLifecycle.
     */
    @After
    public void shutDown() {
        if (gerritHandler != null) {
            gerritHandler.shutdown(true);
        }
        gerritHandler = null;
    }

    /**
     * Tests that gerritHandleLifecycle Notifies GerritEventListener.
     */
    @Test
    public void shouldNotifyGerritEventListener() {
        GerritEventListener eventListenerMock = mock(GerritEventListener.class);
        gerritHandler.addListener(eventListenerMock);
        PatchsetCreated patchset = Setup.createPatchsetCreated();

        gerritHandler.notifyListeners(patchset);

        verify(eventListenerMock).gerritEvent(patchset);
    }

    /**
     * Tests that gerritHandleLifecycle Notifies GerritEventLifecycleListener.
     */
    @Test
    public void shouldNotifyLifecycleListener() {
        GerritEventLifecycleListener lifecycleListenerMock = mock(GerritEventLifecycleListener.class);
        ManualPatchsetCreated manualPatchset = Setup.createManualPatchsetCreated();
        manualPatchset.addListener(lifecycleListenerMock);

        gerritHandler.notifyListeners(manualPatchset);

        InOrder inOrder = inOrder(lifecycleListenerMock);
        inOrder.verify(lifecycleListenerMock).triggerScanStarting(manualPatchset);
        inOrder.verify(lifecycleListenerMock).triggerScanDone(manualPatchset);
    }

}
