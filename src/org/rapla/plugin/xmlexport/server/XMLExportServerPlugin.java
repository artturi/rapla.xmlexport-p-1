/*--------------------------------------------------------------------------*
 | Copyright (C) 2013 Christopher Kohlhaas                                  |
 |                                                                          |
 | This program is free software; you can redistribute it and/or modify     |
 | it under the terms of the GNU General Public License as published by the |
 | Free Software Foundation. A copy of the license has been included with   |
 | these distribution in the COPYING file, if not go to www.fsf.org         |
 |                                                                          |
 | As a special exception, you are granted the permissions to link this     |
 | program with every library, which license fulfills the Open Source       |
 | Definition as published by the Open Source Initiative (OSI).             |
 *--------------------------------------------------------------------------*/
package org.rapla.plugin.xmlexport.server;
import org.rapla.framework.Configuration;
import org.rapla.framework.PluginDescriptor;
import org.rapla.framework.RaplaContextException;
import org.rapla.server.RaplaServerExtensionPoints;
import org.rapla.server.ServerServiceContainer;


public class XMLExportServerPlugin
    implements
    PluginDescriptor<ServerServiceContainer>
{


    public void provideServices(ServerServiceContainer container, Configuration config) throws RaplaContextException  {
        if ( !config.getAttributeAsBoolean("enabled", false) )
        	return;

        container.addWebpage("xmlexport",XMLPageGenerator.class);
        container.addContainerProvidedComponent(RaplaServerExtensionPoints.HTML_MAIN_MENU_EXTENSION_POINT, XMLExportMenu.class);
    }


}

