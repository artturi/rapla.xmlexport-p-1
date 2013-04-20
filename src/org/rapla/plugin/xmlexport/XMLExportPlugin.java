/*--------------------------------------------------------------------------*
 | Copyright (C) 2006 Christopher Kohlhaas                                  |
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
package org.rapla.plugin.xmlexport;
import org.rapla.framework.Configuration;
import org.rapla.framework.Container;
import org.rapla.framework.PluginDescriptor;
import org.rapla.framework.RaplaContextException;
import org.rapla.plugin.RaplaServerExtensionPoints;
import org.rapla.server.ServerService;
import org.rapla.server.ServerServiceContainer;
import org.rapla.servletpages.DefaultHTMLMenuEntry;
import org.rapla.servletpages.HTMLMenuExtensionPoint;
///import org.rapla.servletpages.RaplaResourcePageGenerator;


/**
   This is a demonstration of a rapla-plugin. It adds a sample usecase and option
   to the rapla-system.
 */

public class XMLExportPlugin
    implements
    PluginDescriptor
{

    public static final String PLUGIN_CLASS = XMLExportPlugin.class.getName();

    public String toString() {
        return "XML Export";
    }
    
    /**
     * @throws RaplaContextException 
     * @see org.rapla.framework.PluginDescriptor#provideServices(org.rapla.framework.general.Container)
     */
    public void provideServices(Container container, Configuration config) throws RaplaContextException  {
        if ( !config.getAttributeAsBoolean("enabled", false) )
        	return;

        if ( container.getContext().has(ServerService.class) ) 
        {
        	ServerServiceContainer serviceContainer = container.getContext().lookup( ServerServiceContainer.class);
            serviceContainer.addWebpage("xmlexport",XMLPageGenerator.class);
            
            HTMLMenuExtensionPoint mainMenu = container.getContext().lookup( RaplaServerExtensionPoints.HTML_MAIN_MENU_EXTENSION_POINT );
            mainMenu.insert( new DefaultHTMLMenuEntry("XML-EXPORT","rapla?page=xmlexport"));
        }        
    }

    public Object getPluginMetaInfos( String key )
    {
        return null;
    }

}

