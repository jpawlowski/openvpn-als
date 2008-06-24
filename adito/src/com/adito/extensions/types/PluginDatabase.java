package com.adito.extensions.types;

import com.adito.core.CoreServlet;
import com.adito.core.Database;

public interface PluginDatabase extends Database {

    /**
     * Open and initialise the database.
     * 
     * @param controllingServlet controlling servlet
     * @param def the plugin definition
     * @throws Exception on any error
     */
    public void open(CoreServlet controllingServlet, PluginDefinition def) throws Exception;

}
