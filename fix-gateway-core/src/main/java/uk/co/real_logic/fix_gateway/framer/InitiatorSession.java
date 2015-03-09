/*
 * Copyright 2015 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.real_logic.fix_gateway.framer;

import uk.co.real_logic.fix_gateway.util.MilliClock;

import static uk.co.real_logic.fix_gateway.framer.SessionState.ACTIVE;
import static uk.co.real_logic.fix_gateway.framer.SessionState.CONNECTED;
import static uk.co.real_logic.fix_gateway.framer.SessionState.CONNECTING;

public final class InitiatorSession extends Session
{
    public InitiatorSession(
        final int heartbeatInterval,
        final long connectionId,
        final MilliClock clock,
        final SessionProxy proxy)
    {
        super(heartbeatInterval, connectionId, clock, CONNECTING, proxy);
    }

    public void onLogon(final int heartbeatInterval, final int msgSeqNo, final long sessionId)
    {
        if (msgSeqNo == expectedSeqNo())
        {
            state(ACTIVE);
        }
    }

    public void connected()
    {
        state(CONNECTED);
    }
}
