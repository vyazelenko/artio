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
package uk.co.real_logic.fix_gateway.system_tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.co.real_logic.aeron.driver.MediaDriver;
import uk.co.real_logic.fix_gateway.FixGateway;
import uk.co.real_logic.fix_gateway.SessionConfiguration;
import uk.co.real_logic.fix_gateway.StaticConfiguration;
import uk.co.real_logic.fix_gateway.framer.session.InitiatorSession;

import static org.junit.Assert.assertTrue;
import static uk.co.real_logic.aeron.driver.ThreadingMode.SHARED;
import static uk.co.real_logic.fix_gateway.TestFixtures.unusedPort;
import static uk.co.real_logic.fix_gateway.framer.session.SessionState.ACTIVE;

public class GatewayIntegrationTest
{

    private MediaDriver mediaDriver;
    private FixGateway acceptingGateway;
    private FixGateway initiatingGateway;
    private InitiatorSession session;

    @Before
    public void launch()
    {
        final int port = unusedPort();

        mediaDriver = MediaDriver.launch(new MediaDriver.Context().threadingMode(SHARED));

        final StaticConfiguration acceptingConfig = new StaticConfiguration()
                .registerFallbackAcceptor(new FakeOtfAcceptor())
                .bind("localhost", port);
        acceptingGateway = FixGateway.launch(acceptingConfig);

        final StaticConfiguration initiatingConfig = new StaticConfiguration()
                .bind("localhost", unusedPort());
        initiatingGateway = FixGateway.launch(initiatingConfig);

        final SessionConfiguration config = SessionConfiguration.builder()
                .address("localhost", port)
                .credentials("bob", "Uv1aegoh")
                .senderCompId("LEH_LZJ02")
                .targetCompId("CCG")
                .build();
        session = initiatingGateway.initiate(config, null);
    }

    @Test
    public void sessionHasBeenInitiated() throws InterruptedException
    {
        assertTrue("Session has failed to connect", session.isConnected());
        assertTrue("Session has failed to logon", session.state() == ACTIVE);
    }

    @After
    public void close() throws Exception
    {
        if (acceptingGateway != null)
        {
            acceptingGateway.close();
        }

        if (initiatingGateway != null)
        {
            initiatingGateway.close();
        }

        if (mediaDriver != null)
        {
            mediaDriver.close();
        }
    }

}
