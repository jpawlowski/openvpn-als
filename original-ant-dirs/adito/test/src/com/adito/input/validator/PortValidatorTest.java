package com.adito.input.validator;

import java.io.IOException;
import java.net.ServerSocket;

import org.jdom.JDOMException;
import org.junit.BeforeClass;
import org.junit.Test;

import com.adito.boot.CodedException;
import com.adito.boot.ContextConfig;
import com.adito.boot.PropertyDefinition;
import com.adito.core.CoreException;
import com.adito.input.validators.PortValidator;
import com.adito.testcontainer.AbstractTest;

/**
 * Test whether the port is in use or not.
 */
public class PortValidatorTest extends AbstractTest {

    /**
     * @throws Exception
     */
    @BeforeClass
    public static void oneTimeSetUp() throws Exception {
        setUp("");
    }

    /**
     * @throws IOException
     * @throws JDOMException
     * @throws CodedException
     */
    @Test
    public void validPortWithNoMetaData() throws IOException, JDOMException, CodedException {
        PropertyDefinition definition = getWebServerPortDefinition();
        definition.setTypeMeta("");
        PortValidator validator = new PortValidator();
        validator.validate(definition, "69", null);
    }

    /**
     * @throws IOException
     * @throws JDOMException
     * @throws CodedException
     */
    @Test(expected = CoreException.class)
    public void invalidPortWithNoMetaData() throws IOException, JDOMException, CodedException {
        PropertyDefinition definition = getWebServerPortDefinition();
        definition.setTypeMeta("");
        PortValidator validator = new PortValidator();
        validator.validate(definition, "99999999", null);
    }

    /**
     * @throws CodedException
     * @throws IOException
     * @throws JDOMException
     */
    @Test
    public void validPortSocketNotInUse() throws CodedException, IOException, JDOMException {
        PropertyDefinition definition = getWebServerPortDefinition();
        assertPortNotInUse(69);
        PortValidator validator = new PortValidator();
        validator.validate(definition, "69", null);
    }

    /**
     * @throws CodedException
     * @throws IOException
     * @throws JDOMException
     */
    @Test(expected = CoreException.class)
    public void validPortSocketInUse() throws CodedException, IOException, JDOMException {
        PropertyDefinition definition = getWebServerPortDefinition();
        assertPortNotInUse(69);

        ServerSocket socket = new ServerSocket(69);
        try {
            PortValidator validator = new PortValidator();
            validator.validate(definition, "69", null);
        } finally {
            socket.close();
        }
    }

    private PropertyDefinition getWebServerPortDefinition() throws IOException, JDOMException {
        ContextConfig config = new ContextConfig(getClass().getClassLoader());
        return config.getDefinition("webServer.port");
    }

    private void assertPortNotInUse(int port) throws IOException {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(port);
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }
}